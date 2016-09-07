package com.hartwig.healthchecks.roz.extractor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.dir.CPCTRunContextFactory;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.SingleValueResult;

import org.junit.Test;

public class SlicedExtractorTest {

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();
    private static final String REF_SAMPLE = "CPCT11111111R";
    private static final String TUMOR_SAMPLE = "CPCT11111111T";

    @Test
    public void canAnalyseTypicalSlicedVCF() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);

        final SlicedExtractor extractor = new SlicedExtractor(runContext);

        final BaseResult report = extractor.run();
        assertEquals(CheckType.SLICED, report.getCheckType());
        final HealthCheck sampleData = ((SingleValueResult) report).getCheck();
        assertEquals(SlicedCheck.SLICED_NUMBER_OF_VARIANTS.toString(), sampleData.getCheckName());
        assertEquals(REF_SAMPLE, sampleData.getSampleId());
        assertEquals("4", sampleData.getValue());
    }

    @Test(expected = IOException.class)
    public void readingNonExistingFileYieldsIOException() throws IOException, HealthChecksException {
        RunContext runContext = CPCTRunContextFactory.testContext("DoesNotExist", REF_SAMPLE, TUMOR_SAMPLE);

        final SlicedExtractor extractor = new SlicedExtractor(runContext);
        extractor.run();
    }
}
