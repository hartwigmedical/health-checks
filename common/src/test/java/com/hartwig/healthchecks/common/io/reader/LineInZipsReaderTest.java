package com.hartwig.healthchecks.common.io.reader;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;

public class LineInZipsReaderTest {

    private static final String WRONG_VALUE_OF_LINE = "Wrong Value Of Line";

    private static final String TOTAL_SEQUENCES_9952 = "9952";

    private static final String EMPTY_ZIP = "CPCT12345678T_FLOWCELL_S2_L001_R1_001_fastqc.zip";

    private static final String ZIP_FILE = "CPCT12345678T_FLOWCELL_S2_L002_R2_001_fastqc.zip";

    private static final String TOTAL_SEQUENCES = "Total Sequences";

    protected static final String FASTQC_DATA_FILE_NAME = "fastqc_data.txt";

    private static final String PATIENT = "CPCT12345678T";

    private static final String TEST_DIR = "rundir";

    protected static final String QC_STATS = "QCStats";

    @Test
    public void readLine() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(
                        TEST_DIR + File.separator + PATIENT + File.separator + QC_STATS + File.separator + ZIP_FILE);
        final String line = LineInZipsReader.build().readLines(testPath.getPath(), FASTQC_DATA_FILE_NAME,
                        TOTAL_SEQUENCES);
        assertTrue(WRONG_VALUE_OF_LINE, line.startsWith(TOTAL_SEQUENCES));
        assertTrue(WRONG_VALUE_OF_LINE, line.endsWith(TOTAL_SEQUENCES_9952));

    }

    @Test(expected = LineNotFoundException.class)
    public void readLinesEmptyFiles() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource("emptyFiles" + File.separator + PATIENT + File.separator + QC_STATS
                        + File.separator + EMPTY_ZIP);
        LineInZipsReader.build().readLines(testPath.getPath(), FASTQC_DATA_FILE_NAME, TOTAL_SEQUENCES);
    }

    @Test(expected = LineNotFoundException.class)
    public void readLineNotInFile() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(
                        TEST_DIR + File.separator + PATIENT + File.separator + QC_STATS + File.separator + ZIP_FILE);
        LineInZipsReader.build().readLines(testPath.getPath(), FASTQC_DATA_FILE_NAME, "bla");
    }
}
