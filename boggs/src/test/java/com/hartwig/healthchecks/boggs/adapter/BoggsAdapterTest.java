package com.hartwig.healthchecks.boggs.adapter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingExtractor;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsCheck;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsExtractor;
import com.hartwig.healthchecks.boggs.healthcheck.prestasts.PrestatsHealthChecker;
import com.hartwig.healthchecks.boggs.model.report.BaseDataReport;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
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
    public void verifyAdapterRunning() throws IOException, EmptyFileException {
        final MappingReport dummyMappingReport = getDummyMappingReport();
        final PrestatsReport dummyPrestatsReport = getDummyPrestatsReport();

        new MockUp<MappingHealthChecker>() {

            @Mock
            void $init(final String runDir, final MappingExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 1)
            public BaseReport runCheck() {
                return dummyMappingReport;
            }
        };
        new MockUp<PrestatsHealthChecker>() {

            @Mock
            void $init(final String runDir, final PrestatsExtractor extractor) {
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
        final MappingReport mappingReport = new MappingReport(CheckType.MAPPING);
        mappingReport.addData(new BaseDataReport(DUMMY_ID, DUMMY_CHECK, DUMMY_VALUE));
        return mappingReport;
    }

    private PrestatsReport getDummyPrestatsReport() {
        final BaseDataReport prestatsDataReport = new BaseDataReport(DUMMY_ID, PrestatsCheck.DUMMY.toString(),
                        DUMMY_STATUS);
        final PrestatsReport prestatsReport = new PrestatsReport(CheckType.PRESTATS);
        prestatsReport.addData(prestatsDataReport);
        return prestatsReport;
    }

    @Test
    public void verifyMappingHealthCheckerIoException() throws IOException, EmptyFileException {

        new MockUp<MappingHealthChecker>() {

            @Mock
            void $init(final String runDir, final MappingExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 1)
            public BaseReport runCheck() throws IOException, EmptyFileException {
                throw new IOException();
            }
        };
        new MockUp<PrestatsHealthChecker>() {

            @Mock
            void $init(final String runDir, final PrestatsExtractor extractor) {
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
            void $init(final String runDir, final MappingExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 1)
            public BaseReport runCheck() throws IOException, EmptyFileException {
                throw new EmptyFileException("SomeMessage");
            }
        };
        new MockUp<PrestatsHealthChecker>() {

            @Mock
            void $init(final String runDir, final PrestatsExtractor extractor) {
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
            void $init(final String runDir, final MappingExtractor extractor) {
                assertEquals(runDir, DUMMY_RUN_DIR);
            }

            @Mock(invocations = 1)
            public BaseReport runCheck() throws IOException, EmptyFileException {
                return getDummyMappingReport();
            }
        };
        new MockUp<PrestatsHealthChecker>() {

            @Mock
            void $init(final String runDir, final PrestatsExtractor extractor) {
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
