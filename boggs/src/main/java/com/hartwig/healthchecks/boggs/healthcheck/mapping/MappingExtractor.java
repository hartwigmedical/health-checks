package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.hartwig.healthchecks.boggs.extractor.BoggsExtractor;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatParser;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStats;
import com.hartwig.healthchecks.boggs.model.report.MappingDataReport;
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

    public MappingReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, EmptyFileException {
        final Optional<Path> sampleFile = getFilesPath(runDirectory, SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);
        final String externalId = sampleFile.get().getFileName().toString();
        final Long totalSequences = sumOfTotalSequences(sampleFile.get());
        final MappingDataReport mappingDataReport = getFlagstatsData(sampleFile.get(), totalSequences.toString());
        return new MappingReport(CheckType.MAPPING, externalId, totalSequences.toString(), mappingDataReport);
    }

    private MappingDataReport getFlagstatsData(@NotNull final Path path, @NotNull final String totalSequences)
            throws IOException, EmptyFileException {
        final Optional<Path> filePath = Files.walk(
                new File(path + File.separator + MAPPING + File.separator).toPath()).filter(
                p -> p.getFileName().toString().endsWith(FLAGSTAT_SUFFIX) && p.getFileName().toString().contains(
                        REALIGN)).findFirst();

        if (!filePath.isPresent()) {
            throw new FileNotFoundException();
        }

        final FlagStatData flagstatData = flagstatParser.parse(filePath.get().toString());
        if (flagstatData == null) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, path.toString()));
        }

        final FlagStats passed = flagstatData.getQcPassedReads();

        final Double mappedPercentage = toPercentage(passed.getMapped() / passed.getTotal());
        final Double properlyPairedPercentage = toPercentage(passed.getProperlyPaired() / passed.getMapped());
        final Double singletonPercentage = passed.getSingletons();
        final Double mateMappedToDifferentChrPercentage = passed.getMateMappedToDifferentChr();
        final Double proportionOfDuplicateRead = toPercentage(passed.getDuplicates() / passed.getTotal());
        final boolean isAllReadsPresent =
                passed.getTotal() == (Double.parseDouble(totalSequences) * DOUBLE_SEQUENCE) + passed.getSecondary();

        return new MappingDataReport(mappedPercentage, properlyPairedPercentage, singletonPercentage,
                mateMappedToDifferentChrPercentage, proportionOfDuplicateRead, isAllReadsPresent);
    }
}
