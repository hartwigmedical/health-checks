package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import java.io.IOException;
import java.util.List;

import com.hartwig.healthchecks.boggs.model.report.BaseDataReport;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.BaseReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class MappingHealthChecker implements HealthChecker {

    private static final Logger LOGGER = LogManager.getLogger(MappingHealthChecker.class);

    private final String runDirectory;

    private final MappingExtractor dataExtractor;

    public MappingHealthChecker(@NotNull final String runDirectory, @NotNull final MappingExtractor dataExtractor) {
        this.runDirectory = runDirectory;
        this.dataExtractor = dataExtractor;
    }

    @Override
    @NotNull
    public BaseReport runCheck() throws IOException, EmptyFileException {
        final MappingReport mappingReport = dataExtractor.extractFromRunDirectory(runDirectory);
        final List<BaseDataReport> mapping = mappingReport.getMapping();

        mapping.forEach(report -> {
            LOGGER.info("Result for mapping health check '%s' is '%s'", report.getCheckName(), report.getValue());
        });

        return mappingReport;
    }
}