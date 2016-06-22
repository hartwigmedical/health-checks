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
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.common.util.ErrorReport;

public class MappingHealthChecker implements HealthChecker {

    protected static final String ERROR_MSG = "Got An Exception with message: %s";

    private static final Logger LOGGER = LogManager.getLogger(MappingHealthChecker.class);

    private static final double MIN_MAPPED_PERC = 99.2d;

    private static final double MIN_PROP_PAIRED_PERC = 99.0d;

    private static final double MAX_SINGLETONS = 0.5d;

    private static final double MAX_MATE_MAP_TO_DIFF_CHR = 0.01d;

    private final String runDirectory;

    private final MappingExtractor dataExtractor;

    public MappingHealthChecker(@NotNull final String runDirectory, @NotNull final MappingExtractor dataExtractor) {
        this.runDirectory = runDirectory;
        this.dataExtractor = dataExtractor;
    }

    @Override
    @NotNull
    public BaseReport runCheck() {
        MappingReport mappingReport;
        try {
            mappingReport = dataExtractor.extractFromRunDirectory(runDirectory);
        } catch (IOException | EmptyFileException exception) {
            LOGGER.error(String.format(ERROR_MSG, exception.getMessage()));
            return new ErrorReport(CheckType.MAPPING, exception.getClass().getName(), exception.getMessage());
        }
        final MappingDataReport mappingDataReport = mappingReport.getMappingDataReport();
        logMappingReport(mappingReport, mappingDataReport);
        return mappingReport;
    }

    private void logMappingReport(final MappingReport mappingReport, final MappingDataReport mappingDataReport) {
        LOGGER.info(String.format("Checking mapping health for %s", mappingReport.getExternalId()));

        final boolean isAllReadsPresent = !mappingDataReport.isAllReadsPresent();
        logMappingReportLine(isAllReadsPresent, "OK : All Reads are present", "WARN : Not All Reads are present");
        final boolean isMappedPrecentageInRange = mappingDataReport.getMappedPercentage() < MIN_MAPPED_PERC;
        logMappingReportFormattedLine(isMappedPrecentageInRange, "OK: Acceptable getMapped percentage: %s",
                        "WARN: Low getMapped percentage: %s", mappingDataReport.getMappedPercentage());

        final boolean isProperlyPairedPerInRange = mappingDataReport
                        .getProperlyPairedPercentage() < MIN_PROP_PAIRED_PERC;
        logMappingReportFormattedLine(isProperlyPairedPerInRange, "OK: Acceptable properly paired percentage: %s",
                        "WARN: Low properly paired percentage: ", mappingDataReport.getProperlyPairedPercentage());

        final boolean isSingletonPerInRange = mappingDataReport.getSingletonPercentage() > MAX_SINGLETONS;
        logMappingReportFormattedLine(isSingletonPerInRange, "OK: Acceptable singleton percentage: %s",
                        "WARN: High singleton percentage: %s", mappingDataReport.getSingletonPercentage());

        final boolean isMatMapToDiffChrInRange = mappingDataReport
                        .getMateMappedToDifferentChrPercentage() > MAX_MATE_MAP_TO_DIFF_CHR;
        logMappingReportFormattedLine(isMatMapToDiffChrInRange,
                        "OK: Acceptable mate getMapped to different chr percentage: %s",
                        "WARN: High mate getMapped to different chr percentage: %s",
                        mappingDataReport.getMateMappedToDifferentChrPercentage());

        final boolean isPropOfDuplicateInRange = mappingDataReport
                        .getProportionOfDuplicateRead() > MAX_MATE_MAP_TO_DIFF_CHR;
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
