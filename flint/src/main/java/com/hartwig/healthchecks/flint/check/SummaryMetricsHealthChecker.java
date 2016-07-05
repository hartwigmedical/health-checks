package com.hartwig.healthchecks.flint.check;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.common.util.ErrorReport;

public class SummaryMetricsHealthChecker implements HealthChecker {

    private static final String ERROR_MSG = "Got An Exception with message: %s";

    private static final Logger LOGGER = LogManager.getLogger(SummaryMetricsHealthChecker.class);

    @NotNull
    private final String runDirectory;

    @NotNull
    private final DataExtractor dataExtractor;

    public SummaryMetricsHealthChecker(@NotNull final String runDirectory, @NotNull final DataExtractor dataExtractor) {
        this.runDirectory = runDirectory;
        this.dataExtractor = dataExtractor;
    }

    @Override
    @NotNull
    public BaseReport runCheck() {

        BaseReport report;
        try {
            report = dataExtractor.extractFromRunDirectory(runDirectory);
        } catch (IOException | HealthChecksException exception) {
            LOGGER.error(String.format(ERROR_MSG, exception.getMessage()));
            report = new ErrorReport(CheckType.SUMMARY_METRICS, exception.getClass().getName(), exception.getMessage());
        }
        return report;
    }
}
