package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import java.io.IOException;

import com.hartwig.healthchecks.boggs.model.report.MappingDataReport;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.BaseReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

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

    @Override @NotNull public BaseReport runCheck() throws IOException, EmptyFileException {
        final MappingReport mappingReport = dataExtractor.extractFromRunDirectory(runDirectory);
        final MappingDataReport mappingDataReport = mappingReport.getMappingDataReport();

        logMappingReport(mappingReport, mappingDataReport);

        return mappingReport;
    }

    private void logMappingReport(final MappingReport mappingReport, final MappingDataReport mappingDataReport) {
        LOGGER.info(String.format("Checking mapping health for %s", mappingReport.getExternalId()));

        boolean isAllReadsPresent = !mappingDataReport.isAllReadsPresent();
        logMappingReportLine(isAllReadsPresent, "OK : All Reads are present", "WARN : Not All Reads are present %s");
        boolean isMappedPrecentageInRange = mappingDataReport.getMappedPercentage() < MIN_MAPPED_PERCENTAGE;
        logMappingReportFormattedLine(isMappedPrecentageInRange, "OK: Acceptable mapped percentage: %s",
                "WARN: Low mapped percentage: %s", mappingDataReport.getMappedPercentage());

        boolean isProperlyPairedPercentageInRange =
                mappingDataReport.getProperlyPairedPercentage() < MIN_PROPERLY_PAIRED_PERCENTAGE;
        logMappingReportFormattedLine(isProperlyPairedPercentageInRange,
                "OK: Acceptable properly paired percentage: %s", "WARN: Low properly paired percentage: ",
                mappingDataReport.getProperlyPairedPercentage());

        boolean isSingletonPerInRange = mappingDataReport.getSingletonPercentage() > MAX_SINGLETONS;
        logMappingReportFormattedLine(isSingletonPerInRange, "OK: Acceptable singleton percentage: %s",
                "WARN: High singleton percentage: %s", mappingDataReport.getSingletonPercentage());

        boolean isMatMapToDiffChrInRange =
                mappingDataReport.getMateMappedToDifferentChrPercentage() > MAX_MATE_MAPPED_TO_DIFFERENT_CHR;
        logMappingReportFormattedLine(isMatMapToDiffChrInRange,
                "OK: Acceptable mate mapped to different chr percentage: %s",
                "WARN: High mate mapped to different chr percentage: %s",
                mappingDataReport.getMateMappedToDifferentChrPercentage());

        boolean isPropOfDuplicateInRange =
                mappingDataReport.getProportionOfDuplicateRead() > MAX_MATE_MAPPED_TO_DIFFERENT_CHR;
        logMappingReportFormattedLine(isPropOfDuplicateInRange,
                "OK: Acceptable proportion of Duplication percentage: %s",
                "WARN: High proportion of Duplication percentage: %s",
                mappingDataReport.getProportionOfDuplicateRead());
    }

    private void logMappingReportLine(final boolean failStatus, final String succesMessage, final String failMessage) {
        String message = succesMessage;
        if (failStatus) {
            message = failMessage;
        }
        LOGGER.info(message);
    }

    private void logMappingReportFormattedLine(final boolean failStatus, final String succesMessage,
            final String failMessage, final Double value) {
        logMappingReportLine(failStatus, String.format(succesMessage, value), String.format(failMessage, value));
    }
}
