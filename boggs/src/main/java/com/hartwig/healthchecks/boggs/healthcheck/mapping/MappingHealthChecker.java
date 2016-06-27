package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.common.util.ErrorReport;

public class MappingHealthChecker implements HealthChecker {

    protected static final String ERROR_MSG = "Got An Exception with message: %s";

    private static final Logger LOGGER = LogManager.getLogger(MappingHealthChecker.class);

    private final String runDirectory;

    private final MappingExtractor dataExtractor;

    public MappingHealthChecker(@NotNull final String runDirectory, @NotNull final MappingExtractor dataExtractor) {
        this.runDirectory = runDirectory;
        this.dataExtractor = dataExtractor;
    }

    @Override
    @NotNull
    public BaseReport runCheck() {
        BaseReport mappingReport;
        try {
            mappingReport = dataExtractor.extractFromRunDirectory(runDirectory);
        } catch (IOException | EmptyFileException exception) {
            LOGGER.error(String.format(ERROR_MSG, exception.getMessage()));
            mappingReport = new ErrorReport(CheckType.MAPPING, exception.getClass().getName(), exception.getMessage());
        }
        return mappingReport;
    }

}
