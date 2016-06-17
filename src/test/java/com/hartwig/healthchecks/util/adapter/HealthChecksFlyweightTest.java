package com.hartwig.healthchecks.util.adapter;

import com.hartwig.healthchecks.boggs.adapter.BoggsAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.exception.NotFoundException;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Test;

public class HealthChecksFlyweightTest {

    @Test
    public void getAdapterSuccess() {
        final HealthChecksFlyweight healthChecksFlyweight = HealthChecksFlyweight.getInstance();

        Assert.assertNotNull(healthChecksFlyweight);

        try {
            final HealthCheckAdapter boggsAdapter = healthChecksFlyweight.getAdapter("boggs");

            Assert.assertTrue(boggsAdapter instanceof BoggsAdapter);
        } catch (NotFoundException e) {
            Assert.fail();
        }
    }

    @Test(expected = NotFoundException.class)
    public void getAdapterFailure() throws NotFoundException {
        final HealthChecksFlyweight healthChecksFlyweight = HealthChecksFlyweight.getInstance();

        Assert.assertNotNull(healthChecksFlyweight);

        new Expectations() {{
            healthChecksFlyweight.getAdapter("bugs");
            result = new NotFoundException("Expected error.");
        }};
    }
}