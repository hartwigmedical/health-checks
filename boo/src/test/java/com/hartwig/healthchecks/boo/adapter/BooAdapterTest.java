package com.hartwig.healthchecks.boo.adapter;

import java.util.Arrays;

import org.junit.Test;

import com.hartwig.healthchecks.boo.extractor.PrestatsCheck;
import com.hartwig.healthchecks.boo.extractor.PrestatsExtractor;
import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.report.SampleReport;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

public class BooAdapterTest {

    private static final String DUMMY_ID = "DUMMY_ID";

    private static final String DUMMY_STATUS = "DummyStatus";

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    private static final String DUMMY_REPORT = "DummyReport";

    @Test
    public void verifyAdapterRunning(@Mocked final HealthCheckerImpl prestats, @Mocked final Report report,
                    @Mocked final HealthCheckReportFactory factory, @Mocked final AbstractHealthCheckAdapter mock) {

        new NonStrictExpectations() {

            {
                AbstractHealthCheckAdapter.attachReport(DUMMY_REPORT);
                result = factory;
                times = 1;

                factory.create();
                result = report;
                times = 1;

                new HealthCheckerImpl(CheckType.PRESTATS, anyString, (PrestatsExtractor) any);
                result = prestats;
                times = 1;

                prestats.runCheck();
                returns(getDummyPrestatsReport());
                times = 1;
            }
        };
        final AbstractHealthCheckAdapter adapter = new BooAdapter();
        adapter.runCheck(DUMMY_RUN_DIR, DUMMY_REPORT);

        new Verifications() {

            {
                report.addReportData((BaseReport) any);
                times = 1;
            }
        };
    }

    private SampleReport getDummyPrestatsReport() {
        final BaseDataReport prestatsDataReport = new BaseDataReport(DUMMY_ID, PrestatsCheck.DUMMY.toString(),
                        DUMMY_STATUS);
        return new SampleReport(CheckType.PRESTATS, Arrays.asList(prestatsDataReport),
                        Arrays.asList(prestatsDataReport));
    }
}
