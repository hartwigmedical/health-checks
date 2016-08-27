package com.hartwig.healthchecks.common.checks;

import java.io.IOException;

import com.hartwig.healthchecks.common.data.BaseResult;
import com.hartwig.healthchecks.common.data.ErrorResult;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class HealthCheckerImpl implements HealthChecker {

    private static final Logger LOGGER = LogManager.getLogger(HealthCheckerImpl.class);
    private static final String ERROR_MSG = "Got an exception with message: %s";

    @NotNull
    private final CheckType checkType;
    @NotNull
    private final DataExtractor dataExtractor;

    public HealthCheckerImpl(@NotNull final CheckType checkType, @NotNull final DataExtractor dataExtractor) {
        this.checkType = checkType;
        this.dataExtractor = dataExtractor;
    }

    @Override
    @NotNull
    public BaseResult runCheck() {
        BaseResult report;
        try {
            report = dataExtractor.extract();
        } catch (IOException | HealthChecksException exception) {
            LOGGER.error(String.format(ERROR_MSG, exception.getMessage()));
            report = new ErrorResult(checkType, exception.getClass().getName(), exception.getMessage());
        }
        return report;
    }
}
