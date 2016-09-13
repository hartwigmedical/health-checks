package com.hartwig.healthchecks.nesbit.check;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.dir.TestRunContextFactory;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.PatientResult;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class GermlineCheckerTest {

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();
    private static final String REF_SAMPLE = "sample1";
    private static final String TUMOR_SAMPLE = "sample2";

    private final GermlineChecker checker = new GermlineChecker();

    @Test
    public void canCountSNPAndIndels() throws IOException, HealthChecksException {
        final RunContext runContext = TestRunContextFactory.forTest(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);
        final BaseResult result = checker.run(runContext);

        assertEquals(CheckType.GERMLINE, result.getCheckType());
        final List<HealthCheck> refData = ((PatientResult) result).getRefSampleChecks();
        final List<HealthCheck> tumData = ((PatientResult) result).getTumorSampleChecks();

        assertChecks(refData, 55, 4);
        assertChecks(tumData, 74, 4);
    }

    @Test
    public void errorYieldsCorrectOutput() {
        final RunContext runContext = TestRunContextFactory.forTest(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);
        final PatientResult result = (PatientResult) checker.errorResult(runContext);
        assertEquals(2, result.getRefSampleChecks().size());
        assertEquals(2, result.getTumorSampleChecks().size());
    }

    private static void assertChecks(@NotNull final List<HealthCheck> checks, final long expectedCountSNP,
            final long expectedCountIndels) {
        assertEquals(2, checks.size());

        final Optional<HealthCheck> snpReport = checks.stream().filter(
                data -> data.getCheckName().equals(GermlineCheck.VARIANTS_GERMLINE_SNP.toString())).findFirst();
        assert snpReport.isPresent();
        assertEquals(Long.toString(expectedCountSNP), snpReport.get().getValue());

        final Optional<HealthCheck> indelReport = checks.stream().filter(
                data -> data.getCheckName().equals(GermlineCheck.VARIANTS_GERMLINE_INDELS.toString())).findFirst();
        assert indelReport.isPresent();
        assertEquals(Long.toString(expectedCountIndels), indelReport.get().getValue());
    }
}
