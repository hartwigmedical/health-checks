package com.hartwig.healthchecks.bile.extractor;

import java.io.IOException;
import java.util.List;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.extractor.AbstractDataExtractor;
import com.hartwig.healthchecks.common.io.reader.Reader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.PatientReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class RealignerExtractor extends AbstractDataExtractor {

    private static final String EXT = ".sliced.flagstat";

    private final Reader reader;

    public RealignerExtractor(final Reader reader) {
        super();
        this.reader = reader;
    }

    @Override
    public BaseReport extractFromRunDirectory(final String runDirectory) throws IOException, HealthChecksException {
        final BaseDataReport patientData = getPatientData(runDirectory);
        logBaseDataReport(patientData);
        return new PatientReport(CheckType.REALIGNER, patientData);
    }

    private BaseDataReport getPatientData(final String runDirectory) throws IOException, HealthChecksException {
        final List<String> lines = reader.readLines(runDirectory, EXT);

        if (lines.isEmpty()) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, EXT, runDirectory));
        }
        return new BaseDataReport("", "", "");
    }
}
