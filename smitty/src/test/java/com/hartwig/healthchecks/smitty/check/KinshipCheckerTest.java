package com.hartwig.healthchecks.smitty.check;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.MalformedFileException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.dir.TestRunContextFactory;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.SingleValueResult;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class KinshipCheckerTest {

    private static final String REF_SAMPLE = "sample1";
    private static final String TUMOR_SAMPLE = "sample2";

    private static final String CORRECT_RUN = Resources.getResource("run").getPath();
    private static final String MALFORMED_RUN = Resources.getResource("run2").getPath();
    private static final String EMPTY_RUN = Resources.getResource("run3").getPath();

    @Test
    public void extractDataFromKinship() throws IOException, HealthChecksException {
        RunContext runContext = TestRunContextFactory.testContext(CORRECT_RUN, REF_SAMPLE, TUMOR_SAMPLE);

        final KinshipChecker checker = new KinshipChecker();

        final BaseResult result = checker.run(runContext);

        assertNotNull(result);
        assertEquals(CheckType.KINSHIP, result.getCheckType());
        assertKinshipData((SingleValueResult) result, "0.4748");
    }

    @Test(expected = MalformedFileException.class)
    public void cannotReadMalformedKinship() throws IOException, HealthChecksException {
        RunContext runContext = TestRunContextFactory.testContext(MALFORMED_RUN, REF_SAMPLE, TUMOR_SAMPLE);

        final KinshipChecker checker = new KinshipChecker();

        checker.run(runContext);
    }

    @Test(expected = EmptyFileException.class)
    public void cannotReadFromEmptyKinship() throws IOException, HealthChecksException {
        RunContext runContext = TestRunContextFactory.testContext(EMPTY_RUN, REF_SAMPLE, TUMOR_SAMPLE);

        final KinshipChecker checker = new KinshipChecker();

        checker.run(runContext);
    }

    @Test(expected = IOException.class)
    public void cannotReadFromNonExistingKinship() throws IOException, HealthChecksException {
        RunContext runContext = TestRunContextFactory.testContext("Does not exist", REF_SAMPLE, TUMOR_SAMPLE);

        final KinshipChecker checker = new KinshipChecker();

        checker.run(runContext);
    }

    private static void assertKinshipData(@NotNull final SingleValueResult result,
            @NotNull final String expectedValue) {
        final HealthCheck healthCheck = result.getCheck();
        assertEquals(expectedValue, healthCheck.getValue());
    }
}
