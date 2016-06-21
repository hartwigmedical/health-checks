package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boggs.extractor.BoggsExtractor;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatParser;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStats;
import com.hartwig.healthchecks.boggs.model.report.MappingDataReport;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.CheckType;

public class MappingExtractor extends BoggsExtractor {

    private static final Long MILLIS_FACTOR = 10000L;
    private static final Double HUNDRED_FACTOR = 100D;
    private static final Integer DOUBLE_SEQUENCE = 2;

    private static final String REALIGN = "realign";
    private static final String FLAGSTAT_SUFFIX = ".flagstat";

    @NotNull
    private final FlagStatParser flagstatParser;

    public MappingExtractor(@NotNull final FlagStatParser flagstatParser) {
        this.flagstatParser = flagstatParser;
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
        final Optional<Path> filePath = Files.walk(new File(path + File.separator + MAPPING + File.separator).toPath())
                .filter(p -> p.getFileName().toString().endsWith(FLAGSTAT_SUFFIX)
                        && p.getFileName().toString().contains(REALIGN))
                .findFirst();

        if (!filePath.isPresent()) {
            throw new FileNotFoundException();
        }

        final FlagStatData flagstatData = flagstatParser.parse(filePath.get().toString());
        if (flagstatData == null) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, path.toString()));
        }

        final FlagStats passed = flagstatData.qcPassedReads();

        final Double mappedPercentage = toPercentage(passed.mapped() / passed.total());
        final Double properlyPairedPercentage = toPercentage(passed.properlyPaired() / passed.mapped());
        final Double singletonPercentage = passed.singletons();
        final Double mateMappedToDifferentChrPercentage = passed.mateMappedToDifferentChr();
        final Double proportionOfDuplicateRead = toPercentage(passed.duplicates() / passed.total());
        final boolean isAllReadsPresent = passed.total() == (Double.parseDouble(totalSequences) * DOUBLE_SEQUENCE)
                + passed.secondary();

        return new MappingDataReport(mappedPercentage, properlyPairedPercentage, singletonPercentage,
                mateMappedToDifferentChrPercentage, proportionOfDuplicateRead, isAllReadsPresent);
    }

    @NotNull
    private static double toPercentage(@NotNull final double percentage) {
        return Math.round(percentage * MILLIS_FACTOR) / HUNDRED_FACTOR;
    }
}
