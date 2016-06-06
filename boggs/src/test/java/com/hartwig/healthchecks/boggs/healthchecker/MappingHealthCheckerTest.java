package com.hartwig.healthchecks.boggs.healthchecker;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hartwig.healthchecks.boggs.model.PatientData;
import com.hartwig.healthchecks.boggs.model.SampleData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatTestFactory;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.PatientExtractor;
import com.hartwig.healthchecks.common.checks.HealthChecker;

import mockit.Expectations;
import mockit.Mocked;

public class MappingHealthCheckerTest {

	@Mocked
	PatientExtractor dataExtractor;

	@NotNull
	private static SampleData dummyData() {
		FlagStatData testData = FlagStatTestFactory.createTestData();
		return new SampleData("DUMMY", Lists.newArrayList(testData), Lists.newArrayList(testData), testData, testData);

	}

	@Test
	public void verifyMappingHealthChecker() {
		PatientData patient = new PatientData(dummyData(), dummyData());

		HealthChecker checker = new MappingHealthChecker("DummyRunDir", dataExtractor);

		  try {
			new Expectations() {
			        {
						dataExtractor.extractFromRunDirectory("DummyRunDir");
						returns(patient);
			        }
			    };
		} catch (IOException e1) {
			fail();
		}

		try {
			assertTrue(checker.isHealthy());
		} catch (IOException e) {
			fail();
		}
	}
}
