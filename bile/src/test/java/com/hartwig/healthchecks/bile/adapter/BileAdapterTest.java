package com.hartwig.healthchecks.bile.adapter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.PatientReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import mockit.Mock;
import mockit.MockUp;

public class BileAdapterTest {

    private static final String DUMMY_VALUE = "1.0d";

    private static final String DUMMY_CHECK = "DUMMY_CHECK";

    private static final String DUMMY_ID = "DUMMY_ID";

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    @Test
    public void verifyAdapterRunning() {

        new MockUp<HealthCheckerImpl>() {

            @Mock
            void $init(final CheckType checkType, final String runDir, final DataExtractor extractor) {
                assertEquals(checkType, CheckType.REALIGNER);
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 1)
            public BaseReport runCheck() {
                return getBaseDummyReport();
            }
        };
        final HealthCheckAdapter adapter = new BileAdapter();
        adapter.runCheck(DUMMY_RUN_DIR);
    }

    private BaseReport getBaseDummyReport() {
        final BaseDataReport baseDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, DUMMY_VALUE);
        return new PatientReport(CheckType.SLICED, baseDataReport);
    }
}
