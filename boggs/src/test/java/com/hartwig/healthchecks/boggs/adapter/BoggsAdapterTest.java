package com.hartwig.healthchecks.boggs.adapter;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsCheck;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsHealthChecker;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.extractor.DataExtractor;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import mockit.Mock;
import mockit.MockUp;

public class BoggsAdapterTest {

    private static final String DUMMY_VALUE = "1.0d";

    private static final String DUMMY_CHECK = "DUMMY_CHECK";

    private static final String DUMMY_ID = "DUMMY_ID";

    private static final String DUMMY_STATUS = "DummyStatus";

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    @Test
    public void verifyAdapterRunning() {
        final MappingReport dummyMappingReport = getDummyMappingReport();
        final PrestatsReport dummyPrestatsReport = getDummyPrestatsReport();

        new MockUp<MappingHealthChecker>() {

            @Mock
            void $init(final String runDir, final DataExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 1)
            public BaseReport runCheck() {
                return dummyMappingReport;
            }
        };
        new MockUp<PrestatsHealthChecker>() {

            @Mock
            void $init(final String runDir, final DataExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 1)
            public BaseReport runCheck() {
                return dummyPrestatsReport;
            }
        };

        final BoggsAdapter adapter = new BoggsAdapter();
        adapter.runCheck(DUMMY_RUN_DIR);
    }

    private MappingReport getDummyMappingReport() {
        final BaseDataReport mappingDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, DUMMY_VALUE);
        final MappingReport mappingReport = new MappingReport(CheckType.MAPPING, Arrays.asList(mappingDataReport),
                        Arrays.asList(mappingDataReport));
        return mappingReport;
    }

    private PrestatsReport getDummyPrestatsReport() {
        final BaseDataReport prestatsDataReport = new BaseDataReport(DUMMY_ID, PrestatsCheck.DUMMY.toString(),
                        DUMMY_STATUS);
        return new PrestatsReport(CheckType.PRESTATS, Arrays.asList(prestatsDataReport),
                        Arrays.asList(prestatsDataReport));
    }
}
