package com.hartwig.healthchecks.common.checks;

import java.io.IOException;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.result.BaseResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class HealthCheckRunner {

    private static final Logger LOGGER = LogManager.getLogger(HealthCheckRunner.class);
    private static final String ERROR_MSG = "Got an exception with message: %s";

    private HealthCheckRunner() {
    }

    @NotNull
    public static BaseResult run(@NotNull final RunContext runContext, @NotNull final HealthChecker checker) {
        BaseResult result;
        try {
            result = checker.run(runContext);
        } catch (IOException | HealthChecksException exception) {
            LOGGER.error(String.format(ERROR_MSG, exception.getMessage()));
            result = checker.errorResult(runContext);
        }
        return result;
    }
}
