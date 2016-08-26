package com.hartwig.healthchecks.nesbit.extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.reader.ExtensionFinderAndLineReader;
import com.hartwig.healthchecks.common.predicate.VCFPassDataLinePredicate;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.PatientMultiChecksReport;
import com.hartwig.healthchecks.nesbit.model.VCFSomaticData;
import com.hartwig.healthchecks.nesbit.model.VCFSomaticSetData;
import com.hartwig.healthchecks.nesbit.model.VCFType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class SomaticExtractor extends AbstractVCFExtractor {

    private static final Logger LOGGER = LogManager.getLogger(SomaticExtractor.class);

    private static final String MELTED_SOMATICS_EXTENSION = "_Cosmicv76_melted.vcf";
    private static final String VCF_COLUMN_SEPARATOR = "\t";

    @VisibleForTesting
    static final String MUTECT = "mutect";
    @VisibleForTesting
    static final String VARSCAN = "varscan";
    @VisibleForTesting
    static final String STRELKA = "strelka";
    @VisibleForTesting
    static final String FREEBAYES = "freebayes";

    private static final List<String> CALLERS = Arrays.asList(MUTECT, VARSCAN, STRELKA, FREEBAYES);
    private static final List<Integer> CALLERS_COUNT = Arrays.asList(1, 2, 3, 4);

    private static final String VCF_INFO_FIELD_SEPARATOR = ";";
    private static final String CALLER_ALGO_IDENTIFIER = "set=";
    private static final String CALLER_ALGO_START = "=";
    private static final String CALLER_ALGO_SEPARATOR = "-";
    private static final String CALLER_FILTERED_IDENTIFIER = "filterIn";

    @NotNull
    private final ExtensionFinderAndLineReader reader = ExtensionFinderAndLineReader.build();
    @NotNull
    private final RunContext runContext;

    public SomaticExtractor(@NotNull final RunContext runContext) {
        this.runContext = runContext;
    }

    @NotNull
    @Override
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<String> lines = reader.readLines(runDirectory, MELTED_SOMATICS_EXTENSION,
                new VCFPassDataLinePredicate());
        final List<VCFSomaticData> vcfData = getVCFSomaticData(lines);

        final List<BaseDataReport> reports = new ArrayList<>();
        reports.addAll(getTypeChecks(vcfData, runContext.tumorSample(), VCFType.SNP));
        reports.addAll(getTypeChecks(vcfData, runContext.tumorSample(), VCFType.INDELS));

        BaseDataReport.log(LOGGER, reports);
        return new PatientMultiChecksReport(CheckType.SOMATIC, reports);
    }

    @NotNull
    private static List<VCFSomaticData> getVCFSomaticData(@NotNull final List<String> lines) {
        return lines.stream().map(line -> {
            final String[] values = line.split(VCF_COLUMN_SEPARATOR);
            final VCFType type = getVCFType(values[REF_INDEX], values[ALT_INDEX]);
            final String info = values[INFO_INDEX];
            return new VCFSomaticData(type, info);
        }).filter(vcfData -> vcfData != null).collect(Collectors.toList());
    }

    @NotNull
    private static List<BaseDataReport> getTypeChecks(@NotNull final List<VCFSomaticData> vcfData,
            @NotNull final String sampleId, @NotNull final VCFType vcfType) {
        final BaseDataReport countReport = getSomaticVariantCount(sampleId, vcfData, vcfType,
                SomaticCheck.SOMATIC_COUNT.checkName(vcfType.name()));
        final List<BaseDataReport> reports = new ArrayList<>();
        reports.add(countReport);
        final List<VCFSomaticSetData> vcfTypeSetData = getSetDataForType(vcfData, vcfType);
        final List<BaseDataReport> precisionReports = CALLERS.stream().map(
                caller -> calculatePrecision(vcfTypeSetData, sampleId, vcfType, caller)).collect(Collectors.toList());
        reports.addAll(precisionReports);

        final List<BaseDataReport> sensitivityReports = CALLERS.stream().map(
                caller -> calculateSensitivity(vcfTypeSetData, sampleId, vcfType, caller)).collect(
                Collectors.toList());
        reports.addAll(sensitivityReports);

        final List<BaseDataReport> proportionReports = CALLERS_COUNT.stream().map(
                callerCount -> calculateProportion(vcfTypeSetData, sampleId, vcfType, callerCount)).collect(
                Collectors.toList());
        reports.addAll(proportionReports);
        return reports;
    }

    @NotNull
    private static BaseDataReport getSomaticVariantCount(@NotNull final String sampleId,
            @NotNull final List<VCFSomaticData> vcfData, final VCFType vcfType, final String checkName) {
        final Long count = vcfData.stream().filter(data -> data.getType().equals(vcfType)).count();
        return new BaseDataReport(sampleId, checkName, String.valueOf(count));
    }

    @NotNull
    private static List<VCFSomaticSetData> getSetDataForType(@NotNull final List<VCFSomaticData> vcfData,
            @NotNull final VCFType vcfType) {
        return vcfData.stream().filter(vcf -> vcf.getType().equals(vcfType)).map(vcf -> {
            VCFSomaticSetData vcfSomaticSetData = null;
            final Optional<String> setValue = Arrays.stream(vcf.getInfo().split(VCF_INFO_FIELD_SEPARATOR)).filter(
                    infoLine -> infoLine.contains(CALLER_ALGO_IDENTIFIER)).map(
                    infoLine -> infoLine.substring(infoLine.indexOf(CALLER_ALGO_START) + 1,
                            infoLine.length())).findFirst();
            assert setValue.isPresent();
            final String[] allCallers = setValue.get().split(CALLER_ALGO_SEPARATOR);
            final List<String> filteredCallers = Arrays.stream(allCallers).filter(
                    caller -> !caller.startsWith(CALLER_FILTERED_IDENTIFIER)).collect(Collectors.toList());
            if (filteredCallers.size() > 0) {
                final Map<String, Integer> callersMap = filteredCallers.stream().collect(
                        Collectors.toMap(key -> key, value -> filteredCallers.size() - 1));
                vcfSomaticSetData = new VCFSomaticSetData(filteredCallers.size(), callersMap);
            }
            return vcfSomaticSetData;
        }).filter(vcfSomaticSetData -> vcfSomaticSetData != null).collect(Collectors.toList());
    }

    @NotNull
    private static BaseDataReport calculatePrecision(@NotNull final List<VCFSomaticSetData> vcfSomaticSetData,
            @NotNull final String sampleId, @NotNull final VCFType vcfType, @NotNull final String caller) {
        final List<VCFSomaticSetData> callerSets = getSetsForCaller(vcfSomaticSetData, caller);
        final List<VCFSomaticSetData> callerSetsPerCallersCount = getSetForCallerWithMoreThanOneCaller(callerSets,
                caller);
        double precision = 0D;
        if (!callerSetsPerCallersCount.isEmpty() && !callerSets.isEmpty()) {
            precision = (double) callerSetsPerCallersCount.size() / callerSets.size();
        }
        return new BaseDataReport(sampleId,
                SomaticCheck.PRECISION_CHECK.checkName(vcfType.name(), caller.toUpperCase(Locale.ENGLISH)),
                String.valueOf(precision));
    }

    @NotNull
    private static BaseDataReport calculateSensitivity(@NotNull final List<VCFSomaticSetData> vcfSomaticSetData,
            @NotNull final String sampleId, @NotNull final VCFType vcfType, @NotNull final String caller) {
        final List<VCFSomaticSetData> callerSetsPerCallersCount = getSetForCallerWithMoreThanOneCaller(
                vcfSomaticSetData, caller);
        final List<VCFSomaticSetData> setsPerCount = getSetsFilteredByCount(vcfSomaticSetData,
                isTotalCallersCountMoreThan(1));
        double sensitivity = 0D;
        if (!callerSetsPerCallersCount.isEmpty() && !setsPerCount.isEmpty()) {
            sensitivity = (double) callerSetsPerCallersCount.size() / setsPerCount.size();
        }
        return new BaseDataReport(sampleId,
                SomaticCheck.SENSITIVITY_CHECK.checkName(vcfType.name(), caller.toUpperCase(Locale.ENGLISH)),
                String.valueOf(sensitivity));
    }

    @NotNull
    private static BaseDataReport calculateProportion(@NotNull final List<VCFSomaticSetData> vcfSomaticSetData,
            @NotNull final String sampleId, @NotNull final VCFType vcfType, final int count) {
        final List<VCFSomaticSetData> setsPerCount = getSetsFilteredByCount(vcfSomaticSetData,
                isTotalCallersCountEqual(count));
        double proportion = 0D;
        if (!setsPerCount.isEmpty() && !vcfSomaticSetData.isEmpty()) {
            proportion = (double) setsPerCount.size() / vcfSomaticSetData.size();
        }

        return new BaseDataReport(sampleId,
                SomaticCheck.PROPORTION_CHECK.checkName(vcfType.name(), String.valueOf(count)),
                String.valueOf(proportion));
    }

    @NotNull
    private static List<VCFSomaticSetData> getSetForCallerWithMoreThanOneCaller(
            @NotNull final List<VCFSomaticSetData> vcfSomaticSetData, @NotNull final String caller) {
        final List<VCFSomaticSetData> callerSets = getSetsForCaller(vcfSomaticSetData, caller);
        return callerSets.stream().filter(
                vcfSomaticSet -> vcfSomaticSet.getCallersCountPerCaller().get(caller) > 1).collect(
                Collectors.toList());
    }

    @NotNull
    private static List<VCFSomaticSetData> getSetsForCaller(@NotNull final List<VCFSomaticSetData> vcfSomaticSetData,
            @NotNull final String caller) {
        return vcfSomaticSetData.stream().filter(
                vcfSomaticSet -> vcfSomaticSet.getCallersCountPerCaller().containsKey(caller)).collect(
                Collectors.toList());
    }

    @NotNull
    private static List<VCFSomaticSetData> getSetsFilteredByCount(
            @NotNull final List<VCFSomaticSetData> vcfSomaticSetData,
            @NotNull final Predicate<VCFSomaticSetData> countFilter) {
        return vcfSomaticSetData.stream().filter(countFilter).collect(Collectors.toList());
    }

    @NotNull
    private static Predicate<VCFSomaticSetData> isTotalCallersCountMoreThan(final int count) {
        return vcfSomaticSet -> vcfSomaticSet.getTotalCallerCount() > count;
    }

    @NotNull
    private static Predicate<VCFSomaticSetData> isTotalCallersCountEqual(final int count) {
        return vcfSomaticSet -> vcfSomaticSet.getTotalCallerCount() == count;
    }
}
