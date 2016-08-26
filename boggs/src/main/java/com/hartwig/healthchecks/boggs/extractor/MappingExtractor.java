package com.hartwig.healthchecks.boggs.extractor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatParser;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStats;
import com.hartwig.healthchecks.boggs.flagstatreader.SambambaFlagStatParser;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.extractor.AbstractTotalSequenceExtractor;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.reader.ZipFilesReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.PatientReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class MappingExtractor extends AbstractTotalSequenceExtractor {

    private static final Logger LOGGER = LogManager.getLogger(MappingExtractor.class);

    private static final String FASTQC_BASE_DIRECTORY = "QCStats";
    private static final String FLAGSTAT_BASE_DIRECTORY = "mapping";

    private static final String FLAGSTAT_FILE_FILTER = ".realign";

    @NotNull
    private final RunContext runContext;
    @NotNull
    private final FlagStatParser flagstatParser = new SambambaFlagStatParser();
    @NotNull
    private final ZipFilesReader zipFileReader = new ZipFilesReader();

    public MappingExtractor(@NotNull final RunContext runContext) {
        this.runContext = runContext;
    }

    @Override
    @NotNull
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<BaseDataReport> refSampleData = getSampleData(runContext.runDirectory(), runContext.refSample());
        final List<BaseDataReport> tumorSampleData = getSampleData(runContext.runDirectory(),
                runContext.tumorSample());

        return new PatientReport(CheckType.MAPPING, refSampleData, tumorSampleData);
    }

    @NotNull
    private List<BaseDataReport> getSampleData(@NotNull final String runDirectory, @NotNull final String sampleId)
            throws IOException, HealthChecksException {

        final String basePathForTotalSequences = getBasePathForTotalSequences(runDirectory, sampleId);
        final long totalSequences = sumOfTotalSequencesFromFastQC(basePathForTotalSequences, zipFileReader);

        final String basePathForFlagStat = getBasePathForFlagStat(runDirectory, sampleId);
        final List<BaseDataReport> mappingChecks = getFlagStatsData(basePathForFlagStat, sampleId, totalSequences);

        BaseDataReport.log(LOGGER, mappingChecks);
        return mappingChecks;
    }

    @NotNull
    private List<BaseDataReport> getFlagStatsData(@NotNull final String basePath, @NotNull final String sampleId,
            final long totalSequences) throws IOException, EmptyFileException {
        final FlagStatData flagstatData = flagstatParser.parse(basePath, FLAGSTAT_FILE_FILTER);

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
        final double mappedPercentage = mappedStat.getValue() / totalStat.getValue();

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_PERCENTAGE_MAPPED.toString(),
                String.valueOf(mappedPercentage));
    }

    @NotNull
    private static BaseDataReport properlyPairedPercentage(@NotNull final String sampleId,
            @NotNull final List<FlagStats> passed) {
        final FlagStats mappedStat = passed.get(FlagStatsType.MAPPED_INDEX.getIndex());
        final FlagStats properPaired = passed.get(FlagStatsType.PROPERLY_PAIRED_INDEX.getIndex());
        final double properlyPairedPercentage = properPaired.getValue() / mappedStat.getValue();

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_PROPERLY_PAIRED_PROPORTION_OF_MAPPED.toString(),
                String.valueOf(properlyPairedPercentage));
    }

    @NotNull
    private static BaseDataReport singletonPercentage(@NotNull final String sampleId,
            @NotNull final List<FlagStats> passed) {
        final FlagStats mappedStat = passed.get(FlagStatsType.MAPPED_INDEX.getIndex());
        final FlagStats singletonStat = passed.get(FlagStatsType.SINGLETONS_INDEX.getIndex());
        final double singletonPercentage = singletonStat.getValue() / mappedStat.getValue();

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_PROPORTION_SINGLETON.toString(),
                String.valueOf(singletonPercentage));
    }

    @NotNull
    private static BaseDataReport mateMappedDiffChrPercentage(@NotNull final String sampleId,
            @NotNull final List<FlagStats> passed) {
        final FlagStats mappedStat = passed.get(FlagStatsType.MAPPED_INDEX.getIndex());
        final FlagStats diffPercStat = passed.get(FlagStatsType.MATE_MAP_DIF_CHR_INDEX.getIndex());
        final double mateMappedDiffChrPerc = diffPercStat.getValue() / mappedStat.getValue();

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_PROPORTION_MAPPED_DIFFERENT_CHR.toString(),
                String.valueOf(mateMappedDiffChrPerc));
    }

    @NotNull
    private static BaseDataReport duplicatePercentage(@NotNull final String sampleId,
            @NotNull final List<FlagStats> passed) {
        final FlagStats totalStat = passed.get(FlagStatsType.TOTAL_INDEX.getIndex());
        final FlagStats duplicateStat = passed.get(FlagStatsType.DUPLICATES_INDEX.getIndex());
        final double proportionOfDuplicateRead = duplicateStat.getValue() / totalStat.getValue();

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_MARKDUP_PROPORTION_DUPLICATES.toString(),
                String.valueOf(proportionOfDuplicateRead));
    }

    @NotNull
    private static BaseDataReport proportionOfReadsUsed(@NotNull final String sampleId, final long totalSequences,
            @NotNull final List<FlagStats> passed) {
        final FlagStats totalStat = passed.get(FlagStatsType.TOTAL_INDEX.getIndex());
        final FlagStats secondaryStat = passed.get(FlagStatsType.SECONDARY_INDEX.getIndex());
        double proportionReadPercentage = (totalStat.getValue() - secondaryStat.getValue()) / totalSequences;

        return new BaseDataReport(sampleId, MappingCheck.MAPPING_PROPORTION_READ_VS_TOTAL_SEQUENCES.toString(),
                String.valueOf(proportionReadPercentage));
    }

    @NotNull
    private static String getBasePathForFlagStat(@NotNull final String runDirectory, @NotNull final String sampleId) {
        return runDirectory + File.separator + sampleId + File.separator + FLAGSTAT_BASE_DIRECTORY;
    }

    @NotNull
    private static String getBasePathForTotalSequences(@NotNull final String runDirectory,
            @NotNull final String sampleId) {
        return runDirectory + File.separator + sampleId + File.separator + FASTQC_BASE_DIRECTORY;
    }
}
