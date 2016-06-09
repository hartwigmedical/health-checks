package com.hartwig.healthchecks.boggs.healthchecker;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatTestFactory;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.PatientExtractor;
import com.hartwig.healthchecks.boggs.model.data.PatientData;
import com.hartwig.healthchecks.boggs.model.data.SampleData;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

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
	public void verifyMappingHealthChecker() throws IOException {
		PatientData patient = new PatientData(dummyData(), dummyData());

		HealthChecker checker = new MappingHealthChecker("DummyRunDir", dataExtractor);
		new Expectations() {
			{
				dataExtractor.extractFromRunDirectory("DummyRunDir");
				returns(patient);
			}
		};

		BaseReport report = checker.runCheck();

		assertEquals("Report with wrong type", CheckType.MAPPING, report.getCheckType());
		assertEquals("Refer External ID not correct", "DUMMY", ((MappingReport) report).getRefData().getExternalID());
		assertEquals("Refer Mapped Precentage not correct", "38.46%",
				((MappingReport) report).getRefData().getMappedPercentage());
		assertEquals("Refer Singelton Precentage not correct", "57.69%",
				((MappingReport) report).getRefData().getSingletonPercentage());
		assertEquals("Tumor External ID not correct", "DUMMY", ((MappingReport) report).getTumorData().getExternalID());
		assertEquals("Tumor Mapped Precentage not correct", "38.46%",
				((MappingReport) report).getTumorData().getMappedPercentage());
		assertEquals("Tumor Singelton Precentage not correct", "57.69%",
				((MappingReport) report).getTumorData().getSingletonPercentage());

	}
}
