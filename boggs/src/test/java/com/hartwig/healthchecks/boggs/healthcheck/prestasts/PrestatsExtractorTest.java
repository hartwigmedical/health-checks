package com.hartwig.healthchecks.boggs.healthcheck.prestasts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.List;

import org.junit.Test;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.boggs.model.report.PrestatsDataReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.exception.EmptyFileException;

public class PrestatsExtractorTest {
    private static final String REPORT_SHOULD_NOT_BE_NULL = "Report should not be null";

    private static final String TEST_REF_ID = "CPCT12345678R";

    private static final String TEST_TUM_ID = "CPCT12345678T";

    private static final String WRONG_PATIENT_ID_MSG = "Wrong Patient Id";

    private static final String WRONG_NUMBER_OF_CHECKS_MSG = "Wrong number of checks";

    private static final int EXPECTED_CHECKS_NUM = 13;

    private static final String EMPTY_FILES = "emptyFiles";

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    @Test
    public void canProcessRunDirectoryStructure() throws IOException, EmptyFileException {
        final URL runDirURL = Resources.getResource("rundir");
        final PrestatsExtractor extractor = new PrestatsExtractor();
        final PrestatsReport prestatsData = extractor.extractFromRunDirectory(runDirURL.getPath().toString());
        assertPrestatsReport(prestatsData);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataEmptyFile() throws IOException, EmptyFileException {
        final URL runDirURL = Resources.getResource(EMPTY_FILES);
        final PrestatsExtractor extractor = new PrestatsExtractor();
        extractor.extractFromRunDirectory(runDirURL.getPath().toString());
    }

    @Test(expected = NoSuchFileException.class)
    public void extractDataNoneExistingDir() throws IOException, EmptyFileException {
        final PrestatsExtractor extractor = new PrestatsExtractor();
        extractor.extractFromRunDirectory(DUMMY_RUN_DIR);
    }

    private void assertPrestatsReport(final PrestatsReport prestatsData) {
        assertNotNull(REPORT_SHOULD_NOT_BE_NULL, prestatsData);
        assertRefSampleData(prestatsData.getReferenceSample());
        assertTumorSampleData(prestatsData.getTumorSample());
    }

    private void assertRefSampleData(final List<PrestatsDataReport> sampleData) {
        assertEquals(WRONG_NUMBER_OF_CHECKS_MSG, EXPECTED_CHECKS_NUM, sampleData.size());
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_NUMBER_OF_READS, PrestatsExtractor.FAIL,
                        TEST_REF_ID);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_TILE_SEQUENCE_QUALITY, PrestatsExtractor.WARN,
                        TEST_REF_ID);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION,
                        PrestatsExtractor.FAIL, TEST_REF_ID);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_DUPLICATION_LEVELS, PrestatsExtractor.PASS,
                        TEST_REF_ID);
    }

    private void assertTumorSampleData(final List<PrestatsDataReport> sampleData) {
        assertEquals(WRONG_NUMBER_OF_CHECKS_MSG, EXPECTED_CHECKS_NUM, sampleData.size());
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_NUMBER_OF_READS, PrestatsExtractor.FAIL,
                        TEST_TUM_ID);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_TILE_SEQUENCE_QUALITY, PrestatsExtractor.WARN,
                        TEST_TUM_ID);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION,
                        PrestatsExtractor.FAIL, TEST_TUM_ID);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_DUPLICATION_LEVELS, PrestatsExtractor.FAIL,
                        TEST_TUM_ID);
    }

    private void assertPrestatsDataReport(final List<PrestatsDataReport> sampleData, final PrestatsCheck check,
                    final String expectedStatus, final String expectedPatientId) {
        final String actualStatus = sampleData.stream().filter(p -> p.getCheckName().equals(check)).findFirst().get()
                        .getStatus();
        final String externalId = sampleData.stream().filter(p -> p.getCheckName().equals(check)).findFirst().get()
                        .getPatientId();
        assertEquals(WRONG_PATIENT_ID_MSG, expectedPatientId, externalId);
        assertEquals(WRONG_NUMBER_OF_CHECKS_MSG, expectedStatus, actualStatus);
    }
}
