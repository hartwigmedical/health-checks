package com.hartwig.healthchecks.boggs.adapter;

import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingExtractor;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsExtractor;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsHealthChecker;
import com.hartwig.healthchecks.boggs.model.report.MappingDataReport;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsDataReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import mockit.Mock;
import mockit.MockUp;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BoggsAdapterTest {

    private static final String DUMMY_ID = "DUMMY_ID";
    private static final String DUMMY_CHECK_NAME = "DummyCheckName";
    private static final String DUMMY_STATUS = "DummyStatus";
    private static final String _123 = "123";
    private static final String SOME_ID = "SomeId";
    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    @Test
    public void verifyAdapterRunning() throws IOException, EmptyFileException {
        final MappingReport dummyMappingReport = getDummyMappingReport();
        final PrestatsReport dummyPrestatsReport = getDummyPrestatsReport();

        new MockUp<MappingHealthChecker>() {
            @Mock
            void $init(String runDir, MappingExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 1)
            public BaseReport runCheck() {
                return dummyMappingReport;
            }
        };
        new MockUp<PrestatsHealthChecker>() {
            @Mock
            void $init(String runDir, PrestatsExtractor extractor) {
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
        final MappingDataReport mappingDataReport = new MappingDataReport(1.0d, 2.0d, 2.0d, 1.0d, 0.2d, true);
        return new MappingReport(CheckType.MAPPING, SOME_ID, _123, mappingDataReport);
    }

    private PrestatsReport getDummyPrestatsReport() {
        final PrestatsDataReport prestatsDataReport = new PrestatsDataReport(DUMMY_STATUS, DUMMY_CHECK_NAME);
        final PrestatsReport prestatsReport = new PrestatsReport(CheckType.PRESTATS ,DUMMY_ID);
        prestatsReport.addData(prestatsDataReport);
        return prestatsReport;
    }

    @Test
    public void verifyMappingHealthCheckerIoException() throws IOException, EmptyFileException {

        new MockUp<MappingHealthChecker>() {
            @Mock
            void $init(String runDir, MappingExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 1)
            public BaseReport runCheck() throws IOException, EmptyFileException {
                throw new IOException();
            }
        };
        new MockUp<PrestatsHealthChecker>() {
            @Mock
            void $init(String runDir, PrestatsExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 0)
            public BaseReport runCheck() throws IOException, EmptyFileException {
                return null;
            }
        };

        final BoggsAdapter adapter = new BoggsAdapter();
        adapter.runCheck(DUMMY_RUN_DIR);
    }

    @Test
    public void verifyMappingHealthCheckerEmptyFileException() throws IOException, EmptyFileException {

        new MockUp<MappingHealthChecker>() {
            @Mock
            void $init(String runDir, MappingExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 1)
            public BaseReport runCheck() throws IOException, EmptyFileException {
                throw new EmptyFileException("SomeMessage");
            }
        };
        new MockUp<PrestatsHealthChecker>() {
            @Mock
            void $init(String runDir, PrestatsExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 0)
            public BaseReport runCheck() throws IOException, EmptyFileException {
                return null;
            }
        };

        final BoggsAdapter adapter = new BoggsAdapter();
        adapter.runCheck(DUMMY_RUN_DIR);
    }

    @Test
    public void verifyPrestatsHealthCheckerEmptyFileException() throws IOException, EmptyFileException {

        new MockUp<MappingHealthChecker>() {
            @Mock
            void $init(String runDir, MappingExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 1)
            public BaseReport runCheck() throws IOException, EmptyFileException {
                return getDummyMappingReport();
            }
        };
        new MockUp<PrestatsHealthChecker>() {
            @Mock
            void $init(String runDir, PrestatsExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 1)
            public BaseReport runCheck() throws IOException, EmptyFileException {
                throw new EmptyFileException("SomeMessage");
            }
        };

        final BoggsAdapter adapter = new BoggsAdapter();
        adapter.runCheck(DUMMY_RUN_DIR);
    }
}
