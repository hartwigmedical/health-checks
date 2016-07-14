package com.hartwig.healthchecks.nesbit.extractor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.reader.FilteredReader;
import com.hartwig.healthchecks.common.predicate.VCFPassDataLinePredicate;
import com.hartwig.healthchecks.common.predicate.VCFHeaderLinePredicate;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.PatientMultiChecksReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.nesbit.model.VCFData;
import com.hartwig.healthchecks.nesbit.model.VCFType;

public class SomaticExtractor extends AbstractVCFExtractor {

    private static final String SOMATIC_INDELS = "VARIANTS_SOMATIC_INDELS";

    private static final String SOMATIC_SNP = "VARIANTS_SOMATIC_SNP";

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

        final BaseDataReport snp = getCountCheck(patientId, vcfData, VCFType.SNP, SOMATIC_SNP);
        final BaseDataReport indels = getCountCheck(patientId, vcfData, VCFType.INDELS, SOMATIC_INDELS);

        return Arrays.asList(snp, indels);
    }

}
