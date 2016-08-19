package com.hartwig.healthchecks.boo.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.path.RunContextFactory;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class PrestatsExtractorTest {

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();

    private static final String TUMOR_SAMPLE = "sample2";
    private static final String TUMOR_TOTAL_SEQUENCES = "1450";

    private static final String EMPTY_SAMPLE = "sample3";
    private static final String INCORRECT_SAMPLE = "sample4";
    private static final String NON_EXISTING_SAMPLE = "sample5";

    private static final String REF_SAMPLE = "sample1";
    private static final String REF_TOTAL_SEQUENCES = "700";
    private static final int EXPECTED_CHECKS_NUM = 12;

    @Test
    public void correctInputYieldsCorrectOutput() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);

        PrestatsExtractor extractor = new PrestatsExtractor(runContext);
        final BaseReport report = extractor.extractFromRunDirectory("");
        assertReport(report);
    }

    //    @Test(expected = EmptyFileException.class)
    //    public void emptyFileYieldsEmptyFileException() throws IOException, HealthChecksException {
    //        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, EMPTY_SAMPLE, EMPTY_SAMPLE);
    //
    //        WGSMetricsExtractor extractor = new WGSMetricsExtractor(runContext);
    //        extractor.extractFromRunDirectory("");
    //    }
    //
    //    @Test(expected = IOException.class)
    //    public void nonExistingFileYieldsIOException() throws IOException, HealthChecksException {
    //        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, NON_EXISTING_SAMPLE, NON_EXISTING_SAMPLE);
    //
    //        WGSMetricsExtractor extractor = new WGSMetricsExtractor(runContext);
    //        extractor.extractFromRunDirectory("");
    //    }
    //
    //    @Test(expected = LineNotFoundException.class)
    //    public void incorrectRefFileYieldsLineNotFoundException() throws IOException, HealthChecksException {
    //        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, INCORRECT_SAMPLE, TUMOR_SAMPLE);
    //
    //        WGSMetricsExtractor extractor = new WGSMetricsExtractor(runContext);
    //        extractor.extractFromRunDirectory("");
    //    }
    //
    //    @Test(expected = LineNotFoundException.class)
    //    public void incorrectTumorFileYieldsLineNotFoundException() throws IOException, HealthChecksException {
    //        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, INCORRECT_SAMPLE);
    //
    //        WGSMetricsExtractor extractor = new WGSMetricsExtractor(runContext);
    //        extractor.extractFromRunDirectory("");
    //    }
    //
    //    @Test(expected = LineNotFoundException.class)
    //    public void incorrectFilesYieldsLineNotFoundException() throws IOException, HealthChecksException {
    //        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, INCORRECT_SAMPLE, INCORRECT_SAMPLE);
    //
    //        WGSMetricsExtractor extractor = new WGSMetricsExtractor(runContext);
    //        extractor.extractFromRunDirectory("");
    //    }
    //
    //
    //    @Test
    //    @Ignore
    //    public void canProcessRunDirectoryStructure() throws IOException, HealthChecksException {
    //        final PrestatsExtractor extractor = new PrestatsExtractor(zipFileReader, samplePathFinder);
    //        new Expectations() {
    //            {
    //                samplePathFinder.findPath(RUN_DIRECTORY, anyString, anyString);
    //                returns(new File(REF_SAMPLE).toPath());
    //                zipFileReader.readAllLinesFromZips((Path) any, PrestatsExtractor.FASTQC_CHECKS_FILE_NAME);
    //                returns(summaryDataRef);
    //
    //                zipFileReader.readFieldFromZipFiles((Path) any, FASTQC_DATA_TXT, anyString);
    //                returns(fastqLines);
    //
    //                samplePathFinder.findPath(RUN_DIRECTORY, anyString, anyString);
    //                returns(new File(TUMOR_SAMPLE).toPath());
    //                zipFileReader.readAllLinesFromZips((Path) any, PrestatsExtractor.FASTQC_CHECKS_FILE_NAME);
    //                returns(summaryDataTum);
    //
    //                zipFileReader.readFieldFromZipFiles((Path) any, FASTQC_DATA_TXT, anyString);
    //                returns(fastqLines);
    //            }
    //        };
    //        final BaseReport prestatsData = extractor.extractFromRunDirectory(RUN_DIRECTORY);
    //        assertReport(prestatsData);
    //    }
    //
    //    @Test(expected = EmptyFileException.class)
    //    public void extractDataEmptySummaryFile() throws IOException, HealthChecksException {
    //        new Expectations() {
    //            {
    //                samplePathFinder.findPath(RUN_DIRECTORY, anyString, anyString);
    //                returns(new File(REF_SAMPLE).toPath());
    //                zipFileReader.readAllLinesFromZips((Path) any, PrestatsExtractor.FASTQC_CHECKS_FILE_NAME);
    //                returns(emptyList);
    //
    //            }
    //        };
    //        final PrestatsExtractor extractor = new PrestatsExtractor(zipFileReader, samplePathFinder);
    //        extractor.extractFromRunDirectory(RUN_DIRECTORY);
    //    }
    //
    //    @Test(expected = EmptyFileException.class)
    //    public void extractDataEmptyFastqFile() throws IOException, HealthChecksException {
    //        new Expectations() {
    //            {
    //                samplePathFinder.findPath(RUN_DIRECTORY, anyString, anyString);
    //                returns(new File(REF_SAMPLE).toPath());
    //                zipFileReader.readAllLinesFromZips((Path) any, PrestatsExtractor.FASTQC_CHECKS_FILE_NAME);
    //                returns(summaryDataRef);
    //
    //                zipFileReader.readFieldFromZipFiles((Path) any, FASTQC_DATA_TXT, anyString);
    //                returns(emptyList);
    //
    //            }
    //        };
    //        final PrestatsExtractor extractor = new PrestatsExtractor(zipFileReader, samplePathFinder);
    //        extractor.extractFromRunDirectory(RUN_DIRECTORY);
    //    }
    //
    //    @Test(expected = NoSuchFileException.class)
    //    public void extractDataNoneExistingDir() throws IOException, HealthChecksException {
    //        new Expectations() {
    //            {
    //                samplePathFinder.findPath(DUMMY_RUN_DIR, anyString, anyString);
    //                result = new NoSuchFileException(DUMMY_RUN_DIR);
    //
    //            }
    //        };
    //        final PrestatsExtractor extractor = new PrestatsExtractor(zipFileReader, samplePathFinder);
    //        extractor.extractFromRunDirectory(DUMMY_RUN_DIR);
    //    }

    private static void assertReport(@NotNull final BaseReport prestatsData) {
        assertNotNull(prestatsData);
        assertEquals(CheckType.PRESTATS, prestatsData.getCheckType());
        assertRefSampleData(((SampleReport) prestatsData).getReferenceSample());
        assertTumorSampleData(((SampleReport) prestatsData).getTumorSample());
    }

    private static void assertRefSampleData(@NotNull final List<BaseDataReport> sampleData) {
        assertEquals(EXPECTED_CHECKS_NUM, sampleData.size());
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_NUMBER_OF_READS, REF_TOTAL_SEQUENCES, REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_BASE_SEQUENCE_QUALITY, PrestatsExtractor.FAIL,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_TILE_SEQUENCE_QUALITY, PrestatsExtractor.PASS,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_SEQUENCE_QUALITY_SCORES,
                PrestatsExtractor.WARN, REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_BASE_SEQUENCE_CONTENT, PrestatsExtractor.PASS,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_SEQUENCE_GC_CONTENT, PrestatsExtractor.WARN,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_BASE_N_CONTENT, PrestatsExtractor.PASS,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION,
                PrestatsExtractor.PASS, REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_DUPLICATION_LEVELS,
                PrestatsExtractor.WARN, REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_OVERREPRESENTED_SEQUENCES, PrestatsExtractor.PASS,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_ADAPTER_CONTENT, PrestatsExtractor.WARN,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_KMER_CONTENT, PrestatsExtractor.FAIL, REF_SAMPLE);
    }

    private static void assertTumorSampleData(@NotNull final List<BaseDataReport> sampleData) {
        assertEquals(EXPECTED_CHECKS_NUM, sampleData.size());
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_NUMBER_OF_READS, TUMOR_TOTAL_SEQUENCES,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_BASE_SEQUENCE_QUALITY, PrestatsExtractor.FAIL,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_TILE_SEQUENCE_QUALITY, PrestatsExtractor.PASS,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_SEQUENCE_QUALITY_SCORES,
                PrestatsExtractor.WARN, TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_BASE_SEQUENCE_CONTENT, PrestatsExtractor.FAIL,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_SEQUENCE_GC_CONTENT, PrestatsExtractor.WARN,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_BASE_N_CONTENT, PrestatsExtractor.PASS,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION,
                PrestatsExtractor.PASS, TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_DUPLICATION_LEVELS,
                PrestatsExtractor.FAIL, TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_OVERREPRESENTED_SEQUENCES, PrestatsExtractor.FAIL,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_ADAPTER_CONTENT, PrestatsExtractor.FAIL,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_KMER_CONTENT, PrestatsExtractor.PASS,
                TUMOR_SAMPLE);
    }

    private static void assertPrestatsDataReport(@NotNull final List<BaseDataReport> sampleData,
            @NotNull final PrestatsCheck check, @NotNull final String expectedStatus,
            @NotNull final String expectedSampleId) {
        Optional<BaseDataReport> optCheckReport = sampleData.stream().filter(
                p -> p.getCheckName().equals(check.toString())).findFirst();

        assert optCheckReport.isPresent();
        final String actualStatus = optCheckReport.get().getValue();
        final String sampleId = optCheckReport.get().getSampleId();

        assertEquals(expectedSampleId, sampleId);
        assertEquals(expectedStatus, actualStatus);
    }
}
