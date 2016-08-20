package com.hartwig.healthchecks.nesbit.extractor;

import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.REF_SAMPLE_SUFFIX;
import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.SEPARATOR_REGEX;
import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.TUM_SAMPLE_SUFFIX;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.reader.ExtensionFinderAndLineReader;
import com.hartwig.healthchecks.common.predicate.VCFHeaderLinePredicate;
import com.hartwig.healthchecks.common.predicate.VCFPassDataLinePredicate;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;
import com.hartwig.healthchecks.nesbit.model.VCFGermlineData;
import com.hartwig.healthchecks.nesbit.model.VCFType;
import com.hartwig.healthchecks.nesbit.predicate.VCFGermlineVariantPredicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class GermlineExtractor extends AbstractVCFExtractor {

    private static final Logger LOGGER = LogManager.getLogger(GermlineExtractor.class);

    private static final String GERMLINE_INDELS = "VARIANTS_GERMLINE_INDELS";
    private static final String GERMLINE_SNP = "VARIANTS_GERMLINE_SNP";
    private static final String GERMLINE_VCF_EXTENSION = "_Cosmicv76_GoNLv5.vcf";

    @NotNull
    private final ExtensionFinderAndLineReader reader;

    public GermlineExtractor(@NotNull final ExtensionFinderAndLineReader reader) {
        this.reader = reader;
    }

    @NotNull
    @Override
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {

        final List<String> headerLines = reader.readLines(runDirectory, GERMLINE_VCF_EXTENSION,
                new VCFHeaderLinePredicate());
        final List<String> lines = reader.readLines(runDirectory, GERMLINE_VCF_EXTENSION,
                new VCFPassDataLinePredicate());

        final List<BaseDataReport> refData = getSampleData(headerLines, lines, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumData = getSampleData(headerLines, lines, TUM_SAMPLE_SUFFIX);

        return new SampleReport(CheckType.GERMLINE, refData, tumData);
    }

    @NotNull
    private static List<BaseDataReport> getSampleData(@NotNull final List<String> headerLines,
            @NotNull final List<String> lines, @NotNull final String suffix)
            throws IOException, HealthChecksException {
        final String[] headers = getHeaders(headerLines, GERMLINE_VCF_EXTENSION, Boolean.TRUE);
        final String sampleId = getSampleIdFromHeader(headers, suffix);
        boolean isRef = Boolean.TRUE;
        if (suffix.equals(TUM_SAMPLE_SUFFIX)) {
            isRef = Boolean.FALSE;
        }
        final List<VCFGermlineData> vcfData = getVCFDataForGermLine(lines);
        final BaseDataReport snp = getGermlineVariantCount(sampleId, vcfData, VCFType.SNP, GERMLINE_SNP, isRef);
        final BaseDataReport indels = getGermlineVariantCount(sampleId, vcfData, VCFType.INDELS, GERMLINE_INDELS,
                isRef);
        final List<BaseDataReport> reports = Arrays.asList(snp, indels);
        BaseDataReport.log(LOGGER, reports);
        return reports;
    }

    @NotNull
    private static List<VCFGermlineData> getVCFDataForGermLine(@NotNull final List<String> lines) {
        return lines.stream().map(line -> {
            final String[] values = line.split(SEPARATOR_REGEX);
            final VCFType type = getVCFType(values[REF_INDEX], values[ALT_INDEX]);
            final String refData = values[PATIENT_REF_INDEX];
            final String tumData = values[PATIENT_TUM_INDEX];
            return new VCFGermlineData(type, refData, tumData);
        }).filter(vcfData -> vcfData != null).collect(Collectors.toList());
    }

    @NotNull
    private static BaseDataReport getGermlineVariantCount(@NotNull final String sampleId,
            @NotNull final List<VCFGermlineData> vcfData, @NotNull final VCFType vcfType,
            @NotNull final String checkName, final boolean refSample) {
        final Long count = vcfData.stream().filter(new VCFGermlineVariantPredicate(vcfType, refSample)).count();
        return new BaseDataReport(sampleId, checkName, String.valueOf(count));
    }
}
