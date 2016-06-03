package com.hartwig.healthchecks.boggs.healthchecker;

import com.google.common.collect.Lists;
import com.hartwig.healthchecks.boggs.PatientData;
import com.hartwig.healthchecks.boggs.SampleData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatTestFactory;
import com.hartwig.healthchecks.boggs.healthcheck.HealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.MappingHealthChecker;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MappingHealthCheckerTest {

    @NotNull
    private static SampleData dummyData() {
        FlagStatData testData = FlagStatTestFactory.createTestData();
        return new SampleData("DUMMY", Lists.newArrayList(testData),
                Lists.newArrayList(testData), testData, testData);

    }

    @Test
    public void verifyMappingHealthChecker() {
        HealthChecker checker = new MappingHealthChecker();

        PatientData patient = new PatientData(dummyData(), dummyData());

        assertTrue(checker.isHealthy(patient));
    }
}
