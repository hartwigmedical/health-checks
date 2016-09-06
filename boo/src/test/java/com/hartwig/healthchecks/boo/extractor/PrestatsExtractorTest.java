package com.hartwig.healthchecks.boo.extractor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.dir.CPCTRunContextFactory;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.PatientResult;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class PrestatsExtractorTest {

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();

    private static final String TUMOR_SAMPLE = "sample2";
    private static final String TUMOR_TOTAL_SEQUENCES = "1450";

    private static final String EMPTY_FASTQC_SAMPLE = "sample3";
    private static final String EMPTY_TOTAL_SEQUENCE_SAMPLE = "sample4";
    private static final String INCOMPLETE_SAMPLE = "sample5";
    private static final String NON_EXISTING_SAMPLE = "sample6";

    private static final String REF_SAMPLE = "sample1";
    private static final String REF_TOTAL_SEQUENCES = "700";
    private static final int EXPECTED_CHECKS_NUM = 12;

    @Test
    public void correctInputYieldsCorrectOutput() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);

        PrestatsExtractor extractor = new PrestatsExtractor(runContext);
        final BaseResult report = extractor.extract();
        assertReport(report);
    }

    @Test(expected = EmptyFileException.class)
    public void emptyFastQCFileYieldsEmptyFileException() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, EMPTY_FASTQC_SAMPLE,
                EMPTY_FASTQC_SAMPLE);

        PrestatsExtractor extractor = new PrestatsExtractor(runContext);
        extractor.extract();
    }

    @Test(expected = EmptyFileException.class)
    public void emptyTotalSequenceFileYieldsEmptyFileException() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, EMPTY_TOTAL_SEQUENCE_SAMPLE,
                EMPTY_TOTAL_SEQUENCE_SAMPLE);

        PrestatsExtractor extractor = new PrestatsExtractor(runContext);
        extractor.extract();
    }

    @Test
    public void incompleteInputYieldsIncompleteOutput() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, INCOMPLETE_SAMPLE, INCOMPLETE_SAMPLE);

        PrestatsExtractor extractor = new PrestatsExtractor(runContext);
        final BaseResult report = extractor.extract();
        final List<HealthCheck> sampleReport = ((PatientResult) report).getRefSampleChecks();
        assertEquals(EXPECTED_CHECKS_NUM, sampleReport.size());

        assertPrestatsDataReport(sampleReport, PrestatsCheck.PRESTATS_SEQUENCE_DUPLICATION_LEVELS,
                PrestatsExtractor.MISS, INCOMPLETE_SAMPLE);
    }

    @Test(expected = IOException.class)
    public void nonExistingFileYieldsIOException() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, NON_EXISTING_SAMPLE,
                NON_EXISTING_SAMPLE);

        PrestatsExtractor extractor = new PrestatsExtractor(runContext);
        extractor.extract();
    }

    private static void assertReport(@NotNull final BaseResult prestatsData) {
        assertEquals(CheckType.PRESTATS, prestatsData.getCheckType());
        assertRefSampleData(((PatientResult) prestatsData).getRefSampleChecks());
        assertTumorSampleData(((PatientResult) prestatsData).getTumorSampleChecks());
    }

    private static void assertRefSampleData(@NotNull final List<HealthCheck> sampleData) {
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

    private static void assertTumorSampleData(@NotNull final List<HealthCheck> sampleData) {
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

    private static void assertPrestatsDataReport(@NotNull final List<HealthCheck> sampleData,
            @NotNull final PrestatsCheck check, @NotNull final String expectedStatus,
            @NotNull final String expectedSampleId) {
        Optional<HealthCheck> optCheckReport = sampleData.stream().filter(
                p -> p.getCheckName().equals(check.toString())).findFirst();

        assert optCheckReport.isPresent();
        final String actualStatus = optCheckReport.get().getValue();
        final String sampleId = optCheckReport.get().getSampleId();

        assertEquals(expectedSampleId, sampleId);
        assertEquals(expectedStatus, actualStatus);
    }
}
