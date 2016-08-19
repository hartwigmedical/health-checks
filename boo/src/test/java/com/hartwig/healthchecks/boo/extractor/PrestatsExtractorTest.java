package com.hartwig.healthchecks.boo.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.SamplePathFinder;
import com.hartwig.healthchecks.common.io.reader.ZipFilesReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;

public class PrestatsExtractorTest {

    private static final String FASTQC_DATA_TXT = "fastqc_data.txt";
    private static final String REF_SAMPLE = "CPCT12345678R";
    private static final String TUMOR_SAMPLE = "CPCT12345678T";
    private static final int EXPECTED_CHECKS_NUM = 13;
    private static final String DUMMY_RUN_DIR = "DummyRunDir";
    private static final String RUNDIR = "rundir";

    private List<String> summaryDataRef;
    private List<String> summaryDataTum;
    private List<String> fastqLines;
    private List<String> emptyList;

    @Mocked
    private ZipFilesReader zipFileReader;

    @Mocked
    private SamplePathFinder samplePathFinder;

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
    @Ignore
    public void canProcessRunDirectoryStructure() throws IOException, HealthChecksException {
        final PrestatsExtractor extractor = new PrestatsExtractor(zipFileReader, samplePathFinder);
        new Expectations() {
            {
                samplePathFinder.findPath(RUNDIR, anyString, anyString);
                returns(new File(REF_SAMPLE).toPath());
                zipFileReader.readAllLinesFromZips((Path) any, PrestatsExtractor.SUMMARY_FILE_NAME);
                returns(summaryDataRef);

                zipFileReader.readFieldFromZipFiles((Path) any, FASTQC_DATA_TXT, anyString);
                returns(fastqLines);

                samplePathFinder.findPath(RUNDIR, anyString, anyString);
                returns(new File(TUMOR_SAMPLE).toPath());
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
                samplePathFinder.findPath(RUNDIR, anyString, anyString);
                returns(new File(REF_SAMPLE).toPath());
                zipFileReader.readAllLinesFromZips((Path) any, PrestatsExtractor.SUMMARY_FILE_NAME);
                returns(emptyList);

            }
        };
        final PrestatsExtractor extractor = new PrestatsExtractor(zipFileReader, samplePathFinder);
        extractor.extractFromRunDirectory(RUNDIR);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataEmptyFastqFile() throws IOException, HealthChecksException {
        new Expectations() {
            {
                samplePathFinder.findPath(RUNDIR, anyString, anyString);
                returns(new File(REF_SAMPLE).toPath());
                zipFileReader.readAllLinesFromZips((Path) any, PrestatsExtractor.SUMMARY_FILE_NAME);
                returns(summaryDataRef);

                zipFileReader.readFieldFromZipFiles((Path) any, FASTQC_DATA_TXT, anyString);
                returns(emptyList);

            }
        };
        final PrestatsExtractor extractor = new PrestatsExtractor(zipFileReader, samplePathFinder);
        extractor.extractFromRunDirectory(RUNDIR);
    }

    @Test(expected = NoSuchFileException.class)
    public void extractDataNoneExistingDir() throws IOException, HealthChecksException {
        new Expectations() {
            {
                samplePathFinder.findPath(DUMMY_RUN_DIR, anyString, anyString);
                result = new NoSuchFileException(DUMMY_RUN_DIR);

            }
        };
        final PrestatsExtractor extractor = new PrestatsExtractor(zipFileReader, samplePathFinder);
        extractor.extractFromRunDirectory(DUMMY_RUN_DIR);
    }

    private void assertPrestatsReport(@NotNull final BaseReport prestatsData) {
        assertNotNull(prestatsData);
        assertEquals(CheckType.PRESTATS, prestatsData.getCheckType());
        assertRefSampleData(((SampleReport) prestatsData).getReferenceSample());
        assertTumorSampleData(((SampleReport) prestatsData).getTumorSample());
    }

    private static void assertRefSampleData(@NotNull final List<BaseDataReport> sampleData) {
        assertEquals(EXPECTED_CHECKS_NUM, sampleData.size());
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_NUMBER_OF_READS.getDescription(),
                PrestatsExtractor.FAIL, REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_TILE_SEQUENCE_QUALITY.getDescription(),
                PrestatsExtractor.WARN, REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION.getDescription(),
                PrestatsExtractor.FAIL, REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_DUPLICATION_LEVELS.getDescription(),
                PrestatsExtractor.PASS, REF_SAMPLE);
    }

    private static void assertTumorSampleData(@NotNull final List<BaseDataReport> sampleData) {
        assertEquals(EXPECTED_CHECKS_NUM, sampleData.size());
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_NUMBER_OF_READS.getDescription(),
                PrestatsExtractor.FAIL, TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_TILE_SEQUENCE_QUALITY.getDescription(),
                PrestatsExtractor.WARN, TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION.getDescription(),
                PrestatsExtractor.FAIL, TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_DUPLICATION_LEVELS.getDescription(),
                PrestatsExtractor.PASS, TUMOR_SAMPLE);
    }

    private static void assertPrestatsDataReport(@NotNull final List<BaseDataReport> sampleData,
            @NotNull final String check, @NotNull final String expectedStatus,
            @NotNull final String expectedSampleId) {
        Optional<BaseDataReport> optCheckReport = sampleData.stream().filter(
                p -> p.getCheckName().equals(check)).findFirst();

        assert optCheckReport.isPresent();
        final String actualStatus = optCheckReport.get().getValue();
        final String sampleId = optCheckReport.get().getValue();

        assertEquals(expectedSampleId, sampleId);
        assertEquals(expectedStatus, actualStatus);
    }
}
