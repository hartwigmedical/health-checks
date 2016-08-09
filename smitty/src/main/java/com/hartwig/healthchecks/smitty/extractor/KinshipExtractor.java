package com.hartwig.healthchecks.smitty.extractor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.MalformedFileException;
import com.hartwig.healthchecks.common.io.extractor.AbstractDataExtractor;
import com.hartwig.healthchecks.common.io.reader.FileFinderAndReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.PatientReport;

import org.jetbrains.annotations.NotNull;

public class KinshipExtractor extends AbstractDataExtractor {

    private static final String MALFORMED_FILE_MSG = "Malformed %s file is path %s -> %s lines found was expecting %s";

    private static final int EXPECTED_NUM_LINES = 2;
    private static final int SAMPLE_ID_INDEX = 0;
    private static final int KINSHIP_INDEX = 7;
    private static final String KINSHIP_TEST = "KINSHIP_TEST";
    private static final String KINSHIP = ".kinship";

    @NotNull
    private final FileFinderAndReader kinshipReader;

    public KinshipExtractor(@NotNull final FileFinderAndReader kinshipReader) {
        super();
        this.kinshipReader = kinshipReader;
    }

    @Override
    @NotNull
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
                    throws IOException, HealthChecksException {
        final List<String> kinshipLines = kinshipReader.readLines(runDirectory, KINSHIP);
        if (kinshipLines.size() != EXPECTED_NUM_LINES) {
            throw new MalformedFileException(String.format(MALFORMED_FILE_MSG, KINSHIP_TEST, runDirectory,
                            kinshipLines.size(), EXPECTED_NUM_LINES));
        }
        final Optional<BaseDataReport> optBaseDataReport = kinshipLines.stream().skip(ONE).map(line -> {
            final String[] values = line.split(SEPARATOR_REGEX);
            return new BaseDataReport(values[SAMPLE_ID_INDEX], KINSHIP_TEST, values[KINSHIP_INDEX]);
        }).findFirst();

        assert optBaseDataReport.isPresent();

        logBaseDataReport(optBaseDataReport.get());
        return new PatientReport(CheckType.KINSHIP, optBaseDataReport.get());
    }
}
