package com.hartwig.healthchecks.boggs.healthcheck.prestasts;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

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
  public void verifyPrestatsHealthChecker() throws IOException, EmptyFileException {
      final PrestatsReport testData = new PrestatsReport(CheckType.PRESTATS);
      final PrestatsDataReport prestatsTestDataReport = new PrestatsDataReport(DUMMY_ID, FAIL, PrestatsCheck.DUMMY);
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
      List<PrestatsDataReport> summaryData = ((PrestatsReport) report).getSummary();
      assertEquals(WRONG_NUMBER_OF_CHECKS_MSG, 1, summaryData.size());
      assertEquals(WRONG_CHECK_NAME, FAIL, summaryData.get(0).getStatus());
      assertEquals(WRONG_CHECK_STATUS, PrestatsCheck.DUMMY, summaryData.get(0).getCheckName());
      assertEquals(WRONG_PATIENT_ID_MSG, DUMMY_ID, summaryData.get(0).getPatientId());
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