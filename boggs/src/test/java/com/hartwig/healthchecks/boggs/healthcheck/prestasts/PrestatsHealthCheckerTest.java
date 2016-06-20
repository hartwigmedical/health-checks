package com.hartwig.healthchecks.boggs.healthcheck.prestasts;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.hartwig.healthchecks.boggs.model.report.PrestatsDataReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import mockit.Expectations;
import mockit.Mocked;

public class PrestatsHealthCheckerTest {

    private static final String WRONG_REPORT_STRING = "Wrong Report String";

    private static final String WRONG_PATIENT_ID_MSG = "Wrong Patient ID";

    private static final String WRONG_NUMBER_OF_CHECKS_MSG = "Wrong Number of checks";

    private static final String WRONG_TYPE_MSG = "Report with wrong type";

    private static final String DUMMY_ID = "DUMMY_ID";

    private static final String DUMMY_CHECK_NAME = "DummyCheckName";

    private static final String FAIL = "FAIL";

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    private static final String EXPECTED_REPORT = "PrestatsReport [externalId=DUMMY_ID, summary=[PrestatsDataReport [checkName=DummyCheckName, status=FAIL]]]";
    @Mocked
    private PrestatsExtractor dataExtractor;

    @Test
    public void verifyPrestatsHealthChecker() throws IOException, EmptyFileException {
        final PrestatsReport testData = new PrestatsReport(CheckType.PRESTATS, DUMMY_ID);
        final PrestatsDataReport prestatsTestDataReport = new PrestatsDataReport(FAIL, DUMMY_CHECK_NAME);
        testData.addData(prestatsTestDataReport);

        final HealthChecker checker = new PrestatsHealthChecker(DUMMY_RUN_DIR, dataExtractor);

        new Expectations() {
            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                returns(testData);
            }
        };

        final BaseReport report = checker.runCheck();
        assertEquals(WRONG_TYPE_MSG, CheckType.PRESTATS, report.getCheckType());
        assertEquals(WRONG_NUMBER_OF_CHECKS_MSG, 1, ((PrestatsReport) report).getSummary().size());
        assertEquals(WRONG_PATIENT_ID_MSG, DUMMY_ID, ((PrestatsReport) report).getExternalId());

        assertEquals(WRONG_REPORT_STRING, EXPECTED_REPORT, ((PrestatsReport) report).toString());

    }

    @Test(expected = IOException.class)
    public void verifyPrestatsHealthCheckerIOException() throws IOException, EmptyFileException {
        final HealthChecker checker = new PrestatsHealthChecker(DUMMY_RUN_DIR, dataExtractor);
        new Expectations() {
            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                result = new IOException();
            }
        };
        checker.runCheck();
    }
}
