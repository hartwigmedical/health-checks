package com.hartwig.healthchecks.roz.extractor;

import java.io.IOException;
import java.util.List;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.extractor.AbstractDataExtractor;
import com.hartwig.healthchecks.common.io.reader.ExtensionLineReader;
import com.hartwig.healthchecks.common.predicate.VCFDataLinePredicate;
import com.hartwig.healthchecks.common.predicate.VCFHeaderLinePredicate;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.PatientReport;

public class SlicedExtractor extends AbstractDataExtractor {

    private static final int PATIENT_ID_INDEX = 9;

    private static final String SLICED_NUM_VARIANTS = "SLICED_NUMBER_OF_VARIANTS";

    private static final String EXT = "_Cosmicv76_GoNLv5_sliced.vcf";

    private final ExtensionLineReader reader;

    public SlicedExtractor(final ExtensionLineReader reader) {
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
        final List<String> headerLine = reader.readLines(runDirectory, EXT, new VCFHeaderLinePredicate());

        final String patientId = headerLine.get(0).split(SEPERATOR_REGEX)[PATIENT_ID_INDEX];

        final long value = reader.readLines(runDirectory, EXT, new VCFDataLinePredicate()).stream()
                        .filter(line -> !line.startsWith(HASH)).count();
        return new BaseDataReport(patientId, SLICED_NUM_VARIANTS, String.valueOf(value));
    }
}
