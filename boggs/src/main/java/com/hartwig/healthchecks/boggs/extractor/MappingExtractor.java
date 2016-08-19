package com.hartwig.healthchecks.boggs.extractor;

import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.REF_SAMPLE_SUFFIX;
import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.SAMPLE_PREFIX;
import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.TUM_SAMPLE_SUFFIX;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatParser;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStats;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.extractor.AbstractTotalSequenceExtractor;
import com.hartwig.healthchecks.common.io.path.SamplePathFinder;
import com.hartwig.healthchecks.common.io.reader.ZipFilesReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class MappingExtractor extends AbstractTotalSequenceExtractor {

    private static final Logger LOGGER = LogManager.getLogger(MappingExtractor.class);

    private static final long MILLIS_FACTOR = 10000L;
    private static final double HUNDRED_FACTOR = 100D;

    private static final String FLAGSTAT_DIRECTORY = "mapping";
    private static final String FLAGSTAT_FILE_FILTER = ".realign";
    private static final String FLAGSTAT_SUFFIX = ".flagstat";

    // TODO (KODU): Replace samplePathFinder with proper run context
    @NotNull
    private final FlagStatParser flagstatParser;
    @NotNull
    private final ZipFilesReader zipFileReader;
    @NotNull
    private final SamplePathFinder samplePathFinder;

    public MappingExtractor(@NotNull final FlagStatParser flagstatParser, @NotNull final ZipFilesReader zipFileReader,
            @NotNull final SamplePathFinder samplePathFinder) {
        super();
        this.flagstatParser = flagstatParser;
        this.zipFileReader = zipFileReader;
        this.samplePathFinder = samplePathFinder;
    }

    @Override
    @NotNull
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<BaseDataReport> refSampleData = getSampleData(runDirectory, SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumorSampleData = getSampleData(runDirectory, SAMPLE_PREFIX, TUM_SAMPLE_SUFFIX);

        return new SampleReport(CheckType.MAPPING, refSampleData, tumorSampleData);
    }

    @NotNull
    private List<BaseDataReport> getSampleData(@NotNull final String runDirectory, @NotNull final String prefix,
            @NotNull final String suffix) throws IOException, EmptyFileException {
        final Path sampleFile = samplePathFinder.findPath(runDirectory, prefix, suffix);

        final String sampleId = sampleFile.getFileName().toString();
        final long totalSequences = sumOfTotalSequencesFromFastQC(sampleFile, zipFileReader);
        final List<BaseDataReport> mappingChecks = getFlagStatsData(sampleId, sampleFile, totalSequences);

        BaseDataReport.log(LOGGER, mappingChecks);
        return mappingChecks;
    }

    @NotNull
    private List<BaseDataReport> getFlagStatsData(@NotNull final String sampleId, @NotNull final Path runDirPath,
            final long totalSequences) throws IOException, EmptyFileException {
        final FlagStatData flagstatData = flagstatParser.parse(
                runDirPath + File.separator + FLAGSTAT_DIRECTORY + File.separator, FLAGSTAT_FILE_FILTER);

        if (flagstatData == null) {
            throw new EmptyFileException(FLAGSTAT_SUFFIX, runDirPath.toString());
        }

        final List<FlagStats> passed = flagstatData.getPassedStats();

        final BaseDataReport isAllReadDataReport = proportionOfReadsUsed(sampleId, totalSequences, passed);
        final BaseDataReport mappedDataReport = mappedPercentage(sampleId, passed);
        final BaseDataReport properlyPairedDataReport = properlyPairedPercentage(sampleId, passed);
        final BaseDataReport singletonDataReport = singletonPercentage(sampleId, passed);
        final BaseDataReport mateMappedDataReport = mateMappedDiffChrPercentage(sampleId, passed);
        final BaseDataReport duplicateDataReport = duplicatePercentage(sampleId, passed);

        return Lists.newArrayList(isAllReadDataReport, mappedDataReport, properlyPairedDataReport, singletonDataReport,
                mateMappedDataReport, duplicateDataReport);
    }

    @NotNull
    private static BaseDataReport mappedPercentage(@NotNull final String sampleId,
            @NotNull final List<FlagStats> passed) {
        final FlagStats mappedStat = passed.get(FlagStatsType.MAPPED_INDEX.getIndex());
        final FlagStats totalStat = passed.get(FlagStatsType.TOTAL_INDEX.getIndex());
        final double mappedPercentage = toPercentage(mappedStat.getValue() / totalStat.getValue());

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_PERCENTAGE_MAPPED.toString(),
                String.valueOf(mappedPercentage));
    }

    @NotNull
    private static BaseDataReport properlyPairedPercentage(@NotNull final String sampleId,
            @NotNull final List<FlagStats> passed) {
        final FlagStats mappedStat = passed.get(FlagStatsType.MAPPED_INDEX.getIndex());
        final FlagStats properPaired = passed.get(FlagStatsType.PROPERLY_PAIRED_INDEX.getIndex());
        final double properlyPairedPercentage = toPercentage(properPaired.getValue() / mappedStat.getValue());

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_PROPERLY_PAIRED_PROPORTION_OF_MAPPED.toString(),
                String.valueOf(properlyPairedPercentage));
    }

    @NotNull
    private static BaseDataReport singletonPercentage(@NotNull final String sampleId,
            @NotNull final List<FlagStats> passed) {
        final FlagStats mappedStat = passed.get(FlagStatsType.MAPPED_INDEX.getIndex());
        final FlagStats singletonStat = passed.get(FlagStatsType.SINGLETONS_INDEX.getIndex());
        final double singletonPercentage = toPercentage(singletonStat.getValue() / mappedStat.getValue());

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_PROPORTION_SINGLETON.toString(),
                String.valueOf(singletonPercentage));
    }

    @NotNull
    private static BaseDataReport mateMappedDiffChrPercentage(@NotNull final String sampleId,
            @NotNull final List<FlagStats> passed) {
        final FlagStats mappedStat = passed.get(FlagStatsType.MAPPED_INDEX.getIndex());
        final FlagStats diffPercStat = passed.get(FlagStatsType.MATE_MAP_DIF_CHR_INDEX.getIndex());
        final double mateMappedDiffChrPerc = toPercentage(diffPercStat.getValue() / mappedStat.getValue());

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_PROPORTION_MAPPED_DIFFERENT_CHR.toString(),
                String.valueOf(mateMappedDiffChrPerc));
    }

    @NotNull
    private static BaseDataReport duplicatePercentage(@NotNull final String sampleId,
            @NotNull final List<FlagStats> passed) {
        final FlagStats totalStat = passed.get(FlagStatsType.TOTAL_INDEX.getIndex());
        final FlagStats duplicateStat = passed.get(FlagStatsType.DUPLICATES_INDEX.getIndex());
        final double proportionOfDuplicateRead = toPercentage(duplicateStat.getValue() / totalStat.getValue());

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_MARKDUP_PROPORTION_DUPLICATES.toString(),
                String.valueOf(proportionOfDuplicateRead));
    }

    @NotNull
    private static BaseDataReport proportionOfReadsUsed(@NotNull final String sampleId, final long totalSequences,
            @NotNull final List<FlagStats> passed) {
        final FlagStats totalStat = passed.get(FlagStatsType.TOTAL_INDEX.getIndex());
        final FlagStats secondaryStat = passed.get(FlagStatsType.SECONDARY_INDEX.getIndex());
        double proportionReadPercentage =
                toPercentage(totalStat.getValue() + secondaryStat.getValue()) / totalSequences;

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_PROPORTION_READ_VS_TOTAL_SEQUENCES.toString(),
                String.valueOf(proportionReadPercentage));
    }

    private static double toPercentage(final double percentage) {
        return Math.round(percentage * MILLIS_FACTOR) / HUNDRED_FACTOR;
    }
}
