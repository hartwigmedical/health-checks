package com.hartwig.healthchecks.tony.check;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.MalformedFileException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.dir.TestRunContextFactory;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.MultiValueResult;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class CopynumberCheckerTest {
    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();

    private static final String REF_SAMPLE = "sample1";
    private static final String TUMOR_SAMPLE = "sample2";
    private static final String MALFORMED_SAMPLE = "sample3";
    private static final int EXPECTED_NUM_CHECKS = 2;
    private static final long EXPECTED_GAIN_CHECK = 252;
    private static final long EXPECTED_LOSS_CHECK = 11561;

    private final HealthChecker checker = new CopynumberChecker();

    @Test
    public void correctInputYieldsCorrectOutput() throws IOException, HealthChecksException {
        RunContext runContext = TestRunContextFactory.forTest(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);
        final BaseResult result = checker.run(runContext);
        assertResult(result);
    }

    @Test
    public void errorYieldsCorrectOutput() {
        final RunContext runContext = TestRunContextFactory.forTest(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);
        final MultiValueResult result = (MultiValueResult) checker.errorResult(runContext);
        assertEquals(2, result.getChecks().size());
    }

    @Test(expected = MalformedFileException.class)
    public void noGainLossTagsYieldMalformedException() throws IOException, HealthChecksException {
        final RunContext runContext = TestRunContextFactory.forTest(RUN_DIRECTORY, REF_SAMPLE, MALFORMED_SAMPLE);
        checker.run(runContext);
    }

    private static void assertResult(@NotNull final BaseResult baseResult) {
        final MultiValueResult result = (MultiValueResult) baseResult;
        assertEquals(CheckType.COPYNUMBER, result.getCheckType());
        assertEquals(EXPECTED_NUM_CHECKS, result.getChecks().size());

        final HealthCheck gainCheck = extractHealthCheck(result.getChecks(), CopynumberCheck.COPYNUMBER_GENOME_GAIN);

        assertEquals(TUMOR_SAMPLE, gainCheck.getSampleId());
        assertEquals(Long.toString(EXPECTED_GAIN_CHECK), gainCheck.getValue());

        final HealthCheck lossCheck = extractHealthCheck(result.getChecks(), CopynumberCheck.COPYNUMBER_GENOME_LOSS);

        assertEquals(TUMOR_SAMPLE, gainCheck.getSampleId());
        assertEquals(Long.toString(EXPECTED_LOSS_CHECK), lossCheck.getValue());
    }

    @NotNull
    private static HealthCheck extractHealthCheck(@NotNull final List<HealthCheck> checks,
            @NotNull final CopynumberCheck checkName) {
        final Optional<HealthCheck> report = checks.stream().filter(
                check -> check.getCheckName().equals(checkName.toString())).findFirst();

        assert report.isPresent();
        return report.get();
    }
}