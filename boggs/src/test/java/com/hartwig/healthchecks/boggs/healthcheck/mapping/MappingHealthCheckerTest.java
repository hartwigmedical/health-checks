package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.hartwig.healthchecks.boggs.model.report.MappingDataReport;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;

public class MappingHealthCheckerTest {

    private static final String _8564 = "8564";
    private static final String SOME_EXTERNAL_ID = "SomeExternalId";
    private static final String DUMMY_RUN_DIR = "DummyRunDir";
    @Mocked private MappingExtractor dataExtractor;

    @NotNull private static MappingReport dummyData() {
        MappingDataReport mappingDataReport = new MappingDataReport(1.0d, 2.0d, 2.0d, 1.0d, 0.2d, true);
        return new MappingReport(CheckType.MAPPING, SOME_EXTERNAL_ID, _8564, mappingDataReport);
    }

    @Test public void verifyMappingHealthChecker() throws IOException, EmptyFileException {

        final HealthChecker checker = new MappingHealthChecker(DUMMY_RUN_DIR, dataExtractor);
        new Expectations() {
            {
                dataExtractor.extractFromRunDirectory(anyString);
                returns(dummyData());
            }
        };

        final BaseReport report = checker.runCheck();

        assertEquals("Report with wrong type", CheckType.MAPPING, report.getCheckType());
        assertEquals("External ID not correct", SOME_EXTERNAL_ID, ((MappingReport) report).getExternalId());
        assertEquals(1.0d, ((MappingReport) report).getMappingDataReport().getMappedPercentage(), 0d);
        assertEquals(1.0d, ((MappingReport) report).getMappingDataReport().getMateMappedToDifferentChrPercentage(),
                0d);
        assertEquals(2.0d, ((MappingReport) report).getMappingDataReport().getProperlyPairedPercentage(), 0d);
        assertEquals(2.0d, ((MappingReport) report).getMappingDataReport().getSingletonPercentage(), 0d);
        assertEquals(0.2d, ((MappingReport) report).getMappingDataReport().getProportionOfDuplicateRead(), 0d);
    }

}
