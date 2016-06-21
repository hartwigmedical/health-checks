package com.hartwig.healthchecks.boggs.healthcheck.prestasts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.exception.EmptyFileException;

import org.junit.Test;

public class PrestatsExtractorTest {
    private static final String WRONG_NUMBER_OF_CHECKS_MSG = "Wrong number of checks";
    private static final int EXPECTED_CHECKS_NUM = 13;
    private static final String EMPTY_FILES = "emptyFiles";
    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    @Test public void canProcessRunDirectoryStructure() throws IOException, EmptyFileException {
        URL runDirURL = Resources.getResource("rundir");
        PrestatsExtractor extractor = new PrestatsExtractor();
        PrestatsReport prestatsData = extractor.extractFromRunDirectory(runDirURL.getPath().toString());

        assertNotNull("We should get some fails", prestatsData);
        assertEquals(WRONG_NUMBER_OF_CHECKS_MSG, EXPECTED_CHECKS_NUM, prestatsData.getSummary().size());
        String actualStatus = prestatsData.getSummary().stream().filter(p -> p.getCheckName().equals(
                PrestatsCheck.PRESTATS_PER_TILE_SEQUENCE_QUALITY.getDescription())).findFirst().get().getStatus();
        assertEquals(WRONG_NUMBER_OF_CHECKS_MSG, PrestatsExtractor.WARN, actualStatus);
        actualStatus = prestatsData.getSummary().stream().filter(p -> p.getCheckName().equals(
                PrestatsCheck.PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION.getDescription())).findFirst().get().getStatus();
        assertEquals(WRONG_NUMBER_OF_CHECKS_MSG, PrestatsExtractor.FAIL, actualStatus);

        actualStatus = prestatsData.getSummary().stream().filter(p -> p.getCheckName().equals(
                PrestatsCheck.PRESTATS_SEQUENCE_DUPLICATION_LEVELS.getDescription())).findFirst().get().getStatus();
        assertEquals(WRONG_NUMBER_OF_CHECKS_MSG, PrestatsExtractor.PASS, actualStatus);
    }

    @Test(expected = EmptyFileException.class) public void extractDataEmptyFile()
            throws IOException, EmptyFileException {
        URL runDirURL = Resources.getResource(EMPTY_FILES);
        PrestatsExtractor extractor = new PrestatsExtractor();
        extractor.extractFromRunDirectory(runDirURL.getPath().toString());
    }

    @Test(expected = IOException.class) public void extractDataNoneExistingDir()
            throws IOException, EmptyFileException {
        PrestatsExtractor extractor = new PrestatsExtractor();
        extractor.extractFromRunDirectory(DUMMY_RUN_DIR);
    }
}
