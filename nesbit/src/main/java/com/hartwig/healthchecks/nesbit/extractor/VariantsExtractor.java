package com.hartwig.healthchecks.nesbit.extractor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.reader.Reader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.PatientMultiChecksReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.nesbit.model.VCFData;
import com.hartwig.healthchecks.nesbit.model.VCFType;

public class VariantsExtractor extends AbstractVCFExtractor {

    private static final String GERMLINE_INDELS = "VARIANTS_GERMLINE_INDELS";

    private static final String GERMLINE_SNP = "VARIANTS_GERMLINE_SNP";

    private static final String EXT = "_Cosmicv76_GoNLv5.vcf";

    private final Reader reader;

    public VariantsExtractor(final Reader reader) {
        super();
        this.reader = reader;
    }

    @Override
    public BaseReport extractFromRunDirectory(final String runDirectory) throws IOException, HealthChecksException {
        final List<BaseDataReport> patientData = getPatientData(runDirectory);
        return new PatientMultiChecksReport(CheckType.VARIANTS, patientData);
    }

    private List<BaseDataReport> getPatientData(final String runDirectory) throws IOException, HealthChecksException {
        final List<String> lines = reader.readLines(runDirectory, EXT);
        if (lines.isEmpty()) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, EXT, runDirectory));
        }

        final Optional<String> headerLine = lines.stream().filter(line -> line.contains(CHROM)).findFirst();
        validateHeader(headerLine, EXT);
        final String[] headers = headerLine.get().split(SEPERATOR_REGEX);
        final String patientId = Arrays.stream(headers)
                        .filter(header -> header.startsWith(SAMPLE_PREFIX) && header.endsWith(REF_SAMPLE_SUFFIX))
                        .findFirst().get();
        final List<VCFData> vcfData = getVCFData(lines);

        final Long snpCount = vcfData.stream().filter(data -> data.getType().equals(VCFType.SNP)).count();
        final BaseDataReport snp = new BaseDataReport(patientId, GERMLINE_SNP, String.valueOf(snpCount));

        final Long indelCount = vcfData.stream().filter(data -> data.getType().equals(VCFType.INDEL)).count();
        final BaseDataReport indel = new BaseDataReport(patientId, GERMLINE_INDELS, String.valueOf(indelCount));

        return Arrays.asList(snp, indel);
    }
}
