package com.hartwig.healthchecks.common.checks;

import java.io.IOException;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.ErrorResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

class ErrorHandlingChecker {

    private static final Logger LOGGER = LogManager.getLogger(ErrorHandlingChecker.class);
    private static final String ERROR_MSG = "Got an exception with message: %s";

    @NotNull
    private final HealthChecker checker;

    ErrorHandlingChecker(@NotNull final HealthChecker checker) {
        this.checker = checker;
    }

    @NotNull
    BaseResult checkedRun(@NotNull final RunContext runContext) {
        BaseResult result;
        try {
            result = checker.run(runContext);
        } catch (IOException | HealthChecksException exception) {
            LOGGER.error(String.format(ERROR_MSG, exception.getMessage()));
            result = new ErrorResult(checker.checkType(), exception.getClass().getName());
        }
        return result;
    }
}
