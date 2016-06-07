package com.hartwig.healthchecks.boggs.healthchecker;

import com.google.common.collect.Lists;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatTestFactory;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.PatientExtractor;
import com.hartwig.healthchecks.boggs.model.PatientData;
import com.hartwig.healthchecks.boggs.model.SampleData;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.util.CheckType;
import mockit.Expectations;
import mockit.Mocked;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MappingHealthCheckerTest {

	@Mocked
	PatientExtractor dataExtractor;

	@NotNull
	private static SampleData dummyData() {
		FlagStatData testData = FlagStatTestFactory.createTestData();
		return new SampleData(CheckType.MAPPING, "DUMMY", Lists.newArrayList(testData), Lists.newArrayList(testData), testData, testData);

	}

	@Test
	public void verifyMappingHealthChecker() {
		PatientData patient = new PatientData(CheckType.MAPPING, dummyData(), dummyData());

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
