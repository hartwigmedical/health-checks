package com.hartwig.healthchecks.util.adapter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.hartwig.healthchecks.boggs.adapter.BoggsAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.exception.NotFoundException;

public class HealthChecksFlyweightTest {

    private static final String DUMMY_TYPE = "bla";

    @Test
    public void getAdapterSuccess() {
        final HealthChecksFlyweight healthChecksFlyweight = HealthChecksFlyweight.getInstance();

        Assert.assertNotNull(healthChecksFlyweight);

        try {
            final HealthCheckAdapter boggsAdapter = healthChecksFlyweight.getAdapter("boggs");

            assertTrue(boggsAdapter instanceof BoggsAdapter);
        } catch (NotFoundException e) {
            Assert.fail();
        }
    }

    @Test(expected = NotFoundException.class)
    public void getAdapterFailure() throws NotFoundException {
        final HealthChecksFlyweight healthChecksFlyweight = HealthChecksFlyweight.getInstance();
        assertNotNull(healthChecksFlyweight);
        healthChecksFlyweight.getAdapter(DUMMY_TYPE);
    }
}