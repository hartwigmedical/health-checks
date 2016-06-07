package com.hartwig.healthchecks.common.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class CheckCategoryTest {

    @Test
    public void getByTypeSuccess() throws Exception {
        Optional<CheckCategory> checkType = CheckCategory.getByCategory("boggs");
        Assert.assertTrue(checkType.get() == CheckCategory.BOGGS);
    }

    @Test
    public void getByTypeFailures() throws Exception {
        Optional<CheckCategory> checkType = CheckCategory.getByCategory("bugs");
        Assert.assertFalse(checkType.isPresent());
    }
}