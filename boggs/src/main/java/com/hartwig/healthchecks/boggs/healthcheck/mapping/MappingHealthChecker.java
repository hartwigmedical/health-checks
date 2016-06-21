package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boggs.model.report.MappingDataReport;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.BaseReport;

public class MappingHealthChecker implements HealthChecker {
    private static final Logger LOGGER = LogManager.getLogger(MappingHealthChecker.class);

    private static final double MIN_MAPPED_PERCENTAGE = 99.2d;
    private static final double MIN_PROPERLY_PAIRED_PERCENTAGE = 99.0d;
    private static final double MAX_SINGLETONS = 0.5d;
    private static final double MAX_MATE_MAPPED_TO_DIFFERENT_CHR = 0.01;

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
        final MappingDataReport mappingDataReport = mappingReport.getMappingDataReport();

        logMappingReport(mappingReport, mappingDataReport);

        return mappingReport;
    }

    private void logMappingReport(final MappingReport mappingReport, final MappingDataReport mappingDataReport) {
        LOGGER.info(String.format("Checking mapping health for %s", mappingReport.getExternalId()));

        logMappingReportLine((!mappingDataReport.isAllReadsPresent()), "OK : All Reads are present",
                "WARN : Not All Reads are present");
        logMappingReportLine((mappingDataReport.getMappedPercentage() < MIN_MAPPED_PERCENTAGE),
                String.format("OK: Acceptable mapped percentage: %s", mappingDataReport.getMappedPercentage()),
                String.format("WARN: Low mapped percentage: %s", mappingDataReport.getMappedPercentage()));

        logMappingReportLine((mappingDataReport.getProperlyPairedPercentage() < MIN_PROPERLY_PAIRED_PERCENTAGE),
                String.format("OK: Acceptable properly paired percentage: %s",
                        mappingDataReport.getProperlyPairedPercentage()),
                "WARN: Low properly paired percentage: " + mappingDataReport.getProperlyPairedPercentage());

        logMappingReportLine((mappingDataReport.getSingletonPercentage() > MAX_SINGLETONS),
                String.format("OK: Acceptable singleton percentage: %s", mappingDataReport.getSingletonPercentage()),
                String.format("WARN: High singleton percentage: %s", mappingDataReport.getSingletonPercentage()));

        logMappingReportLine(
                (mappingDataReport.getMateMappedToDifferentChrPercentage() > MAX_MATE_MAPPED_TO_DIFFERENT_CHR),
                String.format("OK: Acceptable mate mapped to different chr percentage: %s",
                        mappingDataReport.getMateMappedToDifferentChrPercentage()),
                String.format("WARN: High mate mapped to different chr percentage: %s",
                        mappingDataReport.getMateMappedToDifferentChrPercentage()));

        logMappingReportLine((mappingDataReport.getProportionOfDuplicateRead() > MAX_MATE_MAPPED_TO_DIFFERENT_CHR),
                String.format("OK: Acceptable proportion of Duplication percentage: %s",
                        mappingDataReport.getProportionOfDuplicateRead()),
                String.format("WARN: High proportion of Duplication percentage: %s",
                        mappingDataReport.getProportionOfDuplicateRead()));
    }

    private void logMappingReportLine(final boolean failStatus, final String succesMessage, final String failMessage) {
        String message = succesMessage;
        if (failStatus) {
            message = failMessage;
        }
        LOGGER.info(message);
    }
}
