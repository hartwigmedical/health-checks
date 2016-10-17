package com.hartwig.healthchecks.common.checks;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.result.BaseResult;

public interface HealthChecker {

    @NotNull
    CheckType checkType();

    @NotNull
    BaseResult run(@NotNull RunContext runContext);
}
