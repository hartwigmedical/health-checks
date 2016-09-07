package com.hartwig.healthchecks.common.checks;

import java.io.IOException;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.ErrorResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ErrorHandlingChecker {

    private static final Logger LOGGER = LogManager.getLogger(ErrorHandlingChecker.class);
    private static final String ERROR_MSG = "Got an exception with message: %s";

    @NotNull
    private final CheckType checkType;
    @NotNull
    private final HealthChecker checker;

    public ErrorHandlingChecker(@NotNull final CheckType checkType, @NotNull final HealthChecker checker) {
        this.checkType = checkType;
        this.checker = checker;
    }

    @NotNull
    public BaseResult checkedRun() {
        BaseResult result;
        try {
            result = checker.run();
        } catch (IOException | HealthChecksException exception) {
            LOGGER.error(String.format(ERROR_MSG, exception.getMessage()));
            result = new ErrorResult(checkType, exception.getClass().getName());
        }
        return result;
    }
}
