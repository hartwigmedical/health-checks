package com.hartwig.healthchecks.nesbit.check;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
import com.hartwig.healthchecks.nesbit.model.VCFConstants;
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
            @NotNull final String sampleId, @NotNull final VCFType type) {
        final List<HealthCheck> checks = new ArrayList<>();
        final List<VCFSomaticData> vcfForType = filter(vcfData, hasVCFType(type));

        final HealthCheck vcfCountCheck = new HealthCheck(sampleId, SomaticCheck.COUNT.checkName(type.name()),
                String.valueOf(vcfForType.size()));
        checks.add(vcfCountCheck);

        final List<HealthCheck> precisionChecks = VCFConstants.ALL_CALLERS.stream().map(
                caller -> calculatePrecision(vcfForType, sampleId, type, caller)).collect(Collectors.toList());
        checks.addAll(precisionChecks);

        final List<HealthCheck> sensitivityChecks = VCFConstants.ALL_CALLERS.stream().map(
                caller -> calculateSensitivity(vcfForType, sampleId, type, caller)).collect(Collectors.toList());
        checks.addAll(sensitivityChecks);

        final List<HealthCheck> proportionChecks = CALLERS_COUNT.stream().map(
                callerCount -> calculateProportion(vcfForType, sampleId, type, callerCount)).collect(
                Collectors.toList());
        checks.addAll(proportionChecks);
        return checks;
    }

    @NotNull
    private static List<HealthCheck> getAFChecks(final List<VCFSomaticData> vcfData, final String sampleId) {
        return Lists.newArrayList();
    }

    @NotNull
    private static HealthCheck calculatePrecision(@NotNull final List<VCFSomaticData> variants,
            @NotNull final String sampleId, @NotNull final VCFType type, @NotNull final String caller) {
        final List<VCFSomaticData> variantsForCaller = filter(variants, hasCaller(caller));
        final List<VCFSomaticData> variantsForCallerWithMoreThanOneCaller = filter(variantsForCaller,
                isTotalCallersCountMoreThan(1));

        double precision = 0D;
        if (!variantsForCallerWithMoreThanOneCaller.isEmpty() && !variantsForCaller.isEmpty()) {
            precision = (double) variantsForCallerWithMoreThanOneCaller.size() / variantsForCaller.size();
        }
        return new HealthCheck(sampleId,
                SomaticCheck.PRECISION_CHECK.checkName(type.name(), caller.toUpperCase(Locale.ENGLISH)),
                String.valueOf(precision));
    }

    @NotNull
    private static HealthCheck calculateSensitivity(@NotNull final List<VCFSomaticData> variants,
            @NotNull final String sampleId, @NotNull final VCFType type, @NotNull final String caller) {
        final List<VCFSomaticData> variantsWithMoreThanOneCaller = filter(variants, isTotalCallersCountMoreThan(1));
        final List<VCFSomaticData> variantsForCallerWithMoreThanOneCaller = filter(variantsWithMoreThanOneCaller,
                hasCaller(caller));

        double sensitivity = 0D;
        if (!variantsForCallerWithMoreThanOneCaller.isEmpty() && !variantsWithMoreThanOneCaller.isEmpty()) {
            sensitivity =
                    (double) variantsForCallerWithMoreThanOneCaller.size() / variantsWithMoreThanOneCaller.size();
        }
        return new HealthCheck(sampleId,
                SomaticCheck.SENSITIVITY_CHECK.checkName(type.name(), caller.toUpperCase(Locale.ENGLISH)),
                String.valueOf(sensitivity));
    }

    @NotNull
    private static HealthCheck calculateProportion(@NotNull final List<VCFSomaticData> variants,
            @NotNull final String sampleId, @NotNull final VCFType type, final int count) {
        final List<VCFSomaticData> variantsWithCallerCount = filter(variants, isTotalCallersCountEqual(count));
        double proportion = 0D;
        if (!variantsWithCallerCount.isEmpty() && !variants.isEmpty()) {
            proportion = (double) variantsWithCallerCount.size() / variants.size();
        }

        return new HealthCheck(sampleId, SomaticCheck.PROPORTION_CHECK.checkName(type.name(), String.valueOf(count)),
                String.valueOf(proportion));
    }

    @NotNull
    private static List<VCFSomaticData> filter(@NotNull final List<VCFSomaticData> variants,
            @NotNull final Predicate<VCFSomaticData> filter) {
        return variants.stream().filter(filter).collect(Collectors.toList());
    }

    @NotNull
    private static Predicate<VCFSomaticData> hasVCFType(@NotNull final VCFType type) {
        return vcf -> vcf.type().equals(type);
    }

    @NotNull
    private static Predicate<VCFSomaticData> hasCaller(@NotNull final String caller) {
        return vcf -> vcf.callers().contains(caller);
    }

    @NotNull
    private static Predicate<VCFSomaticData> isTotalCallersCountMoreThan(final int count) {
        return vcf -> vcf.callerCount() > count;
    }

    @NotNull
    private static Predicate<VCFSomaticData> isTotalCallersCountEqual(final int count) {
        return vcf -> vcf.callerCount() == count;
    }
}
