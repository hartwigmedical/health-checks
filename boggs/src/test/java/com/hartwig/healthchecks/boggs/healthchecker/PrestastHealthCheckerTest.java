package com.hartwig.healthchecks.boggs.healthchecker;

import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestastHealthChecker;
import com.hartwig.healthchecks.boggs.model.PrestatsData;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestatsExtractor;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;

public class PrestastHealthCheckerTest {

	private static final String DUMMY_RUN_DIR = "DummyRunDir";
	@Mocked
	PrestatsExtractor dataExtractor;

	@Test
	public void verifyPrestatsHealthChecker() throws IOException {
		PrestatsData testData = new PrestatsData("DummyErr", "DummyName");
		List<PrestatsData> prestatsDatasErrors = new ArrayList<>();

		prestatsDatasErrors.add(testData);
		HealthChecker checker = new PrestastHealthChecker(DUMMY_RUN_DIR, dataExtractor);

		new Expectations() {
			{
				dataExtractor.extractFromRunDirectory(DUMMY_RUN_DIR);
				returns(prestatsDatasErrors);
			}
		};
		assertFalse(checker.isHealthy());
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
