package com.hartwig.healthchecks.common.checks;

import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.result.BaseResult;

import org.jetbrains.annotations.NotNull;

public final class HealthCheckRunner {

    private HealthCheckRunner() {
    }

    @NotNull
    public static BaseResult runCheck(@NotNull final RunContext runContext, @NotNull final HealthChecker checker) {
        final ErrorHandlingChecker errorHandlingChecker = new ErrorHandlingChecker(checker);
        return errorHandlingChecker.checkedRun(runContext);
    }
}
