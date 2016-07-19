package com.hartwig.healthchecks.boo.extractor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.hartwig.healthchecks.boo.extractor.PrestatsCheck;
import com.hartwig.healthchecks.boo.extractor.PrestatsExtractor;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.ErrorReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import mockit.Expectations;
import mockit.Mocked;

public class PrestatsHealthCheckerTest {

    private static final String WRONG_ERROR_MESSAGE = "Wrong Error Message";

    private static final String WRONG_ERROR = "Wrong Error";

    private static final String DUMMY_ERROR = "DUMMY_ERROR";

    private static final String WRONG_CHECK_NAME = "Wrong Check Name";

    private static final String WRONG_CHECK_STATUS = "Wrong Check status";

    private static final String WRONG_PATIENT_ID_MSG = "Wrong Patient ID";

    private static final String WRONG_NUMBER_OF_CHECKS_MSG = "Wrong Number of checks";

    private static final String WRONG_TYPE_MSG = "Report with wrong type";

    private static final String DUMMY_ID = "DUMMY_ID";

    private static final String FAIL = "FAIL";

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    @Mocked
    private PrestatsExtractor dataExtractor;

    @Test
    public void verifyPrestatsHealthChecker() throws IOException, HealthChecksException {
        final BaseDataReport prestatsTestDataReport = new BaseDataReport(DUMMY_ID, PrestatsCheck.DUMMY.getDescription(),
                        FAIL);

        final SampleReport testData = new SampleReport(CheckType.PRESTATS, Arrays.asList(prestatsTestDataReport),
                        Arrays.asList(prestatsTestDataReport));

        final HealthChecker checker = new HealthCheckerImpl(CheckType.PRESTATS, DUMMY_RUN_DIR, dataExtractor);

        new Expectations() {

            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                returns(testData);
            }
        };

        final BaseReport report = checker.runCheck();
        assertEquals(WRONG_TYPE_MSG, CheckType.PRESTATS, report.getCheckType());
        final List<BaseDataReport> summaryData = ((SampleReport) report).getReferenceSample();
        assertEquals(WRONG_NUMBER_OF_CHECKS_MSG, 1, summaryData.size());
        assertEquals(WRONG_CHECK_STATUS, FAIL, summaryData.get(0).getValue());
        assertEquals(WRONG_CHECK_NAME, PrestatsCheck.DUMMY.getDescription(), summaryData.get(0).getCheckName());
        assertEquals(WRONG_PATIENT_ID_MSG, DUMMY_ID, summaryData.get(0).getPatientId());
    }

    @Test
    public void verifyPrestatsHealthCheckerIOException() throws IOException, HealthChecksException {
        new Expectations() {

            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                result = new IOException(DUMMY_ERROR);
            }
        };

        final HealthChecker checker = new HealthCheckerImpl(CheckType.PRESTATS, DUMMY_RUN_DIR, dataExtractor);
        final BaseReport report = checker.runCheck();
        assertEquals(WRONG_TYPE_MSG, CheckType.PRESTATS, report.getCheckType());
        final String error = ((ErrorReport) report).getError();
        final String errorMessage = ((ErrorReport) report).getMessage();

        assertEquals(WRONG_ERROR, IOException.class.getName(), error);
        assertEquals(WRONG_ERROR_MESSAGE, DUMMY_ERROR, errorMessage);
    }

    @Test
    public void verifyPrestatsHealthCheckerEmptyFileException() throws IOException, HealthChecksException {
        new Expectations() {

            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                result = new EmptyFileException(DUMMY_ERROR);
            }
        };

        final HealthChecker checker = new HealthCheckerImpl(CheckType.PRESTATS, DUMMY_RUN_DIR, dataExtractor);
        final BaseReport report = checker.runCheck();
        assertEquals(WRONG_TYPE_MSG, CheckType.PRESTATS, report.getCheckType());
        final String error = ((ErrorReport) report).getError();
        final String errorMessage = ((ErrorReport) report).getMessage();

        assertEquals(WRONG_ERROR, EmptyFileException.class.getName(), error);
        assertEquals(WRONG_ERROR_MESSAGE, DUMMY_ERROR, errorMessage);
    }
}
