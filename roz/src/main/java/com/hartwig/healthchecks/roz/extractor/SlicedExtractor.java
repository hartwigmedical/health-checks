package com.hartwig.healthchecks.roz.extractor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.extractor.AbstractDataExtractor;
import com.hartwig.healthchecks.common.io.reader.Reader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.PatientReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class SlicedExtractor extends AbstractDataExtractor {

    private static final int PATIENT_ID_INDEX = 9;

    private static final String SLICED_NUM_VARIANTS = "SLICED_NUMBER_OF_VARIANTS";

    private static final String CHROM = "#CHROM";

    private static final String EXT = "_Cosmicv76_GoNLv5_sliced.vcf";

    private final Reader reader;

    public SlicedExtractor(final Reader reader) {
        super();
        this.reader = reader;
    }

    @Override
    public BaseReport extractFromRunDirectory(final String runDirectory) throws IOException, HealthChecksException {
        final BaseDataReport patientData = getPatientData(runDirectory);
        logBaseDataReport(patientData);
        return new PatientReport(CheckType.SLICED, patientData);
    }

    private BaseDataReport getPatientData(final String runDirectory) throws IOException, HealthChecksException {
        final List<String> lines = reader.readLines(runDirectory, EXT);

        if (lines.isEmpty()) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, EXT, runDirectory));
        }

        final Optional<String> headerLine = lines.stream().filter(line -> line.contains(CHROM)).findFirst();
        if (!headerLine.isPresent()) {
            throw new LineNotFoundException(String.format(LINE_NOT_FOUND_ERROR, EXT, CHROM));
        }

        final String patientId = headerLine.get().split(SEPERATOR_REGEX)[PATIENT_ID_INDEX];
        final long value = lines.stream().filter(line -> !line.startsWith(HASH)).count();
        return new BaseDataReport(patientId, SLICED_NUM_VARIANTS, String.valueOf(value));
    }
}
