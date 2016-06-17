package com.hartwig.healthchecks.boggs.healthcheck.prestast;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PrestatsExtractorTest {
    private static final String EMPTY_FILES = "emptyFiles";
    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    @Test
    public void canProcessRunDirectoryStructure() throws IOException, EmptyFileException {
        URL runDirURL = Resources.getResource("rundir");
        PrestatsExtractor extractor = new PrestatsExtractor();
        PrestatsReport prestatsData = extractor.extractFromRunDirectory(runDirURL.getPath().toString());

        assertNotNull("We should get some fails", prestatsData);
        assertEquals("Number of files that has failed is not correct", 49, prestatsData.getSummary().size());
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataEmptyFile() throws IOException, EmptyFileException {
        URL runDirURL = Resources.getResource(EMPTY_FILES);
        PrestatsExtractor extractor = new PrestatsExtractor();
        extractor.extractFromRunDirectory(runDirURL.getPath().toString());
    }

    @Test(expected = IOException.class)
    public void extractDataNoneExistingDir() throws IOException, EmptyFileException {
        PrestatsExtractor extractor = new PrestatsExtractor();
        extractor.extractFromRunDirectory(DUMMY_RUN_DIR);
    }
}
