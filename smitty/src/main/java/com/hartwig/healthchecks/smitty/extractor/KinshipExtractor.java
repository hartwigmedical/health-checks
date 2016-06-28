package com.hartwig.healthchecks.smitty.extractor;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.extractor.BaseDataExtractor;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.smitty.reader.KinshipReader;
import com.hartwig.healthchecks.smitty.report.KinshipReport;

public class KinshipExtractor extends BaseDataExtractor {

    private static final int PATIENT_ID_INDEX = 0;

    private static final int KINSHIP_INDEX = 7;

    private static final double MIN_VALUE = 0.46d;

    private static final String KINSHIP_TEST = "KINSHIP_TEST";

    private final KinshipReader kinshipReader;

    public KinshipExtractor(final KinshipReader kinshipReader) {
        this.kinshipReader = kinshipReader;
    }

    @Override
    @NotNull
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
                    throws IOException, EmptyFileException {
        final List<String> kinshipLines = kinshipReader.readLinesFromKinship(runDirectory);
        if (kinshipLines.isEmpty()) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, KINSHIP_TEST, runDirectory));
        }

        final List<BaseDataReport> baseDataReports = kinshipLines.stream().skip(ONE).map(line -> {
            final String[] values = line.split(SEPERATOR_REGEX);
            String checkStatus = PASS;
            if (Double.parseDouble(values[KINSHIP_INDEX]) < MIN_VALUE) {
                checkStatus = FAIL;
            }
            return new BaseDataReport(values[PATIENT_ID_INDEX], KINSHIP_TEST, checkStatus);
        }).collect(Collectors.toList());
        return new KinshipReport(CheckType.KINSHIP, baseDataReports);
    }
}
