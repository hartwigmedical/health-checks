package com.hartwig.healthchecks.boggs.healthchecker;

import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestastHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestatsExtractor;
import com.hartwig.healthchecks.boggs.model.PrestatsData;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.util.CheckType;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Test;

import java.io.IOException;

public class PrestastHealthCheckerTest {

	private static final String DUMMY_RUN_DIR = "DummyRunDir";
	@Mocked
	PrestatsExtractor dataExtractor;

	@Test
	public void verifyPrestatsHealthChecker() throws IOException {
		PrestatsData testData = new PrestatsData(CheckType.PRESTATS);
		testData.addData("DummyFile","DummyData");

		HealthChecker checker = new PrestastHealthChecker(DUMMY_RUN_DIR, dataExtractor);

		new Expectations() {
			{
				dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
				returns(testData);
			}
		};
		checker.isHealthy();
		//assertFalse(checker.isHealthy());
	}

	@Test(expected = IOException.class)
	public void verifyPrestatsHealthCheckerIOException() throws IOException {
		HealthChecker checker = new PrestastHealthChecker(DUMMY_RUN_DIR, dataExtractor);
		new Expectations() {
			{
				dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
				result = new IOException();
			}
		};
		checker.isHealthy();
	}
}
