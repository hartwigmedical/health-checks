package com.hartwig.healthchecks.smitty.adapter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.smitty.check.KinshipHealthChecker;
import com.hartwig.healthchecks.smitty.report.KinshipReport;

import mockit.Mock;
import mockit.MockUp;

public class SmittyAdapterTest {

    private static final String DUMMY_VALUE = "1.0d";

    private static final String DUMMY_CHECK = "DUMMY_CHECK";

    private static final String DUMMY_ID = "DUMMY_ID";

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    @Test
    public void verifyAdapterRunning() {

        new MockUp<KinshipHealthChecker>() {

            @Mock
            void $init(final String runDir, final DataExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 1)
            public BaseReport runCheck() {
                return getKinshipDummyReport();
            }
        };
        final SmittyAdapter adapter = new SmittyAdapter();
        adapter.runCheck(DUMMY_RUN_DIR);
    }

    private BaseReport getKinshipDummyReport() {
        final BaseDataReport baseDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, DUMMY_VALUE);
        return new KinshipReport(CheckType.KINSHIP, baseDataReport);
    }
}
