package com.hartwig.healthchecks.common.io.reader;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.exception.HealthChecksException;

import org.junit.Test;

public class FileInZipsFinderTest {

    private static final String ZIP_FILE = "CPCT12345678T_FLOWCELL_S2_L002_R2_001_fastqc.zip";
    private static final String FASTQC_DATA_FILE_NAME = "fastqc_data.txt";
    private static final String SAMPLE = "CPCT12345678T";
    private static final String TEST_DIR = "160101_HMFregCPCT_FR10002000_FR20003000_CPCT12345678";
    private static final String QC_STATS = "QCStats";

    @Test
    public void readLines() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(
                TEST_DIR + File.separator + SAMPLE + File.separator + QC_STATS + File.separator + ZIP_FILE);
        final ZipFile zipFile = new ZipFile(testPath.getPath());
        final List<? extends ZipEntry> zipEntry = FileInZipsFinder.build().findFileInZip(zipFile,
                        FASTQC_DATA_FILE_NAME);
        assertNotNull("zip entry is null", zipEntry);
    }

    @Test(expected = FileNotFoundException.class)
    public void readFileNotFound() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(
                TEST_DIR + File.separator + SAMPLE + File.separator + QC_STATS + File.separator + ZIP_FILE);
        final ZipFile zipFile = new ZipFile(testPath.getPath());
        FileInZipsFinder.build().findFileInZip(zipFile, "bla");
    }
}
