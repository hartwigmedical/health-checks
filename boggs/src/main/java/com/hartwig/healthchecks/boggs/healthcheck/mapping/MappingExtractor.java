package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boggs.extractor.AbstractBoggsExtractor;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatParser;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStats;
import com.hartwig.healthchecks.boggs.healthcheck.function.DivisionOperator;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.boggs.reader.ZipFileReader;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class MappingExtractor extends AbstractBoggsExtractor {

    private static final Long MILLIS_FACTOR = 10000L;

    private static final String REALIGN = "realign";

    @NotNull
    private final FlagStatParser flagstatParser;

    @NotNull
    private final ZipFileReader zipFileReader;

    public MappingExtractor(@NotNull final FlagStatParser flagstatParser, final ZipFileReader zipFileReader) {
        super();
        this.flagstatParser = flagstatParser;
        this.zipFileReader = zipFileReader;
    }

    @Override
    @NotNull
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
                    throws IOException, HealthChecksException {
        final List<BaseDataReport> refSampleData = getSampleData(runDirectory, SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumorSampleData = getSampleData(runDirectory, SAMPLE_PREFIX, TUM_SAMPLE_SUFFIX);

        return new MappingReport(CheckType.MAPPING, refSampleData, tumorSampleData);
    }

    @NotNull
    private List<BaseDataReport> getSampleData(@NotNull final String runDirectory, @NotNull final String prefix,
                    @NotNull final String suffix) throws IOException, EmptyFileException {
        final Path sampleFile = zipFileReader.getZipFilesPath(runDirectory, prefix, suffix);

        final String patientId = sampleFile.getFileName().toString();
        final Long totalSequences = sumOfTotalSequences(sampleFile, zipFileReader);
        final List<BaseDataReport> mappingChecks = getFlagStatsData(patientId, sampleFile, totalSequences.toString());

        logBaseDataReports(mappingChecks);
        return mappingChecks;
    }

    @NotNull
    private static double toPercentage(@NotNull final double percentage) {
        return Math.round(percentage * MILLIS_FACTOR) / HUNDRED_FACTOR;
    }

    private List<BaseDataReport> getFlagStatsData(@NotNull final String patientId, @NotNull final Path runDirPath,
                    @NotNull final String totalSequences) throws IOException, EmptyFileException {

        final FlagStatData flagstatData = flagstatParser.parse(runDirPath + File.separator + MAPPING + File.separator,
                        REALIGN);
        if (flagstatData == null) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, FLAGSTAT_SUFFIX, runDirPath.toString()));
        }
        final List<BaseDataReport> mappingDataReports = new ArrayList<>();
        final List<FlagStats> passed = flagstatData.getPassedStats();

        final BaseDataReport mappedDataReport = generateMappedDataReport(patientId, passed);
        mappingDataReports.add(mappedDataReport);

        final BaseDataReport properDataReport = generateProperDataReport(patientId, passed);
        mappingDataReports.add(properDataReport);

        final BaseDataReport singletonDataReport = generateSingletonDataReport(patientId, passed);
        mappingDataReports.add(singletonDataReport);

        final BaseDataReport mateMappedDataReport = generateMateMappedDataReport(patientId, passed);
        mappingDataReports.add(mateMappedDataReport);

        final BaseDataReport duplicateDataReport = generateDuplicateDataReport(patientId, passed);
        mappingDataReports.add(duplicateDataReport);

        final BaseDataReport isAllReadDataReport = generateIsAllReadDataReport(patientId, totalSequences, passed);
        mappingDataReports.add(isAllReadDataReport);

        return mappingDataReports;
    }

    @NotNull
    private BaseDataReport generateMappedDataReport(@NotNull final String patientId,
                    @NotNull final List<FlagStats> passed) {

        final FlagStats mappedStat = passed.get(FlagStatsType.MAPPED_INDEX.getIndex());
        final FlagStats totalStat = passed.get(FlagStatsType.TOTAL_INDEX.getIndex());
        final DivisionOperator mappedStatCalc = FlagStatsType.MAPPED_INDEX.getCalculableInstance();
        final double mappedPercentage = toPercentage(
                        mappedStatCalc.calculate(mappedStat.getValue(), totalStat.getValue()));

        final BaseDataReport mappedDataReport = new BaseDataReport(patientId,
                        MappingCheck.MAPPING_MAPPED.getDescription(), String.valueOf(mappedPercentage));

        return mappedDataReport;
    }

    @NotNull
    private BaseDataReport generateProperDataReport(@NotNull final String patientId,
                    @NotNull final List<FlagStats> passed) {

        final FlagStats mappedStat = passed.get(FlagStatsType.MAPPED_INDEX.getIndex());
        final FlagStats properPaired = passed.get(FlagStatsType.PROPERLY_PAIRED_INDEX.getIndex());
        final DivisionOperator properStatCalc = FlagStatsType.PROPERLY_PAIRED_INDEX.getCalculableInstance();
        final double properlyPairedPercentage = toPercentage(
                        properStatCalc.calculate(properPaired.getValue(), mappedStat.getValue()));

        final BaseDataReport properReport = new BaseDataReport(patientId,
                        MappingCheck.MAPPING_PROPERLY_PAIRED.getDescription(),
                        String.valueOf(properlyPairedPercentage));

        return properReport;
    }

    @NotNull
    private BaseDataReport generateSingletonDataReport(@NotNull final String patientId,
                    @NotNull final List<FlagStats> passed) {

        final FlagStats singletonStat = passed.get(FlagStatsType.SINGELTONS_INDEX.getIndex());
        final double singletonPercentage = singletonStat.getValue();

        final BaseDataReport singletonReport = new BaseDataReport(patientId,
                        MappingCheck.MAPPING_SINGLETON.getDescription(), String.valueOf(singletonPercentage));

        return singletonReport;
    }

    @NotNull
    private BaseDataReport generateMateMappedDataReport(@NotNull final String patientId,
                    @NotNull final List<FlagStats> passed) {

        final FlagStats diffPercStat = passed.get(FlagStatsType.MATE_MAP_DIF_CHR_INDEX.getIndex());
        final double mateMappedDiffChrPerc = diffPercStat.getValue();

        final BaseDataReport mateMappedDiffReport = new BaseDataReport(patientId,
                        MappingCheck.MAPPING_MATE_MAPPED_DIFFERENT_CHR.getDescription(),
                        String.valueOf(mateMappedDiffChrPerc));

        return mateMappedDiffReport;
    }

    @NotNull
    private BaseDataReport generateDuplicateDataReport(@NotNull final String patientId,
                    @NotNull final List<FlagStats> passed) {

        final FlagStats totalStat = passed.get(FlagStatsType.TOTAL_INDEX.getIndex());
        final FlagStats duplicateStat = passed.get(FlagStatsType.DUPLICATES_INDEX.getIndex());
        final DivisionOperator duplicateStatCalc = FlagStatsType.DUPLICATES_INDEX.getCalculableInstance();
        final double proportionOfDuplicateRead = toPercentage(
                        duplicateStatCalc.calculate(duplicateStat.getValue(), totalStat.getValue()));

        final BaseDataReport duplicateReport = new BaseDataReport(patientId,
                        MappingCheck.MAPPING_DUPLIVATES.getDescription(), String.valueOf(proportionOfDuplicateRead));

        return duplicateReport;
    }

    @NotNull
    private BaseDataReport generateIsAllReadDataReport(@NotNull final String patientId,
                    @NotNull final String totalSequences, @NotNull final List<FlagStats> passed) {

        final FlagStats totalStat = passed.get(FlagStatsType.TOTAL_INDEX.getIndex());
        final FlagStats secondaryStat = passed.get(FlagStatsType.SECONDARY_INDEX.getIndex());
        final boolean isAllReadsPresent = totalStat.getValue() == Double.parseDouble(totalSequences) * DOUBLE_SEQUENCE
                        + secondaryStat.getValue();

        final BaseDataReport isAllReadReport = new BaseDataReport(patientId,
                        MappingCheck.MAPPING_IS_ALL_READ.getDescription(), String.valueOf(isAllReadsPresent));

        return isAllReadReport;
    }
}
