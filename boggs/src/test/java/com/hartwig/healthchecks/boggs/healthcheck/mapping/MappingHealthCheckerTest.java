package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.hartwig.healthchecks.boggs.model.report.BaseDataReport;
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
    @Mocked
    private MappingExtractor dataExtractor;

    @NotNull
    private static MappingReport dummyData() {

        BaseDataReport mappedData = new BaseDataReport("Dummy", MappingCheck.MAPPING_MAPPED.getDescription(), "0.0");
        BaseDataReport duplicateData = new BaseDataReport("Dummy",
                MappingCheck.MAPPING_DUPLIVATES.getDescription(), "0.0");
        BaseDataReport properlyData = new BaseDataReport("Dummy",
                MappingCheck.MAPPING_PROPERLY_PAIRED.getDescription(), "0.0");

        List<BaseDataReport> reports = Arrays.asList(mappedData, duplicateData, properlyData);

        MappingReport mappingReport = new MappingReport(CheckType.MAPPING);
        mappingReport.addAll(reports);

        return mappingReport;
    }

    @Test
    public void verifyMappingHealthChecker() throws IOException, EmptyFileException {

        final HealthChecker checker = new MappingHealthChecker(DUMMY_RUN_DIR, dataExtractor);
        new Expectations() {
            {
                dataExtractor.extractFromRunDirectory(anyString);
                returns(dummyData());
            }
        };

        final BaseReport report = checker.runCheck();

        assertEquals("Report with wrong type", CheckType.MAPPING, report.getCheckType());
        assertEquals("Dummy", ((MappingReport) report).getMapping().get(0).getPatientId());
        assertEquals(MappingCheck.MAPPING_MAPPED.getDescription(),
                ((MappingReport) report).getMapping().get(0).getCheckName());
    }
}
