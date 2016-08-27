package com.hartwig.healthchecks.common.checks;

import com.hartwig.healthchecks.common.data.BaseResult;

import org.jetbrains.annotations.NotNull;

public interface HealthChecker {

    @NotNull
    BaseResult runCheck();
}
