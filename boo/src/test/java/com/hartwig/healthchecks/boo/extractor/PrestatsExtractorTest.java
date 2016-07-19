package com.hartwig.healthchecks.boo.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.extractor.AbstractDataExtractor;
import com.hartwig.healthchecks.common.io.reader.ZipFileReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import mockit.Expectations;
import mockit.Mocked;

public class PrestatsExtractorTest {

    private static final String WRONG_CHECK_VALUE_MSG = "Wrong Check Value";

    private static final String FASTQC_DATA_TXT = "fastqc_data.txt";

    private static final String REPORT_SHOULD_NOT_BE_NULL = "Report should not be null";

    private static final String TEST_REF_ID = "CPCT12345678R";

    private static final String TEST_TUM_ID = "CPCT12345678T";

    private static final String WRONG_PATIENT_ID_MSG = "Wrong Patient Id";

    private static final String WRONG_NUMBER_OF_CHECKS_MSG = "Wrong number of checks";

    private static final int EXPECTED_CHECKS_NUM = 13;

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    private static final String RUNDIR = "rundir";

    private List<String> summaryDataRef;

    private List<String> summaryDataTum;

    private List<String> fastqLines;

    private List<String> emptyList;

    @Mocked
    private ZipFileReader zipFileReader;

    @Before
    public void setUp() {
        summaryDataRef = new ArrayList<>();
        summaryDataRef.addAll(TestZipFileFactory.getSummaryLines("L001_R1", "R", "PASS", "PASS", "PASS"));
        summaryDataRef.addAll(TestZipFileFactory.getSummaryLines("L002_R1", "R", "WARN", "WARN", "PASS"));
        summaryDataRef.addAll(TestZipFileFactory.getSummaryLines("L001_R2", "R", "PASS", "FAIL", "PASS"));
        summaryDataRef.addAll(TestZipFileFactory.getSummaryLines("L002_R2", "R", "PASS", "PASS", "PASS"));
        summaryDataTum = new ArrayList<>();
        summaryDataTum.addAll(TestZipFileFactory.getSummaryLines("L001_R1", "R", "PASS", "PASS", "PASS"));
        summaryDataTum.addAll(TestZipFileFactory.getSummaryLines("L002_R1", "R", "WARN", "WARN", "PASS"));
        summaryDataTum.addAll(TestZipFileFactory.getSummaryLines("L001_R2", "R", "PASS", "PASS", "PASS"));
        summaryDataTum.addAll(TestZipFileFactory.getSummaryLines("L002_R2", "R", "PASS", "FAIL", "PASS"));

        fastqLines = TestZipFileFactory.getFastqLines();

        emptyList = new ArrayList<>();
    }

    @Test
    public void canProcessRunDirectoryStructure() throws IOException, HealthChecksException {
        final PrestatsExtractor extractor = new PrestatsExtractor(zipFileReader);
        new Expectations() {

            {
                zipFileReader.getZipFilesPath(RUNDIR, anyString, anyString);
                returns(new File(TEST_REF_ID).toPath());
                zipFileReader.readAllLinesFromZips((Path) any, PrestatsExtractor.SUMMARY_FILE_NAME);
                returns(summaryDataRef);

                zipFileReader.readFieldFromZipFiles((Path) any, FASTQC_DATA_TXT, anyString);
                returns(fastqLines);

                zipFileReader.getZipFilesPath(RUNDIR, anyString, anyString);
                returns(new File(TEST_TUM_ID).toPath());
                zipFileReader.readAllLinesFromZips((Path) any, PrestatsExtractor.SUMMARY_FILE_NAME);
                returns(summaryDataTum);

                zipFileReader.readFieldFromZipFiles((Path) any, FASTQC_DATA_TXT, anyString);
                returns(fastqLines);
            }
        };
        final BaseReport prestatsData = extractor.extractFromRunDirectory(RUNDIR);
        assertPrestatsReport(prestatsData);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataEmptySummaryFile() throws IOException, HealthChecksException {
        new Expectations() {

            {
                zipFileReader.getZipFilesPath(RUNDIR, anyString, anyString);
                returns(new File(TEST_REF_ID).toPath());
                zipFileReader.readAllLinesFromZips((Path) any, PrestatsExtractor.SUMMARY_FILE_NAME);
                returns(emptyList);

            }
        };
        final PrestatsExtractor extractor = new PrestatsExtractor(zipFileReader);
        extractor.extractFromRunDirectory(RUNDIR);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataEmptyFastqFile() throws IOException, HealthChecksException {
        new Expectations() {

            {
                zipFileReader.getZipFilesPath(RUNDIR, anyString, anyString);
                returns(new File(TEST_REF_ID).toPath());
                zipFileReader.readAllLinesFromZips((Path) any, PrestatsExtractor.SUMMARY_FILE_NAME);
                returns(summaryDataRef);

                zipFileReader.readFieldFromZipFiles((Path) any, FASTQC_DATA_TXT, anyString);
                returns(emptyList);

            }
        };
        final PrestatsExtractor extractor = new PrestatsExtractor(zipFileReader);
        extractor.extractFromRunDirectory(RUNDIR);
    }

    @Test(expected = NoSuchFileException.class)
    public void extractDataNoneExistingDir() throws IOException, HealthChecksException {
        new Expectations() {

            {
                zipFileReader.getZipFilesPath(DUMMY_RUN_DIR, anyString, anyString);
                result = new NoSuchFileException(DUMMY_RUN_DIR);

            }
        };
        final PrestatsExtractor extractor = new PrestatsExtractor(zipFileReader);
        extractor.extractFromRunDirectory(DUMMY_RUN_DIR);
    }

    private void assertPrestatsReport(final BaseReport prestatsData) {
        assertNotNull(REPORT_SHOULD_NOT_BE_NULL, prestatsData);
        assertEquals("Report with wrong type", CheckType.PRESTATS, prestatsData.getCheckType());
        assertRefSampleData(((SampleReport) prestatsData).getReferenceSample());
        assertTumorSampleData(((SampleReport) prestatsData).getTumorSample());
    }

    private void assertRefSampleData(final List<BaseDataReport> sampleData) {
        assertEquals(WRONG_NUMBER_OF_CHECKS_MSG, EXPECTED_CHECKS_NUM, sampleData.size());
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_NUMBER_OF_READS.getDescription(),
                        AbstractDataExtractor.FAIL, TEST_REF_ID);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_TILE_SEQUENCE_QUALITY.getDescription(),
                        AbstractDataExtractor.WARN, TEST_REF_ID);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION.getDescription(),
                        AbstractDataExtractor.FAIL, TEST_REF_ID);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_DUPLICATION_LEVELS.getDescription(),
                        AbstractDataExtractor.PASS, TEST_REF_ID);
    }

    private void assertTumorSampleData(final List<BaseDataReport> sampleData) {
        assertEquals(WRONG_NUMBER_OF_CHECKS_MSG, EXPECTED_CHECKS_NUM, sampleData.size());
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_NUMBER_OF_READS.getDescription(),
                        AbstractDataExtractor.FAIL, TEST_TUM_ID);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_TILE_SEQUENCE_QUALITY.getDescription(),
                        AbstractDataExtractor.WARN, TEST_TUM_ID);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION.getDescription(),
                        AbstractDataExtractor.FAIL, TEST_TUM_ID);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_DUPLICATION_LEVELS.getDescription(),
                        AbstractDataExtractor.PASS, TEST_TUM_ID);
    }

    private void assertPrestatsDataReport(final List<BaseDataReport> sampleData, final String check,
                    final String expectedStatus, final String expectedPatientId) {
        final String actualStatus = sampleData.stream().filter(p -> p.getCheckName().equals(check)).findFirst().get()
                        .getValue();
        final String externalId = sampleData.stream().filter(p -> p.getCheckName().equals(check)).findFirst().get()
                        .getPatientId();
        assertEquals(WRONG_PATIENT_ID_MSG, expectedPatientId, externalId);
        assertEquals(WRONG_CHECK_VALUE_MSG, expectedStatus, actualStatus);
    }
}
