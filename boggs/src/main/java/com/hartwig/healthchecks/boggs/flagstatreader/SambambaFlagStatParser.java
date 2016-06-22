package com.hartwig.healthchecks.boggs.flagstatreader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.hartwig.healthchecks.common.exception.EmptyFileException;

import org.jetbrains.annotations.NotNull;

public class SambambaFlagStatParser implements FlagStatParser {

    private static final int MATE_MAP_DIF_CHR_Q5_INDEX = 12;

    private static final int MATE_MAP_DIF_CHR_INDEX = 11;

    private static final int SINGELTONS_INDEX = 10;

    private static final int ITSELF_AND_MATE_INDEX = 9;

    private static final int PROPERLY_PAIRED_INDEX = 8;

    private static final int READ_2_INDEX = 7;

    private static final int READ_INDEX = 6;

    private static final int PAIRED_IN_SEQ_INDEX = 5;

    private static final int MAPPED_INDEX = 4;

    private static final int DUPLICATES_INDEX = 3;

    private static final int SUPPLEMENTARY_INDEX = 2;

    private static final int SECONDARY_INDEX = 1;

    private static final int TOTAL_INDEX = 0;

    private static final String FLAGSTATS_FILE_EMPTY_ERROR = "flagstats file empty path -> %s";

    private static final String SEPERATOR_REGEX = " ";

    @NotNull
    public FlagStatData parse(@NotNull final String filePath) throws IOException, EmptyFileException {
        final List<Double> passed = new ArrayList<>();
        final List<Double> failed = new ArrayList<>();

        Files.lines(Paths.get(filePath)).map(line -> {
            final Double qcPassed = Double.parseDouble(line.split(SEPERATOR_REGEX)[0]);
            final Double qcFailed = Double.parseDouble(line.split(SEPERATOR_REGEX)[2]);

            return new Double[] { qcPassed, qcFailed };
        }).forEach(line -> {
            passed.add(line[0]);
            failed.add(line[1]);
        });

        if (failed.isEmpty() || passed.isEmpty()) {
            throw new EmptyFileException(String.format(FLAGSTATS_FILE_EMPTY_ERROR, filePath));
        }

        final FlagStats passedFlagStats = buildFlagStatsData(passed.stream().toArray(Double[]::new));
        final FlagStats failedFlagStats = buildFlagStatsData(failed.stream().toArray(Double[]::new));

        return new FlagStatData(filePath, passedFlagStats, failedFlagStats);
    }

    @NotNull
    private FlagStats buildFlagStatsData(@NotNull final Double... data) {
        return new FlagStatsBuilder()
                .setTotal(data[TOTAL_INDEX])
                .setSecondary(data[SECONDARY_INDEX])
                .setSupplementary(data[SUPPLEMENTARY_INDEX])
                .setDuplicates(data[DUPLICATES_INDEX])
                .setMapped(data[MAPPED_INDEX])
                .setPairedInSequencing(data[PAIRED_IN_SEQ_INDEX])
                .setRead1(data[READ_INDEX])
                .setRead2(data[READ_2_INDEX])
                .setProperlyPaired(data[PROPERLY_PAIRED_INDEX])
                .setItselfAndMateMapped(data[ITSELF_AND_MATE_INDEX])
                .setSingletons(data[SINGELTONS_INDEX])
                .setMateMappedToDifferentChr(data[MATE_MAP_DIF_CHR_INDEX])
                .setMateMappedToDifferentChrMapQ5(data[MATE_MAP_DIF_CHR_Q5_INDEX])
                .build();
    }
}