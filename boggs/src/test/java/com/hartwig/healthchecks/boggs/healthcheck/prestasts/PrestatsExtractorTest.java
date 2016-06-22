package com.hartwig.healthchecks.boggs.healthcheck.prestasts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.boggs.healthcheck.reader.TestZipFileFactory;
import com.hartwig.healthchecks.boggs.model.report.PrestatsDataReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.boggs.reader.ZipFileReader;
import com.hartwig.healthchecks.common.exception.EmptyFileException;

import mockit.Expectations;
import mockit.Mocked;

public class PrestatsExtractorTest {

    private static final String FASTQC_DATA_TXT = "fastqc_data.txt";

    private static final String REPORT_SHOULD_NOT_BE_NULL = "Report should not be null";

    private static final String TEST_REF_ID = "CPCT12345678R";

    private static final String TEST_TUM_ID = "CPCT12345678T";

    private static final String WRONG_PATIENT_ID_MSG = "Wrong Patient Id";

    private static final String WRONG_NUMBER_OF_CHECKS_MSG = "Wrong number of checks";

    private static final int EXPECTED_CHECKS_NUM = 13;

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    private static final String RUNDIR = "rundir";

    private List<String> firstRList;

    private List<String> secondRList;

    private List<String> thridRList;

    private List<String> forthRList;

    private List<String> firstTList;

    private List<String> secondTList;

    private List<String> thridTList;

    private List<String> forthTList;

    private List<String> fastqLines;

    private List<String> emptyList;

    @Mocked
    private ZipFileReader zipFileReader;

    @Before
    public void setUp() {
        firstRList = TestZipFileFactory.getSummaryLines("L001_R1", "R", "PASS", "PASS", "PASS");
        secondRList = TestZipFileFactory.getSummaryLines("L002_R1", "R", "WARN", "WARN", "PASS");
        thridRList = TestZipFileFactory.getSummaryLines("L001_R2", "R", "PASS", "FAIL", "PASS");
        forthRList = TestZipFileFactory.getSummaryLines("L002_R2", "R", "PASS", "PASS", "PASS");

        firstTList = TestZipFileFactory.getSummaryLines("L001_R1", "R", "PASS", "PASS", "PASS");
        secondTList = TestZipFileFactory.getSummaryLines("L002_R1", "R", "WARN", "WARN", "PASS");
        thridTList = TestZipFileFactory.getSummaryLines("L001_R2", "R", "PASS", "PASS", "PASS");
        forthTList = TestZipFileFactory.getSummaryLines("L002_R2", "R", "PASS", "FAIL", "PASS");

        fastqLines = TestZipFileFactory.getFastqLines();

        emptyList = new ArrayList<>();
    }

    @Test
    public void canProcessRunDirectoryStructure() throws IOException, EmptyFileException {
        final URL runDirURL = Resources.getResource(RUNDIR);
        final PrestatsExtractor extractor = new PrestatsExtractor(zipFileReader);
        new Expectations() {
            {
                zipFileReader.readFileFromZip(anyString, PrestatsExtractor.SUMMARY_FILE_NAME);
                returns(firstRList, secondRList, thridRList, forthRList);

                zipFileReader.readFileFromZip(anyString, FASTQC_DATA_TXT);
                returns(fastqLines, fastqLines, fastqLines, fastqLines);

                zipFileReader.readFileFromZip(anyString, PrestatsExtractor.SUMMARY_FILE_NAME);
                returns(firstTList, secondTList, thridTList, forthTList);
                zipFileReader.readFileFromZip(anyString, FASTQC_DATA_TXT);
                returns(fastqLines, fastqLines, fastqLines, fastqLines);

            }
        };
        final PrestatsReport prestatsData = extractor.extractFromRunDirectory(runDirURL.getPath().toString());
        assertPrestatsReport(prestatsData);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataEmptySummaryFile() throws IOException, EmptyFileException {
        final URL runDirURL = Resources.getResource(RUNDIR);
        new Expectations() {
            {
                zipFileReader.readFileFromZip(anyString, PrestatsExtractor.SUMMARY_FILE_NAME);
                returns(emptyList, emptyList, emptyList, emptyList);

            }
        };
        final PrestatsExtractor extractor = new PrestatsExtractor(zipFileReader);
        extractor.extractFromRunDirectory(runDirURL.getPath().toString());
    }

    @Test(expected = NoSuchFileException.class)
    public void extractDataNoneExistingDir() throws IOException, EmptyFileException {
        final PrestatsExtractor extractor = new PrestatsExtractor(zipFileReader);
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
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_DUPLICATION_LEVELS, PrestatsExtractor.PASS,
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
