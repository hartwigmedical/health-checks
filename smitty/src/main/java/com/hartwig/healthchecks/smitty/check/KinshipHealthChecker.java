package com.hartwig.healthchecks.smitty.check;

import java.io.IOException;

import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.extractor.DataExtractor;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.common.util.ErrorReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class KinshipHealthChecker implements HealthChecker {

    private  static final String ERROR_MSG = "Got An Exception with message: %s";

    private static final Logger LOGGER = LogManager.getLogger(KinshipHealthChecker.class);

    @NotNull
    private final String runDirectory;

    @NotNull
    private final DataExtractor dataExtractor;

    public KinshipHealthChecker(@NotNull final String runDirectory, @NotNull final DataExtractor dataExtractor) {
        this.runDirectory = runDirectory;
        this.dataExtractor = dataExtractor;
    }

    @Override
    @NotNull
    public BaseReport runCheck() {

        BaseReport kinshipReport;
        try {
            kinshipReport = dataExtractor.extractFromRunDirectory(runDirectory);
        } catch (IOException | HealthChecksException exception) {
            LOGGER.error(String.format(ERROR_MSG, exception.getMessage()));
            kinshipReport = new ErrorReport(CheckType.KINSHIP, exception.getClass().getName(), exception.getMessage());
        }
        return kinshipReport;
    }

}
