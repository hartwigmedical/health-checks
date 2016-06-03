package com.hartwig.healthchecks.boggs.healthchecker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestastHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestatsData;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestatsExtractor;
import com.hartwig.healthchecks.common.checks.HealthChecker;

import mockit.Expectations;
import mockit.Mocked;

public class PrestastHealthCheckerTest {

	@Mocked
	PrestatsExtractor dataExtractor;

	@Test
	public void verifyPrestatsHealthChecker() {
		PrestatsData testData = new PrestatsData();
		testData.setName("DummyName");
		List<String> errors = new ArrayList<>();
		errors.add("DummyErr");
		testData.setPrestatsErrors(errors);
		List<PrestatsData> prestatsDatasErrors = new ArrayList<>();

		prestatsDatasErrors.add(testData);
		HealthChecker checker = new PrestastHealthChecker("DummyRunDir", dataExtractor);

		try {
			new Expectations() {
				{
					dataExtractor.extractFromRunDirectory("DummyRunDir");
					returns(prestatsDatasErrors);
				}
			};
		} catch (IOException e1) {
			fail();
		}

		assertFalse(checker.isHealthy());
	}
}
