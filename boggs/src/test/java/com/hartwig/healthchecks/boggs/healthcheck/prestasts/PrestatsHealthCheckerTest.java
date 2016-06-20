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

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    private static final String EXPECTED_REPORT = "PrestatsReport [summary=[PrestatsDataReport [checkName=DummyCheckName, status=FAIL, file=DummyFile]]]";
    @Mocked
    private PrestatsExtractor dataExtractor;

    @Test
    public void verifyPrestatsHealthChecker() throws IOException, EmptyFileException {
        final PrestatsReport testData = new PrestatsReport(CheckType.PRESTATS);
        final PrestatsDataReport prestatsTestDataReport = new PrestatsDataReport("FAIL","DummyCheckName", "DummyFile");
        testData.addData(prestatsTestDataReport);

        final HealthChecker checker = new PrestatsHealthChecker(DUMMY_RUN_DIR, dataExtractor);

        new Expectations() {
            {
                dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
                returns(testData);
            }
        };

        final BaseReport report = checker.runCheck();
        assertEquals("Report with wrong type", CheckType.PRESTATS, report.getCheckType());
        assertEquals("Report got wrong size of error", 1, ((PrestatsReport) report).getSummary().size());
        assertEquals("Report String is not correct", EXPECTED_REPORT, ((PrestatsReport) report).toString());

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
