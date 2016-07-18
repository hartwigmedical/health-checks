package com.hartwig.healthchecks.nesbit.extractor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.reader.FilteredReader;
import com.hartwig.healthchecks.common.predicate.VCFHeaderLinePredicate;
import com.hartwig.healthchecks.common.predicate.VCFPassDataLinePredicate;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.PatientMultiChecksReport;
import com.hartwig.healthchecks.nesbit.model.VCFData;
import com.hartwig.healthchecks.nesbit.model.VCFType;

public class GermlineExtractor extends AbstractVCFExtractor {

    private static final String GERMLINE_INDELS = "VARIANTS_GERMLINE_INDELS";

    private static final String GERMLINE_SNP = "VARIANTS_GERMLINE_SNP";

    private static final String EXT = "_Cosmicv76_GoNLv5.vcf";

    private final FilteredReader reader;

    public GermlineExtractor(final FilteredReader reader) {
        super();
        this.reader = reader;
    }

    @Override
    public BaseReport extractFromRunDirectory(final String runDirectory) throws IOException, HealthChecksException {
        final List<BaseDataReport> patientData = getPatientData(runDirectory);
        return new PatientMultiChecksReport(CheckType.GERMLINE, patientData);
    }

    private List<BaseDataReport> getPatientData(final String runDirectory) throws IOException, HealthChecksException {

        final List<String> headerLines = reader.readLines(runDirectory, EXT, new VCFHeaderLinePredicate());
        final String[] headers = getHeaders(headerLines, EXT, Boolean.TRUE);
        final String patientId = getPatientIdFromHeader(headers, REF_SAMPLE_SUFFIX);

        final List<String> lines = reader.readLines(runDirectory, EXT, new VCFPassDataLinePredicate());
        final List<VCFData> vcfData = getVCFData(lines);
        final BaseDataReport snp = getCountCheck(patientId, vcfData, VCFType.SNP, GERMLINE_SNP);
        final BaseDataReport indels = getCountCheck(patientId, vcfData, VCFType.INDELS, GERMLINE_INDELS);
        final List<BaseDataReport> reports = Arrays.asList(snp, indels);
        logBaseDataReports(reports);
        return reports;
    }

}
