package com.hartwig.healthchecks.boggs.adapter;

import java.util.Arrays;

import com.hartwig.healthchecks.boggs.extractor.MappingExtractor;
import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.junit.Test;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

public class BoggsAdapterTest {

    private static final String DUMMY_VALUE = "1.0d";

    private static final String DUMMY_CHECK = "DUMMY_CHECK";

    private static final String DUMMY_ID = "DUMMY_ID";

    private static final String DUMMY_STATUS = "DummyStatus";

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    private static final String DUMMY_REPORT = "DummyReport";

    @Test
    public void verifyAdapterRunning(@Mocked final HealthCheckerImpl mapping, @Mocked final Report report, @Mocked
            HealthCheckReportFactory factory, @Mocked AbstractHealthCheckAdapter mock) {

        new NonStrictExpectations() {

            {
                AbstractHealthCheckAdapter.attachReport(DUMMY_REPORT);
                result = factory;
                times = 1;

                factory.create();
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
        final AbstractHealthCheckAdapter adapter = new BoggsAdapter();
        adapter.runCheck(DUMMY_RUN_DIR, DUMMY_REPORT);

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
