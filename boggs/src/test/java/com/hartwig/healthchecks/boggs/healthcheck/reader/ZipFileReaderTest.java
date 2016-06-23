package com.hartwig.healthchecks.boggs.healthcheck.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;

import org.junit.Test;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsExtractor;
import com.hartwig.healthchecks.boggs.reader.ZipFileReader;

import mockit.Mocked;

public class ZipFileReaderTest {

    private static final String NOT_EMPTY_MSG = "List is not Empty";
    private static final String TEST_PATH = "rundir/CPCT12345678R/QCStats/"
                    + "CPCT12345678R_FLOWCELL_S2_L001_R1_001_fastqc.zip";
    private static final String TEST_EMPTY_PATH = "emptyFiles/CPCT12345678R/QCStats/"
                    + "CPCT12345678R_FLOWCELL_S2_L001_R1_001_fastqc.zip";

    private static final String MISSING_LINES_MSG = "Missing lines from Zip";

    private static final int ExpectedNumberOfLines = 13;

    private static final String SUMMARY_FILE_NAME = "summary.txt";

    @Mocked
    private PrestatsExtractor dataExtractor;

    @Test
    public void readZipFile() {
        final URL exampleFlagStatURL = Resources.getResource(TEST_PATH);

        final ZipFileReader zipFileReader = new ZipFileReader();
        final List<String> lines = zipFileReader.readFileFromZip(exampleFlagStatURL.getPath(), SUMMARY_FILE_NAME);
        assertEquals(MISSING_LINES_MSG, ExpectedNumberOfLines, lines.size());
    }

    @Test
    public void readZipFileEmptyFile() {
        final URL exampleFlagStatURL = Resources.getResource(TEST_EMPTY_PATH);

        final ZipFileReader zipFileReader = new ZipFileReader();
        final List<String> lines = zipFileReader.readFileFromZip(exampleFlagStatURL.getPath(), SUMMARY_FILE_NAME);
        assertTrue(NOT_EMPTY_MSG, lines.isEmpty());
    }
}