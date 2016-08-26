package com.hartwig.healthchecks.smitty.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.path.RunContextFactory;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SingleValueReport;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class KinshipExtractorTest {

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();
    private static final String REF_SAMPLE = "sample1";
    private static final String TUMOR_SAMPLE = "sample2";

    @Test
    public void extractDataFromKinship() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);

        final KinshipExtractor kinshipExtractor = new KinshipExtractor(runContext);

        final BaseReport kinshipReport = kinshipExtractor.extractFromRunDirectory("");
        assertEquals(CheckType.KINSHIP, kinshipReport.getCheckType());

        assertNotNull(kinshipReport);
        assertKinshipData((SingleValueReport) kinshipReport, "0.4748");
    }

    //    @Test(expected = EmptyFileException.class)
    //    public void extractDataFromEmptyKinship() throws IOException, HealthChecksException {
    //        final KinshipExtractor kinshipExtractor = new KinshipExtractor(kinshipReader);
    //        new Expectations() {
    //            {
    //                kinshipReader.readLines(anyString, anyString);
    //                result = new EmptyFileException("", "");
    //            }
    //        };
    //        kinshipExtractor.extractFromRunDirectory(TEST_DIR);
    //    }
    //
    //    @Test(expected = IOException.class)
    //    public void extractDataFromKinshipIoException() throws IOException, HealthChecksException {
    //        final KinshipExtractor kinshipExtractor = new KinshipExtractor(kinshipReader);
    //        new Expectations() {
    //            {
    //                kinshipReader.readLines(anyString, anyString);
    //                result = new IOException();
    //            }
    //        };
    //        kinshipExtractor.extractFromRunDirectory(TEST_DIR);
    //    }
    //
    //    @Test(expected = MalformedFileException.class)
    //    public void extractDataFromKinshipMalformedFileException() throws IOException, HealthChecksException {
    //        final KinshipExtractor kinshipExtractor = new KinshipExtractor(kinshipReader);
    //        new Expectations() {
    //            {
    //                kinshipReader.readLines(anyString, anyString);
    //                returns(malformedLines);
    //            }
    //        };
    //        kinshipExtractor.extractFromRunDirectory(TEST_DIR);
    //    }

    private static void assertKinshipData(@NotNull final SingleValueReport kinshipReport,
            @NotNull final String expectedValue) {
        final BaseDataReport baseDataReport = kinshipReport.getSampleData();
        assertEquals(expectedValue, baseDataReport.getValue());
    }
}
