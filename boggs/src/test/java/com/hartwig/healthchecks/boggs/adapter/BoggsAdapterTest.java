package com.hartwig.healthchecks.boggs.adapter;

import java.util.Arrays;

import org.junit.Test;

import com.hartwig.healthchecks.boggs.extractor.MappingExtractor;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

public class BoggsAdapterTest {

    private static final String DUMMY_VALUE = "1.0d";

    private static final String DUMMY_CHECK = "DUMMY_CHECK";

    private static final String DUMMY_ID = "DUMMY_ID";

    private static final String DUMMY_STATUS = "DummyStatus";

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    @Test
    public void verifyAdapterRunning(@Mocked final HealthCheckerImpl mapping, @Mocked final JsonReport report) {

        new NonStrictExpectations() {

            {
                JsonReport.getInstance();
                result = report;
                times = 1;

                new HealthCheckerImpl(CheckType.MAPPING, anyString, (MappingExtractor) any);
                result = mapping;
                times = 1;
                mapping.runCheck();
                returns(getDummyMappingReport());
                times = 1;
            }
        };
        final HealthCheckAdapter adapter = new BoggsAdapter();
        adapter.runCheck(DUMMY_RUN_DIR);

        new Verifications() {

            {
                report.addReportData((BaseReport) any);
                times = 1;
            }
        };
    }

    private SampleReport getDummyMappingReport() {
        final BaseDataReport mappingDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, DUMMY_VALUE);
        return new SampleReport(CheckType.MAPPING, Arrays.asList(mappingDataReport), Arrays.asList(mappingDataReport));
    }

}
