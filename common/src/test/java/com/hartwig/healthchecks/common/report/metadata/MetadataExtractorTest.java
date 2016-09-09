package com.hartwig.healthchecks.common.report.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.dir.TestRunContextFactory;

import org.junit.Test;

public class MetadataExtractorTest {

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();

    private static final String EXPECTED_VERSION = "v1.7";
    private static final String EXPECTED_DATE = "2016-07-09";

    private final MetadataExtractor extractor = new MetadataExtractor();

    @Test
    public void canReadCorrectMetaData() throws IOException, HealthChecksException {
        RunContext runContext = TestRunContextFactory.forTest(RUN_DIRECTORY);
        final ReportMetadata reportMetadata = extractor.extractMetadata(runContext);

        assertNotNull(reportMetadata);
        assertEquals(EXPECTED_DATE, reportMetadata.getDate());
        assertEquals(EXPECTED_VERSION, reportMetadata.getPipelineVersion());
    }
}
