package com.hartwig.healthchecks.nesbit.check;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.path.PathExtensionFinder;
import com.hartwig.healthchecks.common.io.reader.LineReader;
import com.hartwig.healthchecks.common.predicate.VCFPassDataLinePredicate;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.MultiValueResult;
import com.hartwig.healthchecks.nesbit.model.VCFSomaticData;
import com.hartwig.healthchecks.nesbit.model.VCFSomaticDataFactory;
import com.hartwig.healthchecks.nesbit.model.VCFType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("WeakerAccess")
@ResourceWrapper(type = CheckType.SOMATIC)
public class SomaticChecker implements HealthChecker {

    private static final Logger LOGGER = LogManager.getLogger(SomaticChecker.class);

    private static final String MELTED_SOMATICS_EXTENSION = "_Cosmicv76_melted.vcf";

    @VisibleForTesting
    static final String MUTECT = "mutect";
    @VisibleForTesting
    static final String VARSCAN = "varscan";
    @VisibleForTesting
    static final String STRELKA = "strelka";
    @VisibleForTesting
    static final String FREEBAYES = "freebayes";

    private static final List<String> ALL_CALLERS = Arrays.asList(MUTECT, VARSCAN, STRELKA, FREEBAYES);
    private static final List<Integer> CALLERS_COUNT = Arrays.asList(1, 2, 3, 4);

    public SomaticChecker() {
    }

    @NotNull
    @Override
    public BaseResult run(@NotNull final RunContext runContext) throws IOException, HealthChecksException {
        final Path vcfPath = PathExtensionFinder.build().findPath(runContext.runDirectory(),
                MELTED_SOMATICS_EXTENSION);
        final List<String> lines = LineReader.build().readLines(vcfPath, new VCFPassDataLinePredicate());
        final List<VCFSomaticData> vcfData = getVCFSomaticData(lines);

        final List<HealthCheck> reports = new ArrayList<>();
        reports.addAll(getTypeChecks(vcfData, runContext.tumorSample(), VCFType.SNP));
        reports.addAll(getTypeChecks(vcfData, runContext.tumorSample(), VCFType.INDELS));
        reports.addAll(getAFChecks(vcfData, runContext.tumorSample()));

        HealthCheck.log(LOGGER, reports);
        return new MultiValueResult(checkType(), reports);
    }

    @NotNull
    @Override
    public CheckType checkType() {
        return CheckType.SOMATIC;
    }

    @NotNull
    private static List<VCFSomaticData> getVCFSomaticData(@NotNull final List<String> lines) {
        return lines.stream().map(VCFSomaticDataFactory::fromVCFLine).collect(Collectors.toList());
    }

    @NotNull
    private static List<HealthCheck> getTypeChecks(@NotNull final List<VCFSomaticData> vcfData,
            @NotNull final String sampleId, @NotNull final VCFType vcfType) {
        final HealthCheck countReport = getSomaticVariantCount(sampleId, vcfData, vcfType,
                SomaticCheck.COUNT.checkName(vcfType.name()));
        final List<HealthCheck> reports = new ArrayList<>();
        reports.add(countReport);

        final List<VCFSomaticData> filteredData = filterByVCFType(vcfData, vcfType);
        final List<HealthCheck> precisionReports = ALL_CALLERS.stream().map(
                caller -> calculatePrecision(filteredData, sampleId, vcfType, caller)).collect(Collectors.toList());
        reports.addAll(precisionReports);

        final List<HealthCheck> sensitivityReports = ALL_CALLERS.stream().map(
                caller -> calculateSensitivity(filteredData, sampleId, vcfType, caller)).collect(Collectors.toList());
        reports.addAll(sensitivityReports);

        final List<HealthCheck> proportionReports = CALLERS_COUNT.stream().map(
                callerCount -> calculateProportion(filteredData, sampleId, vcfType, callerCount)).collect(
                Collectors.toList());
        reports.addAll(proportionReports);
        return reports;
    }

    @NotNull
    private static HealthCheck getSomaticVariantCount(@NotNull final String sampleId,
            @NotNull final List<VCFSomaticData> vcfData, final VCFType vcfType, final String checkName) {
        final Long count = vcfData.stream().filter(data -> data.type().equals(vcfType)).count();
        return new HealthCheck(sampleId, checkName, String.valueOf(count));
    }

    @NotNull
    private static List<HealthCheck> getAFChecks(final List<VCFSomaticData> vcfData, final String sampleId) {
        return Lists.newArrayList();
    }

    @NotNull
    private static HealthCheck calculatePrecision(@NotNull final List<VCFSomaticData> vcfData,
            @NotNull final String sampleId, @NotNull final VCFType vcfType, @NotNull final String caller) {
        final List<VCFSomaticData> callerSets = getSetsForCaller(vcfData, caller);
        final List<VCFSomaticData> callerSetsPerCallersCount = getSetForCallerWithMoreThanOneCaller(callerSets,
                caller);
        double precision = 0D;
        if (!callerSetsPerCallersCount.isEmpty() && !callerSets.isEmpty()) {
            precision = (double) callerSetsPerCallersCount.size() / callerSets.size();
        }
        return new HealthCheck(sampleId,
                SomaticCheck.PRECISION_CHECK.checkName(vcfType.name(), caller.toUpperCase(Locale.ENGLISH)),
                String.valueOf(precision));
    }

    @NotNull
    private static HealthCheck calculateSensitivity(@NotNull final List<VCFSomaticData> vcfSomaticSetData,
            @NotNull final String sampleId, @NotNull final VCFType vcfType, @NotNull final String caller) {
        final List<VCFSomaticData> callerSetsPerCallersCount = getSetForCallerWithMoreThanOneCaller(vcfSomaticSetData,
                caller);
        final List<VCFSomaticData> setsPerCount = getSetsFilteredByCount(vcfSomaticSetData,
                isTotalCallersCountMoreThan(1));
        double sensitivity = 0D;
        if (!callerSetsPerCallersCount.isEmpty() && !setsPerCount.isEmpty()) {
            sensitivity = (double) callerSetsPerCallersCount.size() / setsPerCount.size();
        }
        return new HealthCheck(sampleId,
                SomaticCheck.SENSITIVITY_CHECK.checkName(vcfType.name(), caller.toUpperCase(Locale.ENGLISH)),
                String.valueOf(sensitivity));
    }

    @NotNull
    private static HealthCheck calculateProportion(@NotNull final List<VCFSomaticData> vcfSomaticSetData,
            @NotNull final String sampleId, @NotNull final VCFType vcfType, final int count) {
        final List<VCFSomaticData> setsPerCount = getSetsFilteredByCount(vcfSomaticSetData,
                isTotalCallersCountEqual(count));
        double proportion = 0D;
        if (!setsPerCount.isEmpty() && !vcfSomaticSetData.isEmpty()) {
            proportion = (double) setsPerCount.size() / vcfSomaticSetData.size();
        }

        return new HealthCheck(sampleId,
                SomaticCheck.PROPORTION_CHECK.checkName(vcfType.name(), String.valueOf(count)),
                String.valueOf(proportion));
    }

    @NotNull
    private static List<VCFSomaticData> getSetForCallerWithMoreThanOneCaller(
            @NotNull final List<VCFSomaticData> vcfSomaticSetData, @NotNull final String caller) {
        final List<VCFSomaticData> callerSets = getSetsForCaller(vcfSomaticSetData, caller);
        return callerSets.stream().filter(vcfSomaticSet -> vcfSomaticSet.callerCount() > 1).collect(
                Collectors.toList());
    }

    @NotNull
    private static List<VCFSomaticData> getSetsForCaller(@NotNull final List<VCFSomaticData> vcfSomaticData,
            @NotNull final String caller) {
        return vcfSomaticData.stream().filter(vcfData -> vcfData.callers().contains(caller)).collect(
                Collectors.toList());
    }

    @NotNull
    private static List<VCFSomaticData> getSetsFilteredByCount(@NotNull final List<VCFSomaticData> vcfSomaticSetData,
            @NotNull final Predicate<VCFSomaticData> countFilter) {
        return vcfSomaticSetData.stream().filter(countFilter).collect(Collectors.toList());
    }

    @NotNull
    private static List<VCFSomaticData> filterByVCFType(@NotNull final List<VCFSomaticData> vcfData,
            @NotNull final VCFType type) {
        return vcfData.stream().filter(vcf -> vcf.type().equals(type)).collect(Collectors.toList());
    }

    @NotNull
    private static Predicate<VCFSomaticData> isTotalCallersCountMoreThan(final int count) {
        return vcfSomaticSet -> vcfSomaticSet.callerCount() > count;
    }

    @NotNull
    private static Predicate<VCFSomaticData> isTotalCallersCountEqual(final int count) {
        return vcfSomaticSet -> vcfSomaticSet.callerCount() == count;
    }
}
