package com.hartwig.healthchecks.common.util;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

public class CheckCategoryTest {

    @Test public void getByTypeSuccess() throws Exception {
        final Optional<CheckCategory> checkType = CheckCategory.getByCategory("boggs");
        Assert.assertTrue(checkType.get() == CheckCategory.BOGGS);
    }

    @Test public void getByTypeFailures() throws Exception {
        final Optional<CheckCategory> checkType = CheckCategory.getByCategory("bugs");
        Assert.assertFalse(checkType.isPresent());
    }
}