package com.hartwig.healthchecks.nesbit.extractor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.data.BaseResult;
import com.hartwig.healthchecks.common.data.PatientResult;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.reader.ExtensionFinderAndLineReader;
import com.hartwig.healthchecks.common.predicate.VCFPassDataLinePredicate;
import com.hartwig.healthchecks.nesbit.model.VCFGermlineData;
import com.hartwig.healthchecks.nesbit.model.VCFType;
import com.hartwig.healthchecks.nesbit.predicate.VCFGermlineVariantPredicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class GermlineExtractor extends AbstractVCFExtractor {

    private static final Logger LOGGER = LogManager.getLogger(GermlineExtractor.class);

    private static final String GERMLINE_VCF_EXTENSION = "_Cosmicv76_GoNLv5.vcf";
    private static final String VCF_COLUMN_SEPARATOR = "\t";
    private static final int TUMOR_SAMPLE_COLUMN = 10;
    private static final int REF_SAMPLE_COLUMN = 9;

    @NotNull
    private final ExtensionFinderAndLineReader reader = ExtensionFinderAndLineReader.build();
    @NotNull
    private final RunContext runContext;

    public GermlineExtractor(@NotNull final RunContext runContext) {
        this.runContext = runContext;
    }

    @NotNull
    @Override
    public BaseResult extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<String> passFilterLines = reader.readLines(runContext.runDirectory(), GERMLINE_VCF_EXTENSION,
                new VCFPassDataLinePredicate());
        final List<VCFGermlineData> vcfData = getVCFDataForGermLine(passFilterLines);

        final List<HealthCheck> refData = getSampleData(vcfData, runContext.refSample(), true);
        final List<HealthCheck> tumData = getSampleData(vcfData, runContext.tumorSample(), false);

        return new PatientResult(CheckType.GERMLINE, refData, tumData);
    }

    @NotNull
    private static List<HealthCheck> getSampleData(@NotNull List<VCFGermlineData> vcfData,
            @NotNull final String sampleId, final boolean isRefSample) throws IOException, HealthChecksException {
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
        return lines.stream().map(line -> {
            final String[] values = line.split(VCF_COLUMN_SEPARATOR);
            final VCFType type = getVCFType(values[REF_INDEX], values[ALT_INDEX]);
            final String refData = values[REF_SAMPLE_COLUMN];
            final String tumData = values[TUMOR_SAMPLE_COLUMN];
            return new VCFGermlineData(type, refData, tumData);
        }).filter(vcfData -> vcfData != null).collect(Collectors.toList());
    }

    @NotNull
    private static HealthCheck getGermlineVariantCount(@NotNull final String sampleId,
            @NotNull final List<VCFGermlineData> vcfData, @NotNull final VCFType vcfType,
            @NotNull final GermlineCheck check, final boolean isRefSample) {
        final long count = vcfData.stream().filter(new VCFGermlineVariantPredicate(vcfType, isRefSample)).count();
        return new HealthCheck(sampleId, check.toString(), String.valueOf(count));
    }
}
