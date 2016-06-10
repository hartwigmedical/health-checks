package com.hartwig.healthchecks.boggs.healthcheck.prestast;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestatsHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestatsExtractor;
import com.hartwig.healthchecks.boggs.model.report.PrestatsDataReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import mockit.Expectations;
import mockit.Mocked;

public class PrestastHealthCheckerTest {

	private static final String DUMMY_RUN_DIR = "DummyRunDir";
	@Mocked
	PrestatsExtractor dataExtractor;

	@Test
	public void verifyPrestatsHealthChecker() throws IOException, EmptyFileException {
		PrestatsReport testData = new PrestatsReport(CheckType.PRESTATS);
		PrestatsDataReport prestatsTestDataReport = new PrestatsDataReport("DummyCheckName", "FAIL", "DummyFile");
		testData.addData(prestatsTestDataReport);

		HealthChecker checker = new PrestatsHealthChecker(DUMMY_RUN_DIR, dataExtractor);

		new Expectations() {
			{
				dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
				returns(testData);
			}
		};
		
		BaseReport report = checker.runCheck();
		assertEquals("Report with wrong type", CheckType.PRESTATS, report.getCheckType());
		assertEquals("Report got wrong size of error", 1, ((PrestatsReport) report).getSummary().size());
	}

	@Test(expected = IOException.class)
	public void verifyPrestatsHealthCheckerIOException() throws IOException, EmptyFileException {
		HealthChecker checker = new PrestatsHealthChecker(DUMMY_RUN_DIR, dataExtractor);
		new Expectations() {
			{
				dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
				result = new IOException();
			}
		};
		checker.runCheck();
	}
}
