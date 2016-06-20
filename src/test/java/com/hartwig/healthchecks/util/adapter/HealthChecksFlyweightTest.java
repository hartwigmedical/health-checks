package com.hartwig.healthchecks.util.adapter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.hartwig.healthchecks.boggs.adapter.BoggsAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.exception.NotFoundException;

public class HealthChecksFlyweightTest {

    private static final String WRONG_TYPE_MSG = "Wrong Type fo Adapter";
    private static final String BOGGS = "boggs";
    private static final String NOT_NULL_MSG = "healthChecksFlyweight should not be null";
    private static final String DUMMY_TYPE = "bla";

    @Test
    public void getAdapterSuccess() throws NotFoundException {
        final HealthChecksFlyweight healthChecksFlyweight = HealthChecksFlyweight.getInstance();
        Assert.assertNotNull(NOT_NULL_MSG, healthChecksFlyweight);
        final HealthCheckAdapter boggsAdapter = healthChecksFlyweight.getAdapter(BOGGS);
        assertTrue(WRONG_TYPE_MSG, boggsAdapter instanceof BoggsAdapter);
    }

    @Test(expected = NotFoundException.class)
    public void getAdapterFailure() throws NotFoundException {
        final HealthChecksFlyweight healthChecksFlyweight = HealthChecksFlyweight.getInstance();
        assertNotNull(NOT_NULL_MSG, healthChecksFlyweight);
        healthChecksFlyweight.getAdapter(DUMMY_TYPE);
    }
}