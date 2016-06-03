package com.hartwig.healthchecks.common.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class CheckTypeTest {

    @Test
    public void getByTypeSuccess() throws Exception {
        Optional<CheckType> checkType = CheckType.getByType("boggs");
        Assert.assertTrue(checkType.get() == CheckType.BOGGS);
    }

    @Test
    public void getByTypeFailures() throws Exception {
        Optional<CheckType> checkType = CheckType.getByType("bugs");
        Assert.assertFalse(checkType.isPresent());
    }
}