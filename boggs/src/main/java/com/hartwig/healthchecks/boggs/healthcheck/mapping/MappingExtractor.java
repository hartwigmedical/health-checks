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
    private static double toPercentage(@NotNull final double percentage) {
        return Math.round(percentage * MILLIS_FACTOR) / HUNDRED_FACTOR;
    }

    @NotNull
    public MappingReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, EmptyFileException {

        final Optional<Path> sampleFile = getFilesPath(runDirectory, SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);
        final String externalId = sampleFile.get().getFileName().toString();
        final Long totalSequences = sumOfTotalSequences(sampleFile.get());
        final List<BaseDataReport> mapping = getFlagStatsData(externalId, sampleFile.get(),
                totalSequences.toString());

        MappingReport report = new MappingReport(CheckType.MAPPING);
        report.addAll(mapping);

        return report;
    }

    private List<BaseDataReport> getFlagStatsData(@NotNull final String externalId, @NotNull final Path runDirPath,
            @NotNull final String totalSequences) throws IOException, EmptyFileException {

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

        final List<BaseDataReport> mappingDataReports = new ArrayList<>();
        final List<FlagStats> passed = flagstatData.getPassedStats();

        final FlagStats mappedStat = passed.get(FlagStatsType.MAPPED_INDEX.getIndex());
        final FlagStats totalStat = passed.get(FlagStatsType.TOTAL_INDEX.getIndex());
        final DivisionOperator mappedStatCalc = FlagStatsType.MAPPED_INDEX.getCalculableInstance();
        final double mappedPercentage = toPercentage(mappedStatCalc.calculate(mappedStat.getValue(), totalStat.getValue()));

        final BaseDataReport percentageReport = new BaseDataReport(externalId, mappedStat.getCheckType(), String.valueOf(mappedPercentage));
        mappingDataReports.add(percentageReport);

        final FlagStats properPaired = passed.get(FlagStatsType.PROPERLY_PAIRED_INDEX.getIndex());
        final DivisionOperator properStatCalc = FlagStatsType.PROPERLY_PAIRED_INDEX.getCalculableInstance();
        final double properlyPairedPercentage = toPercentage(properStatCalc.calculate(properPaired.getValue(), properPaired.getValue()));

        final BaseDataReport properlyReport = new BaseDataReport(externalId, properPaired.getCheckType(), String.valueOf(properlyPairedPercentage));
        mappingDataReports.add(properlyReport);

        final FlagStats singletonStat = passed.get(FlagStatsType.SINGELTONS_INDEX.getIndex());
        final double singletonPercentage = singletonStat.getValue();

        final BaseDataReport singletonReport = new BaseDataReport(externalId, singletonStat.getCheckType(), String.valueOf(singletonPercentage));
        mappingDataReports.add(singletonReport);

        final FlagStats diffPercStat = passed.get(FlagStatsType.MATE_MAP_DIF_CHR_INDEX.getIndex());
        final double mateMappedDiffChrPercentage = diffPercStat.getValue();

        final BaseDataReport mateMappedDiffReport = new BaseDataReport(externalId, diffPercStat.getCheckType(), String.valueOf(mateMappedDiffChrPercentage));
        mappingDataReports.add(mateMappedDiffReport);

        final FlagStats duplicateStat = passed.get(FlagStatsType.DUPLICATES_INDEX.getIndex());
        final DivisionOperator duplicateStatCalc = FlagStatsType.DUPLICATES_INDEX.getCalculableInstance();
        final double proportionOfDuplicateRead = toPercentage(duplicateStatCalc.calculate(duplicateStat.getValue(), totalStat.getValue()));

        final BaseDataReport duplicateReport = new BaseDataReport(externalId, diffPercStat.getCheckType(), String.valueOf(proportionOfDuplicateRead));
        mappingDataReports.add(duplicateReport);

        final FlagStats secondaryStat = passed.get(FlagStatsType.SECONDARY_INDEX.getIndex());
        final boolean isAllReadsPresent = totalStat.getValue() == Double.parseDouble(totalSequences) * DOUBLE_SEQUENCE
                + secondaryStat.getValue();

        final BaseDataReport secondaryReport = new BaseDataReport(externalId, secondaryStat.getCheckType(), String.valueOf(isAllReadsPresent));
        mappingDataReports.add(secondaryReport);

        return mappingDataReports;
    }
}
