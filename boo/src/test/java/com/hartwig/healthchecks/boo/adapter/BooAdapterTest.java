package com.hartwig.healthchecks.boo.adapter;

import java.util.Arrays;

import org.junit.Test;

import com.hartwig.healthchecks.boo.adapter.BooAdapter;
import com.hartwig.healthchecks.boo.extractor.PrestatsCheck;
import com.hartwig.healthchecks.boo.extractor.PrestatsExtractor;
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

public class BooAdapterTest {

    private static final String DUMMY_VALUE = "1.0d";

    private static final String DUMMY_CHECK = "DUMMY_CHECK";

    private static final String DUMMY_ID = "DUMMY_ID";

    private static final String DUMMY_STATUS = "DummyStatus";

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    @Test
    public void verifyAdapterRunning(@Mocked final HealthCheckerImpl prestats, @Mocked final JsonReport report) {

        new NonStrictExpectations() {

            {
                JsonReport.getInstance();
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
        final HealthCheckAdapter adapter = new BooAdapter();
        adapter.runCheck(DUMMY_RUN_DIR);

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