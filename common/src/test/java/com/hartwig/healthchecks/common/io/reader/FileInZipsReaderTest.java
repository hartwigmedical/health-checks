package com.hartwig.healthchecks.common.io.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;

import org.junit.Test;

public class FileInZipsReaderTest {

    private static final String ZIP_FILE = "CPCT12345678T_FLOWCELL_S2_L002_R2_001_fastqc.zip";
    private static final String SAMPLE = "CPCT12345678T";
    private static final String TEST_DIR = "160101_HMFregCPCT_FR10002000_FR20003000_CPCT12345678";
    private static final String QC_STATS = "QCStats";
    private static final String SUMMARY_FILE_NAME = "summary.txt";
    private static final String WRONG_NUM_LINES = "Wrong # of Lines";
    private static final String NOT_NULL = "Should not Be null";
    private static final int EXPECTED_NUM_LINES = 13;

    @Test
    public void readLines() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(
                TEST_DIR + File.separator + SAMPLE + File.separator + QC_STATS + File.separator + ZIP_FILE);
        final List<String> readLines = FileInZipsReader.build().readLines(testPath.getPath(), SUMMARY_FILE_NAME);
        assertNotNull(NOT_NULL, readLines);
        assertEquals(WRONG_NUM_LINES, EXPECTED_NUM_LINES, readLines.size());
    }

    @Test(expected = EmptyFileException.class)
    public void readLinesEmptyFiles() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource("emptyFile.zip");
        FileInZipsReader.build().readLines(testPath.getPath(), "emptyFile");
    }

    @Test(expected = FileNotFoundException.class)
    public void readLinesFileNotInZip() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(
                TEST_DIR + File.separator + SAMPLE + File.separator + QC_STATS + File.separator + ZIP_FILE);
        FileInZipsReader.build().readLines(testPath.getPath(), "bla");
    }
}
