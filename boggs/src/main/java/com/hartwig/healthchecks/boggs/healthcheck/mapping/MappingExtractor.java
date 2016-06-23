package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boggs.extractor.BoggsExtractor;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatParser;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStats;
import com.hartwig.healthchecks.boggs.model.report.MappingDataReport;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.boggs.reader.ZipFileReader;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.CheckType;

public class MappingExtractor extends BoggsExtractor {

    private static final Logger LOGGER = LogManager.getLogger(MappingExtractor.class);

    private static final Long MILLIS_FACTOR = 10000L;

    private static final Double HUNDRED_FACTOR = 100D;

    private static final Integer DOUBLE_SEQUENCE = 2;

    private static final String REALIGN = "realign";

    private static final String FLAGSTAT_SUFFIX = ".flagstat";

    private static final double MIN_MAPPED_PERC = 99.2d;

    private static final double MIN_PROP_PAIRED_PERC = 99.0d;

    private static final double MAX_SINGLETONS = 0.5d;

    private static final double MAX_MATE_MAP_TO_DIFF_CHR = 0.01d;

    @NotNull
    private final FlagStatParser flagstatParser;

    @NotNull
    private final ZipFileReader zipFileReader;

    public MappingExtractor(@NotNull final FlagStatParser flagstatParser, final ZipFileReader zipFileReader) {
        super();
        this.flagstatParser = flagstatParser;
        this.zipFileReader = zipFileReader;
    }

    @NotNull
    private static double toPercentage(@NotNull final double percentage) {
        return Math.round(percentage * MILLIS_FACTOR) / HUNDRED_FACTOR;
    }

    public MappingReport extractFromRunDirectory(@NotNull final String runDirectory)
                    throws IOException, EmptyFileException {
        final Optional<Path> sampleFile = getFilesPath(runDirectory, SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);
        final String externalId = sampleFile.get().getFileName().toString();
        final Long totalSequences = sumOfTotalSequences(sampleFile.get(), zipFileReader);
        final MappingDataReport mappingDataReport = getFlagstatsData(sampleFile.get(), totalSequences.toString());
        final MappingReport mappingReport = new MappingReport(CheckType.MAPPING, externalId, totalSequences.toString(),
                        mappingDataReport);
        logMappingReport(mappingReport, mappingDataReport);
        return mappingReport;
    }

    private MappingDataReport getFlagstatsData(@NotNull final Path runDirPath, @NotNull final String totalSequences)
                    throws IOException, EmptyFileException {
        final Optional<Path> filePath = Files
                        .walk(new File(runDirPath + File.separator + MAPPING + File.separator).toPath())
                        .filter(path -> path.getFileName().toString().endsWith(FLAGSTAT_SUFFIX)
                                        && path.getFileName().toString().contains(REALIGN))
                        .findFirst();

        if (!filePath.isPresent()) {
            throw new FileNotFoundException();
        }

        final FlagStatData flagstatData = flagstatParser.parse(filePath.get().toString());
        if (flagstatData == null) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, runDirPath.toString()));
        }

        final FlagStats passed = flagstatData.getQcPassedReads();

        final Double mappedPercentage = toPercentage(passed.getMapped() / passed.getTotal());
        final Double properlyPairedPercentage = toPercentage(passed.getProperlyPaired() / passed.getMapped());
        final Double singletonPercentage = passed.getSingletons();
        final Double mateMappedToDifferentChrPercentage = passed.getMateMappedToDifferentChr();
        final Double proportionOfDuplicateRead = toPercentage(passed.getDuplicates() / passed.getTotal());
        final boolean isAllReadsPresent = passed.getTotal() == Double.parseDouble(totalSequences) * DOUBLE_SEQUENCE
                        + passed.getSecondary();

        return new MappingDataReport(mappedPercentage, properlyPairedPercentage, singletonPercentage,
                        mateMappedToDifferentChrPercentage, proportionOfDuplicateRead, isAllReadsPresent);
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
