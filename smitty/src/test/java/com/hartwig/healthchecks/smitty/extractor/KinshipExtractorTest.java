package com.hartwig.healthchecks.smitty.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.data.BaseResult;
import com.hartwig.healthchecks.common.data.SingleValueResult;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.MalformedFileException;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.path.RunContextFactory;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class KinshipExtractorTest {

    private static final String REF_SAMPLE = "sample1";
    private static final String TUMOR_SAMPLE = "sample2";

    private static final String CORRECT_RUN = Resources.getResource("run").getPath();
    private static final String MALFORMED_RUN = Resources.getResource("run2").getPath();
    private static final String EMPTY_RUN = Resources.getResource("run3").getPath();

    @Test
    public void extractDataFromKinship() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(CORRECT_RUN, REF_SAMPLE, TUMOR_SAMPLE);

        final KinshipExtractor kinshipExtractor = new KinshipExtractor(runContext);

        final BaseResult kinshipReport = kinshipExtractor.extract();

        assertNotNull(kinshipReport);
        assertEquals(CheckType.KINSHIP, kinshipReport.getCheckType());
        assertKinshipData((SingleValueResult) kinshipReport, "0.4748");
    }

    @Test(expected = MalformedFileException.class)
    public void cannotReadMalformedKinship() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(MALFORMED_RUN, REF_SAMPLE, TUMOR_SAMPLE);

        final KinshipExtractor kinshipExtractor = new KinshipExtractor(runContext);

        kinshipExtractor.extract();
    }

    @Test(expected = EmptyFileException.class)
    public void cannotReadFromEmptyKinship() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(EMPTY_RUN, REF_SAMPLE, TUMOR_SAMPLE);

        final KinshipExtractor kinshipExtractor = new KinshipExtractor(runContext);

        kinshipExtractor.extract();
    }

    @Test(expected = IOException.class)
    public void cannotReadFromNonExistingKinship() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext("Does not exist", REF_SAMPLE, TUMOR_SAMPLE);

        final KinshipExtractor kinshipExtractor = new KinshipExtractor(runContext);

        kinshipExtractor.extract();
    }

    private static void assertKinshipData(@NotNull final SingleValueResult kinshipReport,
            @NotNull final String expectedValue) {
        final HealthCheck healthCheck = kinshipReport.getCheck();
        assertEquals(expectedValue, healthCheck.getValue());
    }
}
