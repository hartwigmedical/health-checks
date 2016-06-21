package com.hartwig.healthchecks.boggs.healthcheck.prestasts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;

import org.junit.Test;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.exception.EmptyFileException;

public class PrestatsExtractorTest {
    private static final String REPORT_SHOULD_NOT_BE_NULL = "Report should not be null";
    private static final String TEST_ID = "CPCT12345678R";
    private static final String WRONG_PATIENT_ID_MSG = "Wrong Patient Id";
    private static final String WRONG_NUMBER_OF_CHECKS_MSG = "Wrong number of checks";
    private static final int EXPECTED_CHECKS_NUM = 13;
    private static final String EMPTY_FILES = "emptyFiles";
    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    @Test
    public void canProcessRunDirectoryStructure() throws IOException, EmptyFileException {
        URL runDirURL = Resources.getResource("rundir");
        PrestatsExtractor extractor = new PrestatsExtractor();
        PrestatsReport prestatsData = extractor.extractFromRunDirectory(runDirURL.getPath().toString());

        assertNotNull(REPORT_SHOULD_NOT_BE_NULL, prestatsData);
        assertEquals(WRONG_NUMBER_OF_CHECKS_MSG, EXPECTED_CHECKS_NUM, prestatsData.getSummary().size());
        assertPrestatsReport(prestatsData, PrestatsCheck.PRESTATS_PER_TILE_SEQUENCE_QUALITY.getDescription(),
                PrestatsExtractor.WARN);
        assertPrestatsReport(prestatsData, PrestatsCheck.PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION.getDescription(),
                PrestatsExtractor.FAIL);
        assertPrestatsReport(prestatsData, PrestatsCheck.PRESTATS_SEQUENCE_DUPLICATION_LEVELS.getDescription(),
                PrestatsExtractor.PASS);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataEmptyFile() throws IOException, EmptyFileException {
        URL runDirURL = Resources.getResource(EMPTY_FILES);
        PrestatsExtractor extractor = new PrestatsExtractor();
        extractor.extractFromRunDirectory(runDirURL.getPath().toString());
    }

    @Test(expected = NoSuchFileException.class)
    public void extractDataNoneExistingDir() throws IOException, EmptyFileException {
        PrestatsExtractor extractor = new PrestatsExtractor();
        extractor.extractFromRunDirectory(DUMMY_RUN_DIR);
    }

    private void assertPrestatsReport(PrestatsReport prestatsData, String check, String expectedStatus) {
        String actualStatus = prestatsData.getSummary().stream().filter(p -> p.getCheckName().equals(check)).findFirst()
                .get().getStatus();
        String externalId = prestatsData.getSummary().stream().filter(p -> p.getCheckName().equals(check)).findFirst()
                .get().getPatientId();
        assertEquals(WRONG_PATIENT_ID_MSG, TEST_ID, externalId);
        assertEquals(WRONG_NUMBER_OF_CHECKS_MSG, expectedStatus, actualStatus);
    }
}
