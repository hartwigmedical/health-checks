package com.hartwig.healthchecks.common.checks;

import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.result.BaseResult;

import org.jetbrains.annotations.NotNull;

public interface HealthChecker {

    @NotNull
    CheckType checkType();

    @NotNull
    BaseResult run(@NotNull final RunContext runContext);
}
