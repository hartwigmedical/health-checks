package com.hartwig.healthchecks.boggs.healthcheck.prestasts;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boggs.model.report.PrestatsDataReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.BaseReport;

public class PrestatsHealthChecker implements HealthChecker {

    private static final String FAIL_ERROR = "FAIL";

    private static final String FOUND_FAILS_MSG = "NOT OK: %s has status FAIL for Patient %s";

    private static final Logger LOGGER = LogManager.getLogger(PrestatsHealthChecker.class);

    @NotNull
    private final String runDirectory;

    @NotNull
    private final PrestatsExtractor dataExtractor;

    public PrestatsHealthChecker(@NotNull final String runDirectory, @NotNull final PrestatsExtractor dataExtractor) {
        this.runDirectory = runDirectory;
        this.dataExtractor = dataExtractor;
    }

    @Override
    public BaseReport runCheck() throws IOException, EmptyFileException {
        final PrestatsReport prestatsReport = dataExtractor.extractFromRunDirectory(runDirectory);
        logPrestatsReport(prestatsReport.getReferenceSample());
        logPrestatsReport(prestatsReport.getTumorSample());
        return prestatsReport;
    }

    private void logPrestatsReport(final List<PrestatsDataReport> prestatsDataReport) {
        prestatsDataReport.forEach((prestatsData) -> {
            if (prestatsData.getStatus().equalsIgnoreCase(FAIL_ERROR)) {
                LOGGER.info(String.format(FOUND_FAILS_MSG, prestatsData.getCheckName(), prestatsData.getPatientId()));
            }
        });
    }
}
