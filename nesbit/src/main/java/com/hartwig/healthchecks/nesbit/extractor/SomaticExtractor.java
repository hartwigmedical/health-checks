package com.hartwig.healthchecks.nesbit.extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.reader.FilteredReader;
import com.hartwig.healthchecks.common.predicate.VCFHeaderLinePredicate;
import com.hartwig.healthchecks.common.predicate.VCFPassDataLinePredicate;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.PatientMultiChecksReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.nesbit.model.VCFData;
import com.hartwig.healthchecks.nesbit.model.VCFSomaticSetData;
import com.hartwig.healthchecks.nesbit.model.VCFType;

public class SomaticExtractor extends AbstractVCFExtractor {

    private static final String SET = "set=";

    private static final String PROPORTION_CHECK_LABEL = "SOMATIC_%s_PROPORTION_VARIANTS_%s_CALLERS";

    private static final String SENSITIVITY_CHECK_LABEL = "SOMATIC_%s_SENSITIVITY_%s_VARIANTS_2+_CALLERS";

    private static final String SOMATIC_TYPE_LABEL = "VARIANTS_SOMATIC_%s";

    private static final String PRECISION_CHECK_LABEL = "SOMATIC_%s_PRECISION_%s_2+_CALLERS";

    private static final List<Integer> CALLERS_COUNT = Arrays.asList(1, 2, 3);

    private static final List<String> CALLERS = Arrays.asList("mutect", "varscan", "strelka", "freebayes");

    private static final String FILTER_IN = "filterIn";

    private static final String EXT = "_Cosmicv76_melted.vcf";

    private final FilteredReader reader;

    public SomaticExtractor(final FilteredReader reader) {
        super();
        this.reader = reader;
    }

    @Override
    public BaseReport extractFromRunDirectory(final String runDirectory) throws IOException, HealthChecksException {
        final List<BaseDataReport> patientData = getPatientData(runDirectory);
        return new PatientMultiChecksReport(CheckType.SOMATIC, patientData);
    }

    private List<BaseDataReport> getPatientData(final String runDirectory) throws IOException, HealthChecksException {
        final List<String> headerLines = reader.readLines(runDirectory, EXT, new VCFHeaderLinePredicate());
        final String[] headers = getHeaders(headerLines, EXT, Boolean.FALSE);
        final String patientId = getPatientIdFromHeader(headers, TUM_SAMPLE_SUFFIX);

        final List<String> lines = reader.readLines(runDirectory, EXT, new VCFPassDataLinePredicate());
        final List<VCFData> vcfData = getVCFData(lines);

        final List<BaseDataReport> reports = new ArrayList<>();
        reports.addAll(getTypeChecks(vcfData, patientId, VCFType.SNP));
        reports.addAll(getTypeChecks(vcfData, patientId, VCFType.INDELS));
        logBaseDataReports(reports);
        return reports;
    }

    private List<BaseDataReport> getTypeChecks(final List<VCFData> vcfData, final String patientId,
                    final VCFType vcfType) {
        final BaseDataReport countReport = getCountCheck(patientId, vcfData, vcfType,
                        String.format(SOMATIC_TYPE_LABEL, vcfType.name()));
        final List<BaseDataReport> reports = new ArrayList<>();
        reports.add(countReport);
        final List<VCFSomaticSetData> vcfTypeSetData = getSetDataForType(vcfData, vcfType);
        final List<BaseDataReport> precisionReports = CALLERS.stream().map(caller -> {
            return calculatePrecision(vcfTypeSetData, patientId, vcfType, caller);
        }).collect(Collectors.toList());
        reports.addAll(precisionReports);

        final List<BaseDataReport> sensitivityReports = CALLERS.stream().map(caller -> {
            return calculateSensitivity(vcfTypeSetData, patientId, vcfType, caller);
        }).collect(Collectors.toList());
        reports.addAll(sensitivityReports);

        final List<BaseDataReport> proportionReports = CALLERS_COUNT.stream().map(callerCount -> {
            return calculateProportion(vcfTypeSetData, patientId, vcfType, callerCount);
        }).collect(Collectors.toList());
        reports.addAll(proportionReports);
        return reports;
    }

    private List<VCFSomaticSetData> getSetDataForType(final List<VCFData> vcfData, final VCFType vcfType) {
        return vcfData.stream().filter(vcf -> vcf.getType().equals(vcfType)).map(vcf -> {
            VCFSomaticSetData vcfSomaticSetData = null;
            final String setValue = Arrays.stream(vcf.getInfo().split(SEMICOLON_DELIMITER))
                            .filter(infoLine -> infoLine.contains(SET))
                            .map(infoLine -> infoLine.substring(infoLine.indexOf(EQUAL) + ONE, infoLine.length()))
                            .findFirst().get();
            final String[] allCallers = setValue.split(DASH);
            final List<String> filteredCallers = Arrays.stream(allCallers)
                            .filter(caller -> !caller.startsWith(FILTER_IN)).collect(Collectors.toList());
            if (filteredCallers.size() > ZERO) {
                final Map<String, Integer> callersMap = filteredCallers.stream()
                                .collect(Collectors.toMap(key -> key, value -> filteredCallers.size() - ONE));
                vcfSomaticSetData = new VCFSomaticSetData(filteredCallers.size(), callersMap);
            }
            return vcfSomaticSetData;
        }).filter(vcfSomaticSetData -> vcfSomaticSetData != null).collect(Collectors.toList());
    }

    private BaseDataReport calculatePrecision(final List<VCFSomaticSetData> vcfSomaticSetData, final String patientId,
                    final VCFType vcfType, final String caller) {
        final List<VCFSomaticSetData> callerSets = getSetsForCaller(vcfSomaticSetData, caller);
        final List<VCFSomaticSetData> callerSetsPerCallersCount = getSetForCallerWithMoreThanOneCaller(callerSets,
                        caller);
        final double precision = (double) callerSetsPerCallersCount.size() / callerSets.size();
        return new BaseDataReport(patientId,
                        String.format(PRECISION_CHECK_LABEL, vcfType.name(), caller.toUpperCase(Locale.ENGLISH)),
                        String.valueOf(precision));
    }

    private BaseDataReport calculateSensitivity(final List<VCFSomaticSetData> vcfSomaticSetData, final String patientId,
                    final VCFType vcfType, final String caller) {
        final List<VCFSomaticSetData> callerSetsPerCallersCount = getSetForCallerWithMoreThanOneCaller(
                        vcfSomaticSetData, caller);
        final List<VCFSomaticSetData> setsPerCount = getSetsFilteredByCount(vcfSomaticSetData,
                        isTotalCallersCountMoreThan(ONE));
        final double sensitivity = (double) callerSetsPerCallersCount.size() / setsPerCount.size();
        return new BaseDataReport(patientId,
                        String.format(SENSITIVITY_CHECK_LABEL, vcfType.name(), caller.toUpperCase(Locale.ENGLISH)),
                        String.valueOf(sensitivity));
    }

    private BaseDataReport calculateProportion(final List<VCFSomaticSetData> vcfSomaticSetData, final String patientId,
                    final VCFType vcfType, final int count) {
        final List<VCFSomaticSetData> setsPerCount = getSetsFilteredByCount(vcfSomaticSetData,
                        isTotalCallersCountEqual(count));
        final double proportion = (double) setsPerCount.size() / vcfSomaticSetData.size();
        return new BaseDataReport(patientId,
                        String.format(PROPORTION_CHECK_LABEL, vcfType.name(), String.valueOf(count)),
                        String.valueOf(proportion));
    }

    private List<VCFSomaticSetData> getSetForCallerWithMoreThanOneCaller(
                    final List<VCFSomaticSetData> vcfSomaticSetData, final String caller) {
        final List<VCFSomaticSetData> callerSets = getSetsForCaller(vcfSomaticSetData, caller);
        return callerSets.stream().filter(vcfSomaticSet -> vcfSomaticSet.getCallersCountPerCaller().get(caller) > ONE)
                        .collect(Collectors.toList());
    }

    private List<VCFSomaticSetData> getSetsForCaller(final List<VCFSomaticSetData> vcfSomaticSetData,
                    final String caller) {
        return vcfSomaticSetData.stream()
                        .filter(vcfSomaticSet -> vcfSomaticSet.getCallersCountPerCaller().containsKey(caller))
                        .collect(Collectors.toList());
    }

    private List<VCFSomaticSetData> getSetsFilteredByCount(final List<VCFSomaticSetData> vcfSomaticSetData,
                    final Predicate<VCFSomaticSetData> countFilter) {
        return vcfSomaticSetData.stream().filter(countFilter).collect(Collectors.toList());
    }

    private static Predicate<VCFSomaticSetData> isTotalCallersCountMoreThan(final int count) {
        return vcfSomaticSet -> vcfSomaticSet.getTotalCallerCount() > count;
    }

    private static Predicate<VCFSomaticSetData> isTotalCallersCountEqual(final int count) {
        return vcfSomaticSet -> vcfSomaticSet.getTotalCallerCount() == count;
    }

}
