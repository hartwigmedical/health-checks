package com.hartwig.healthchecks.common.report;

import java.util.Optional;

import com.hartwig.healthchecks.common.checks.CheckCategory;

import org.junit.Assert;
import org.junit.Test;

public class CheckCategoryTest {

    @Test
    public void getByTypeSuccess() {
        final Optional<CheckCategory> checkType = CheckCategory.getByCategory("boggs");
        assert checkType.isPresent();
        Assert.assertTrue(checkType.get() == CheckCategory.BOGGS);
    }

    @Test
    public void getByTypeFailures() {
        final Optional<CheckCategory> checkType = CheckCategory.getByCategory("bugs");
        Assert.assertFalse(checkType.isPresent());
    }
}