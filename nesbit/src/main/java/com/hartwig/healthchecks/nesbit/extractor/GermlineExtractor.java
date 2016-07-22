package com.hartwig.healthchecks.nesbit.extractor;

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

public class GermlineExtractor extends AbstractVCFExtractor {

    private static final String GERMLINE_INDELS = "VARIANTS_GERMLINE_INDELS";

    private static final String GERMLINE_SNP = "VARIANTS_GERMLINE_SNP";

    private static final String EXT = "_Cosmicv76_GoNLv5.vcf";

    private final ExtensionFinderAndLineReader reader;

    public GermlineExtractor(final ExtensionFinderAndLineReader reader) {
        super();
        this.reader = reader;
    }

    @Override
    public BaseReport extractFromRunDirectory(final String runDirectory) throws IOException, HealthChecksException {
        final List<String> headerLines = reader.readLines(runDirectory, EXT, new VCFHeaderLinePredicate());
        final List<String> lines = reader.readLines(runDirectory, EXT, new VCFPassDataLinePredicate());

        final List<BaseDataReport> refData = getSampleData(headerLines, lines, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumData = getSampleData(headerLines, lines, TUM_SAMPLE_SUFFIX);

        return new SampleReport(CheckType.GERMLINE, refData, tumData);
    }

    private List<BaseDataReport> getSampleData(final List<String> headerLines, final List<String> lines,
                    final String suffix) throws IOException, HealthChecksException {
        final String[] headers = getHeaders(headerLines, EXT, Boolean.TRUE);
        final String patientId = getPatientIdFromHeader(headers, suffix);
        boolean isRef = Boolean.TRUE;
        if (suffix.equals(TUM_SAMPLE_SUFFIX)) {
            isRef = Boolean.FALSE;
        }
        final List<VCFGermlineData> vcfData = getVCFDataForGermLine(lines);
        final BaseDataReport snp = getGermlineVariantCount(patientId, vcfData, VCFType.SNP, GERMLINE_SNP, isRef);
        final BaseDataReport indels = getGermlineVariantCount(patientId, vcfData, VCFType.INDELS, GERMLINE_INDELS,
                        isRef);
        final List<BaseDataReport> reports = Arrays.asList(snp, indels);
        logBaseDataReports(reports);
        return reports;
    }

    private List<VCFGermlineData> getVCFDataForGermLine(final List<String> lines) {
        return lines.stream().map(line -> {
            final String[] values = line.split(SEPERATOR_REGEX);
            final VCFType type = getVCFType(values[REF_INDEX], values[ALT_INDEX]);
            final String refData = values[PATIENT_REF_INDEX];
            final String tumData = values[PATIENT_TUM_INDEX];
            return new VCFGermlineData(type, refData, tumData);
        }).filter(vcfData -> vcfData != null).collect(Collectors.toList());
    }

    private BaseDataReport getGermlineVariantCount(final String patientId, final List<VCFGermlineData> vcfData,
                    final VCFType vcfType, final String checkName, final boolean refSample) {
        final Long count = vcfData.stream().filter(new VCFGermlineVariantPredicate(vcfType, refSample)).count();
        return new BaseDataReport(patientId, checkName, String.valueOf(count));
    }

}
