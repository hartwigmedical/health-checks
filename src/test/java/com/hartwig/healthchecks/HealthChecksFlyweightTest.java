package com.hartwig.healthchecks;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.hartwig.healthchecks.boggs.check.MappingChecker;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.NotFoundException;

import org.junit.Test;

public class HealthChecksFlyweightTest {

    @Test
    public void existingChecker() throws NotFoundException {
        final HealthChecksFlyweight healthChecksFlyweight = HealthChecksFlyweight.getInstance();
        assertNotNull(healthChecksFlyweight);
        final HealthChecker mappingChecker = healthChecksFlyweight.getChecker(CheckType.MAPPING.toString());
        assertTrue(mappingChecker instanceof MappingChecker);
    }

    @Test(expected = NotFoundException.class)
    public void nonExistingChecker() throws NotFoundException {
        final HealthChecksFlyweight healthChecksFlyweight = HealthChecksFlyweight.getInstance();
        assertNotNull(healthChecksFlyweight);
        healthChecksFlyweight.getChecker("bla");
    }
}
