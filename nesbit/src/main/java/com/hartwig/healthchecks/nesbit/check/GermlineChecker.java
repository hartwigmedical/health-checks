package com.hartwig.healthchecks.nesbit.check;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import com.hartwig.healthchecks.common.result.PatientResult;
import com.hartwig.healthchecks.nesbit.model.VCFGermlineData;
import com.hartwig.healthchecks.nesbit.model.VCFGermlineDataFactory;
import com.hartwig.healthchecks.nesbit.model.VCFType;
import com.hartwig.healthchecks.nesbit.predicate.VCFGermlineVariantPredicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("WeakerAccess")
@ResourceWrapper(type = CheckType.GERMLINE)
public class GermlineChecker implements HealthChecker {

    private static final Logger LOGGER = LogManager.getLogger(GermlineChecker.class);

    private static final String GERMLINE_VCF_EXTENSION = "_Cosmicv76_GoNLv5.vcf";

    public GermlineChecker() {
    }

    @NotNull
    @Override
    public CheckType checkType() {
        return CheckType.GERMLINE;
    }

    @NotNull
    @Override
    public BaseResult run(@NotNull final RunContext runContext) throws IOException, HealthChecksException {
        final Path vcfPath = PathExtensionFinder.build().findPath(runContext.runDirectory(), GERMLINE_VCF_EXTENSION);
        final List<String> passFilterLines = LineReader.build().readLines(vcfPath, new VCFPassDataLinePredicate());
        final List<VCFGermlineData> vcfData = getVCFDataForGermLine(passFilterLines);

        final List<HealthCheck> refData = getSampleData(vcfData, runContext.refSample(), true);
        final List<HealthCheck> tumData = getSampleData(vcfData, runContext.tumorSample(), false);

        return new PatientResult(checkType(), refData, tumData);
    }

    @NotNull
    private static List<HealthCheck> getSampleData(@NotNull List<VCFGermlineData> vcfData,
            @NotNull final String sampleId, final boolean isRefSample) {
        final HealthCheck snp = getGermlineVariantCount(sampleId, vcfData, VCFType.SNP,
                GermlineCheck.VARIANTS_GERMLINE_SNP, isRefSample);
        final HealthCheck indels = getGermlineVariantCount(sampleId, vcfData, VCFType.INDELS,
                GermlineCheck.VARIANTS_GERMLINE_INDELS, isRefSample);
        final List<HealthCheck> reports = Arrays.asList(snp, indels);
        HealthCheck.log(LOGGER, reports);
        return reports;
    }

    @NotNull
    private static List<VCFGermlineData> getVCFDataForGermLine(@NotNull final List<String> lines) {
        return lines.stream().map(VCFGermlineDataFactory::fromVCFLine).collect(Collectors.toList());
    }

    @NotNull
    private static HealthCheck getGermlineVariantCount(@NotNull final String sampleId,
            @NotNull final List<VCFGermlineData> vcfData, @NotNull final VCFType vcfType,
            @NotNull final GermlineCheck check, final boolean isRefSample) {
        final long count = vcfData.stream().filter(new VCFGermlineVariantPredicate(vcfType, isRefSample)).count();
        return new HealthCheck(sampleId, check.toString(), String.valueOf(count));
    }
}
