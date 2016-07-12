package com.hartwig.healthchecks.bile.extractor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.reader.Reader;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import mockit.Expectations;
import mockit.Mocked;

public class RealignerExtractorTest {

    private static final String TEST_DIR = "Test";

    private static final String REPORT_WITH_WRONG_TYPE = "Report with wrong type";

    @Mocked
    private Reader reader;

    private List<String> lines;

    @Before
    public void setUp() {
        lines = Arrays.asList("Test");

    }

    @Test
    public void extractData() throws IOException, HealthChecksException {

        final RealignerExtractor extractor = new RealignerExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString);
                returns(lines);
            }
        };
        final BaseReport report = extractor.extractFromRunDirectory(TEST_DIR);
        assertEquals(REPORT_WITH_WRONG_TYPE, CheckType.REALIGNER, report.getCheckType());
    }

}
