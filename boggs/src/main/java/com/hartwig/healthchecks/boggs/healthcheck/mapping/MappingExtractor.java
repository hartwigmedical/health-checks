package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.hartwig.healthchecks.boggs.extractor.BoggsExtractor;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatParser;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStats;
import com.hartwig.healthchecks.boggs.healthcheck.function.DivisionOperator;
import com.hartwig.healthchecks.boggs.model.report.BaseDataReport;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.CheckType;

import org.jetbrains.annotations.NotNull;

public class MappingExtractor extends BoggsExtractor {

    private static final Long MILLIS_FACTOR = 10000L;

    private static final Double HUNDRED_FACTOR = 100D;

    private static final Integer DOUBLE_SEQUENCE = 2;

    private static final String REALIGN = "realign";

    private static final String FLAGSTAT_SUFFIX = ".flagstat";

    @NotNull
    private final FlagStatParser flagstatParser;

    public MappingExtractor(@NotNull final FlagStatParser flagstatParser) {
        super();
        this.flagstatParser = flagstatParser;
    }

    @NotNull
    public MappingReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, EmptyFileException {

        final Optional<Path> sampleFile = getFilesPath(runDirectory, SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);
        final String externalId = sampleFile.get().getFileName().toString();
        final Long totalSequences = sumOfTotalSequences(sampleFile.get());
        final List<BaseDataReport> mapping = getFlagStatsData(externalId, sampleFile.get(),
                totalSequences.toString());

        final MappingReport report = new MappingReport(CheckType.MAPPING);
        report.addAll(mapping);

        return report;
    }

    @NotNull
    private static double toPercentage(@NotNull final double percentage) {
        return Math.round(percentage * MILLIS_FACTOR) / HUNDRED_FACTOR;
    }

    private List<BaseDataReport> getFlagStatsData(@NotNull final String externalId, @NotNull final Path runDirPath,
            @NotNull final String totalSequences) throws IOException, EmptyFileException {

        final FlagStatData flagstatData = parseFlagStatFile(runDirPath);

        final List<BaseDataReport> mappingDataReports = new ArrayList<>();
        final List<FlagStats> passed = flagstatData.getPassedStats();

        final BaseDataReport mappedDataReport = generateMappedDataReport(externalId, passed);
        mappingDataReports.add(mappedDataReport);

        final BaseDataReport properDataReport = generateProperDataReport(externalId, passed);
        mappingDataReports.add(properDataReport);

        final BaseDataReport singletonDataReport = generateSingletonDataReport(externalId, passed);
        mappingDataReports.add(singletonDataReport);

        final BaseDataReport mateMappedDataReport = generateMateMappedDataReport(externalId, passed);
        mappingDataReports.add(mateMappedDataReport);

        final BaseDataReport duplicateDataReport = generateDuplicateDataReport(externalId, passed);
        mappingDataReports.add(duplicateDataReport);

        final BaseDataReport isAllReadDataReport = generateIsAllReadDataReport(externalId, totalSequences, passed);
        mappingDataReports.add(isAllReadDataReport);

        return mappingDataReports;
    }

    @NotNull
    private FlagStatData parseFlagStatFile(final @NotNull Path runDirPath) throws IOException, EmptyFileException {
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
        return flagstatData;
    }

    @NotNull
    private BaseDataReport generateMappedDataReport(@NotNull final String externalId,
            @NotNull final  List<FlagStats> passed) {

        final FlagStats mappedStat = passed.get(FlagStatsType.MAPPED_INDEX.getIndex());
        final FlagStats totalStat = passed.get(FlagStatsType.TOTAL_INDEX.getIndex());
        final DivisionOperator mappedStatCalc = FlagStatsType.MAPPED_INDEX.getCalculableInstance();
        final double mappedPercentage = toPercentage(mappedStatCalc.calculate(mappedStat.getValue(),
                totalStat.getValue()));

        final BaseDataReport mappedDataReport = new BaseDataReport(externalId,
                MappingCheck.MAPPING_MAPPED.getDescription(),
                String.valueOf(mappedPercentage));

        return mappedDataReport;
    }

    @NotNull
    private BaseDataReport generateProperDataReport(@NotNull final String externalId,
            @NotNull final  List<FlagStats> passed) {

        final FlagStats mappedStat = passed.get(FlagStatsType.MAPPED_INDEX.getIndex());
        final FlagStats properPaired = passed.get(FlagStatsType.PROPERLY_PAIRED_INDEX.getIndex());
        final DivisionOperator properStatCalc = FlagStatsType.PROPERLY_PAIRED_INDEX.getCalculableInstance();
        final double properlyPairedPercentage = toPercentage(properStatCalc.calculate(properPaired.getValue(),
                mappedStat.getValue()));

        final BaseDataReport properReport = new BaseDataReport(externalId,
                MappingCheck.MAPPING_PROPERLY_PAIRED.getDescription(),
                String.valueOf(properlyPairedPercentage));

        return properReport;
    }

    @NotNull
    private BaseDataReport generateSingletonDataReport(@NotNull final String externalId,
            @NotNull final  List<FlagStats> passed) {

        final FlagStats singletonStat = passed.get(FlagStatsType.SINGELTONS_INDEX.getIndex());
        final double singletonPercentage = singletonStat.getValue();

        final BaseDataReport singletonReport = new BaseDataReport(externalId,
                MappingCheck.MAPPING_SINGLETON.getDescription(),
                String.valueOf(singletonPercentage));

        return singletonReport;
    }

    @NotNull
    private BaseDataReport generateMateMappedDataReport(@NotNull final String externalId,
            @NotNull final  List<FlagStats> passed) {

        final FlagStats diffPercStat = passed.get(FlagStatsType.MATE_MAP_DIF_CHR_INDEX.getIndex());
        final double mateMappedDiffChrPercentage = diffPercStat.getValue();

        final BaseDataReport mateMappedDiffReport = new BaseDataReport(externalId,
                MappingCheck.MAPPING_MATE_MAPPED_DIFFERENT_CHR.getDescription(),
                String.valueOf(mateMappedDiffChrPercentage));

        return mateMappedDiffReport;
    }

    @NotNull
    private BaseDataReport generateDuplicateDataReport(@NotNull final String externalId,
            @NotNull final  List<FlagStats> passed) {

        final FlagStats totalStat = passed.get(FlagStatsType.TOTAL_INDEX.getIndex());
        final FlagStats duplicateStat = passed.get(FlagStatsType.DUPLICATES_INDEX.getIndex());
        final DivisionOperator duplicateStatCalc = FlagStatsType.DUPLICATES_INDEX.getCalculableInstance();
        final double proportionOfDuplicateRead = toPercentage(duplicateStatCalc.calculate(duplicateStat.getValue(),
                totalStat.getValue()));

        final BaseDataReport duplicateReport = new BaseDataReport(externalId,
                MappingCheck.MAPPING_DUPLIVATES.getDescription(),
                String.valueOf(proportionOfDuplicateRead));

        return duplicateReport;
    }

    @NotNull
    private BaseDataReport generateIsAllReadDataReport(@NotNull final String externalId,
            @NotNull final String totalSequences, @NotNull final  List<FlagStats> passed) {

        final FlagStats totalStat = passed.get(FlagStatsType.TOTAL_INDEX.getIndex());
        final FlagStats secondaryStat = passed.get(FlagStatsType.SECONDARY_INDEX.getIndex());
        final boolean isAllReadsPresent = totalStat.getValue() == Double.parseDouble(totalSequences) * DOUBLE_SEQUENCE
                + secondaryStat.getValue();

        final BaseDataReport isAllReadReport = new BaseDataReport(externalId,
                MappingCheck.MAPPING_IS_ALL_READ.getDescription(),
                String.valueOf(isAllReadsPresent));

        return isAllReadReport;
    }
}
