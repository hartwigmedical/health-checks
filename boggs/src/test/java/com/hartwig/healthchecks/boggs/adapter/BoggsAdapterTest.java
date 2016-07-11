package com.hartwig.healthchecks.boggs.adapter;

import java.util.Arrays;

import org.junit.Test;

import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingExtractor;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsCheck;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsExtractor;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.SampleReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

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
    public void verifyAdapterRunning(@Mocked final HealthCheckerImpl mapping, @Mocked final HealthCheckerImpl prestats,
                    @Mocked final JsonReport report) {

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

                new HealthCheckerImpl(CheckType.PRESTATS, anyString, (PrestatsExtractor) any);
                result = prestats;
                times = 1;

                prestats.runCheck();
                returns(getDummyPrestatsReport());
                times = 1;
            }
        };
        final HealthCheckAdapter adapter = new BoggsAdapter();
        adapter.runCheck(DUMMY_RUN_DIR);

        new Verifications() {

            {
                report.addReportData((BaseReport) any);
                times = 2;
            }
        };
    }

    private SampleReport getDummyMappingReport() {
        final BaseDataReport mappingDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, DUMMY_VALUE);
        return new SampleReport(CheckType.MAPPING, Arrays.asList(mappingDataReport), Arrays.asList(mappingDataReport));
    }

    private SampleReport getDummyPrestatsReport() {
        final BaseDataReport prestatsDataReport = new BaseDataReport(DUMMY_ID, PrestatsCheck.DUMMY.toString(),
                        DUMMY_STATUS);
        return new SampleReport(CheckType.PRESTATS, Arrays.asList(prestatsDataReport),
                        Arrays.asList(prestatsDataReport));
    }
}
