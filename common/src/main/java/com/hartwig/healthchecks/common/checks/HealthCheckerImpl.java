package com.hartwig.healthchecks.common.checks;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.ErrorReport;

public class HealthCheckerImpl implements HealthChecker {

    private static final String ERROR_MSG = "Got an exception with message: %s";

    private static final Logger LOGGER = LogManager.getLogger(HealthCheckerImpl.class);

    @NotNull
    private final String runDirectory;
    @NotNull
    private final DataExtractor dataExtractor;

    private final CheckType checkType;

    public HealthCheckerImpl(final CheckType checkType, @NotNull final String runDirectory,
            @NotNull final DataExtractor dataExtractor) {
        this.runDirectory = runDirectory;
        this.dataExtractor = dataExtractor;
        this.checkType = checkType;
    }

    @Override
    @NotNull
    public BaseReport runCheck() {

        BaseReport report;
        try {
            report = dataExtractor.extractFromRunDirectory(runDirectory);
        } catch (IOException | HealthChecksException exception) {
            LOGGER.error(String.format(ERROR_MSG, exception.getMessage()));
            report = new ErrorReport(checkType, exception.getClass().getName(), exception.getMessage());
        }
        return report;
    }
}
