package com.hartwig.healthchecks.common.io.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.io.reader.ZipFilesReader;

public class ZipFilesReaderTest {

    private static final String NOT_EMPTY_MSG = "List is not Empty";

    private static final String TEST_DIR = "rundir";

    private static final String TEST_PATH = "rundir/CPCT12345678R/QCStats/"
                    + "CPCT12345678R_FLOWCELL_S2_L001_R1_001_fastqc.zip";

    private static final String TEST_EMPTY_PATH = "emptyFiles/CPCT12345678R/QCStats/"
                    + "CPCT12345678R_FLOWCELL_S2_L001_R1_001_fastqc.zip";

    private static final String MISSING_LINES_MSG = "Missing lines from Zip";

    private static final int NUM_SUNMARY_LINES = 13;

    private static final int NUM_FASTQC_LINES = 1;

    private static final String SUMMARY_FILE_NAME = "summary.txt";

    private static final String SAMPLE_PREFIX = "CPCT";

    private static final String REF_SAMPLE_SUFFIX = "R";

    private static final String TOTAL_SEQUENCES = "Total Sequences";

    private static final String FASTQC_DATA_FILE_NAME = "fastqc_data.txt";

    @Test
    public void getZipFilesPath() throws IOException {
        final URL exampleFlagStatURL = Resources.getResource(TEST_DIR);
        final ZipFilesReader zipFileReader = new ZipFilesReader();
        final Path path = zipFileReader.getZipFilesPath(exampleFlagStatURL.getPath(), SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);
        assertNotNull("WrongPath", path);
    }

    @Test(expected = NoSuchFileException.class)
    public void getZipFilesPathNoExisting() throws IOException {
        final ZipFilesReader zipFileReader = new ZipFilesReader();
        zipFileReader.getZipFilesPath("bla", SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);
    }

    @Test
    public void readAllLinesFromZip() throws IOException {
        final URL url = Resources.getResource(TEST_PATH);
        final ZipFilesReader zipFileReader = new ZipFilesReader();
        final Path path = new File(url.getPath()).toPath();
        final List<String> lines = zipFileReader.readAllLinesFromZips(path, SUMMARY_FILE_NAME);
        assertEquals(MISSING_LINES_MSG, NUM_SUNMARY_LINES, lines.size());
    }

    @Test
    public void readAllLinesFromEmptyFileFromZip() throws IOException {
        final URL url = Resources.getResource(TEST_EMPTY_PATH);
        final ZipFilesReader zipFileReader = new ZipFilesReader();
        final Path path = new File(url.getPath()).toPath();
        final List<String> lines = zipFileReader.readAllLinesFromZips(path, SUMMARY_FILE_NAME);
        assertTrue(NOT_EMPTY_MSG, lines.isEmpty());
    }

    @Test
    public void readFieldFromZipFiles() throws IOException {
        final URL url = Resources.getResource(TEST_PATH);
        final ZipFilesReader zipFileReader = new ZipFilesReader();
        final Path path = new File(url.getPath()).toPath();
        final List<String> lines = zipFileReader.readFieldFromZipFiles(path, FASTQC_DATA_FILE_NAME, TOTAL_SEQUENCES);
        assertEquals(MISSING_LINES_MSG, NUM_FASTQC_LINES, lines.size());
    }

    @Test
    public void readFieldFromZipFilesEmpty() throws IOException {
        final URL url = Resources.getResource(TEST_EMPTY_PATH);
        final ZipFilesReader zipFileReader = new ZipFilesReader();
        final Path path = new File(url.getPath()).toPath();
        final List<String> lines = zipFileReader.readFieldFromZipFiles(path, FASTQC_DATA_FILE_NAME, TOTAL_SEQUENCES);
        assertTrue(NOT_EMPTY_MSG, lines.isEmpty());
    }
}
