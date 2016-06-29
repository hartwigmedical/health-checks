package com.hartwig.healthchecks.boggs.healthcheck.prestasts;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.extractor.DataExtractor;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.common.util.ErrorReport;

public class PrestatsHealthChecker implements HealthChecker {

    protected static final String ERROR_MSG = "Got An Exception with message: %s";

    private static final Logger LOGGER = LogManager.getLogger(PrestatsHealthChecker.class);

    @NotNull
    private final String runDirectory;

    @NotNull
    private final DataExtractor dataExtractor;

    public PrestatsHealthChecker(@NotNull final String runDirectory, @NotNull final DataExtractor dataExtractor) {
        this.runDirectory = runDirectory;
        this.dataExtractor = dataExtractor;
    }

    @Override
    @NotNull
    public BaseReport runCheck() {

        BaseReport prestatsReport;
        try {
            prestatsReport = dataExtractor.extractFromRunDirectory(runDirectory);
        } catch (IOException | HealthChecksException exception) {
            LOGGER.error(String.format(ERROR_MSG, exception.getMessage()));
            prestatsReport = new ErrorReport(CheckType.PRESTATS, exception.getClass().getName(),
                            exception.getMessage());
        }
        return prestatsReport;
    }
}
