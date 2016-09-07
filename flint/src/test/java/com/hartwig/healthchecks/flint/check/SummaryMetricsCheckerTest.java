package com.hartwig.healthchecks.flint.check;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.dir.CPCTRunContextFactory;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.PatientResult;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class SummaryMetricsCheckerTest {

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();

    private static final String REF_SAMPLE = "sample1";
    private static final String REF_PF_MISMATCH_RATE = "0.006024";
    private static final String REF_PF_INDEL_RATE = "0.000261";
    private static final String REF_STRAND_BALANCE = "0.399972";
    private static final String REF_PCT_CHIMERA = "0.000212";
    private static final String REF_PCT_ADAPTER = "0.000046";

    private static final String TUMOR_SAMPLE = "sample2";
    private static final String TUMOR_PF_MISMATCH_RATE = "0.005024";
    private static final String TUMOR_PF_INDEL_RATE = "0.000262";
    private static final String TUMOR_STRAND_BALANCE = "0.499972";
    private static final String TUMOR_PCT_CHIMERA = "0.000112";
    private static final String TUMOR_PCT_ADAPTER = "0.000056";

    private static final String EMPTY_SAMPLE = "sample3";
    private static final String INCORRECT_SAMPLE = "sample4";
    private static final String NON_EXISTING_SAMPLE = "sample5";

    @Test
    public void correctInputYieldsCorrectOutput() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);

        SummaryMetricsChecker checker = new SummaryMetricsChecker(runContext);
        final BaseResult result = checker.run();
        assertResult(result);
    }

    @Test(expected = EmptyFileException.class)
    public void emptyFileYieldsEmptyFileException() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, EMPTY_SAMPLE, EMPTY_SAMPLE);

        SummaryMetricsChecker checker = new SummaryMetricsChecker(runContext);
        checker.run();
    }

    @Test(expected = IOException.class)
    public void nonExistingFileYieldsIOException() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, NON_EXISTING_SAMPLE,
                NON_EXISTING_SAMPLE);

        SummaryMetricsChecker checker = new SummaryMetricsChecker(runContext);
        checker.run();
    }

    @Test(expected = LineNotFoundException.class)
    public void incorrectRefFileYieldsLineNotFoundException() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, INCORRECT_SAMPLE, TUMOR_SAMPLE);

        SummaryMetricsChecker checker = new SummaryMetricsChecker(runContext);
        checker.run();
    }

    @Test(expected = LineNotFoundException.class)
    public void incorrectTumorFileYieldsLineNotFoundException() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, INCORRECT_SAMPLE);

        SummaryMetricsChecker checker = new SummaryMetricsChecker(runContext);
        checker.run();
    }

    @Test(expected = LineNotFoundException.class)
    public void incorrectFilesYieldsLineNotFoundException() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, INCORRECT_SAMPLE, INCORRECT_SAMPLE);

        SummaryMetricsChecker checker = new SummaryMetricsChecker(runContext);
        checker.run();
    }

    private static void assertResult(@NotNull final BaseResult result) {
        assertEquals(CheckType.SUMMARY_METRICS, result.getCheckType());
        assertNotNull(result);
        assertField(result, SummaryMetricsCheck.MAPPING_PF_MISMATCH_RATE.toString(), REF_PF_MISMATCH_RATE,
                TUMOR_PF_MISMATCH_RATE);
        assertField(result, SummaryMetricsCheck.MAPPING_PF_INDEL_RATE.toString(), REF_PF_INDEL_RATE,
                TUMOR_PF_INDEL_RATE);
        assertField(result, SummaryMetricsCheck.MAPPING_STRAND_BALANCE.toString(), REF_STRAND_BALANCE,
                TUMOR_STRAND_BALANCE);
        assertField(result, SummaryMetricsCheck.MAPPING_PCT_CHIMERA.toString(), REF_PCT_CHIMERA, TUMOR_PCT_CHIMERA);
        assertField(result, SummaryMetricsCheck.MAPPING_PCT_ADAPTER.toString(), REF_PCT_ADAPTER, TUMOR_PCT_ADAPTER);
    }

    private static void assertField(@NotNull final BaseResult result, @NotNull final String field,
            @NotNull final String refValue, @NotNull final String tumValue) {
        assertBaseData(((PatientResult) result).getRefSampleChecks(), REF_SAMPLE, field, refValue);
        assertBaseData(((PatientResult) result).getTumorSampleChecks(), TUMOR_SAMPLE, field, tumValue);
    }

    private static void assertBaseData(@NotNull final List<HealthCheck> checks, @NotNull final String sampleId,
            @NotNull final String checkName, @NotNull final String expectedValue) {
        final Optional<HealthCheck> value = checks.stream().filter(
                p -> p.getCheckName().equals(checkName)).findFirst();
        assert value.isPresent();

        assertEquals(expectedValue, value.get().getValue());
        assertEquals(sampleId, value.get().getSampleId());
    }
}
