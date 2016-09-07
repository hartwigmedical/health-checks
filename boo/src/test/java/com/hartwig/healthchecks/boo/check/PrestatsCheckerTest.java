package com.hartwig.healthchecks.boo.check;

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

public class PrestatsCheckerTest {

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

        PrestatsChecker checker = new PrestatsChecker(runContext);
        final BaseResult result = checker.run();
        assertReport(result);
    }

    @Test(expected = EmptyFileException.class)
    public void emptyFastQCFileYieldsEmptyFileException() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, EMPTY_FASTQC_SAMPLE,
                EMPTY_FASTQC_SAMPLE);

        PrestatsChecker checker = new PrestatsChecker(runContext);
        checker.run();
    }

    @Test(expected = EmptyFileException.class)
    public void emptyTotalSequenceFileYieldsEmptyFileException() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, EMPTY_TOTAL_SEQUENCE_SAMPLE,
                EMPTY_TOTAL_SEQUENCE_SAMPLE);

        PrestatsChecker checker = new PrestatsChecker(runContext);
        checker.run();
    }

    @Test
    public void incompleteInputYieldsIncompleteOutput() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, INCOMPLETE_SAMPLE, INCOMPLETE_SAMPLE);

        PrestatsChecker extractor = new PrestatsChecker(runContext);
        final BaseResult result = extractor.run();
        final List<HealthCheck> refResults = ((PatientResult) result).getRefSampleChecks();
        assertEquals(EXPECTED_CHECKS_NUM, refResults.size());

        assertPrestatsDataReport(refResults, PrestatsCheck.PRESTATS_SEQUENCE_DUPLICATION_LEVELS, PrestatsChecker.MISS,
                INCOMPLETE_SAMPLE);
    }

    @Test(expected = IOException.class)
    public void nonExistingFileYieldsIOException() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, NON_EXISTING_SAMPLE,
                NON_EXISTING_SAMPLE);

        PrestatsChecker checker = new PrestatsChecker(runContext);
        checker.run();
    }

    private static void assertReport(@NotNull final BaseResult prestatsData) {
        assertEquals(CheckType.PRESTATS, prestatsData.getCheckType());
        assertRefSampleData(((PatientResult) prestatsData).getRefSampleChecks());
        assertTumorSampleData(((PatientResult) prestatsData).getTumorSampleChecks());
    }

    private static void assertRefSampleData(@NotNull final List<HealthCheck> sampleData) {
        assertEquals(EXPECTED_CHECKS_NUM, sampleData.size());
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_NUMBER_OF_READS, REF_TOTAL_SEQUENCES, REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_BASE_SEQUENCE_QUALITY, PrestatsChecker.FAIL,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_TILE_SEQUENCE_QUALITY, PrestatsChecker.PASS,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_SEQUENCE_QUALITY_SCORES, PrestatsChecker.WARN,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_BASE_SEQUENCE_CONTENT, PrestatsChecker.PASS,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_SEQUENCE_GC_CONTENT, PrestatsChecker.WARN,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_BASE_N_CONTENT, PrestatsChecker.PASS,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION, PrestatsChecker.PASS,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_DUPLICATION_LEVELS, PrestatsChecker.WARN,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_OVERREPRESENTED_SEQUENCES, PrestatsChecker.PASS,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_ADAPTER_CONTENT, PrestatsChecker.WARN,
                REF_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_KMER_CONTENT, PrestatsChecker.FAIL, REF_SAMPLE);
    }

    private static void assertTumorSampleData(@NotNull final List<HealthCheck> sampleData) {
        assertEquals(EXPECTED_CHECKS_NUM, sampleData.size());
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_NUMBER_OF_READS, TUMOR_TOTAL_SEQUENCES,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_BASE_SEQUENCE_QUALITY, PrestatsChecker.FAIL,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_TILE_SEQUENCE_QUALITY, PrestatsChecker.PASS,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_SEQUENCE_QUALITY_SCORES, PrestatsChecker.WARN,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_BASE_SEQUENCE_CONTENT, PrestatsChecker.FAIL,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_SEQUENCE_GC_CONTENT, PrestatsChecker.WARN,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_PER_BASE_N_CONTENT, PrestatsChecker.PASS,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_LENGTH_DISTRIBUTION, PrestatsChecker.PASS,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_SEQUENCE_DUPLICATION_LEVELS, PrestatsChecker.FAIL,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_OVERREPRESENTED_SEQUENCES, PrestatsChecker.FAIL,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_ADAPTER_CONTENT, PrestatsChecker.FAIL,
                TUMOR_SAMPLE);
        assertPrestatsDataReport(sampleData, PrestatsCheck.PRESTATS_KMER_CONTENT, PrestatsChecker.PASS,
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
