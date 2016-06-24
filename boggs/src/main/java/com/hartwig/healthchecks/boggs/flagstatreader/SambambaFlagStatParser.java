package com.hartwig.healthchecks.boggs.flagstatreader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hartwig.healthchecks.boggs.healthcheck.mapping.FlagStatsType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;

import org.jetbrains.annotations.NotNull;

public class SambambaFlagStatParser implements FlagStatParser {

    private static final String EMPTY_FILE_ERROR = "flagstats file empty path -> %s";

    private static final String SEPERATOR_REGEX = " ";

    private static final int ZERO = 0;

    private static final int ONE = 1;

    private static final int TWO = 2;

    private static final int THREE = 3;

    @NotNull
    public FlagStatData parse(@NotNull final String filePath) throws IOException, EmptyFileException {
        final Map<String, Double> passed = new HashMap<>();
        final Map<String, Double> failed = new HashMap<>();

        Files.lines(Paths.get(filePath)).map(line -> {
            final String qcPassed = line.split(SEPERATOR_REGEX)[ZERO];
            final String qcFailed = line.split(SEPERATOR_REGEX)[TWO];

            final String firstWord = line.split(SEPERATOR_REGEX)[THREE];
            final int firstWordIndex = line.indexOf(firstWord);
            final String checkName = line.substring(firstWordIndex, line.length());

            return new String[] {qcPassed, qcFailed, checkName};
        }).forEach(array -> {
            final double passedValue = Double.parseDouble(array[ZERO]);
            final double failedValue = Double.parseDouble(array[ONE]);

            passed.put(array[TWO], passedValue);
            failed.put(array[TWO], failedValue);
        });

        if (failed.isEmpty() || passed.isEmpty()) {
            throw new EmptyFileException(String.format(EMPTY_FILE_ERROR, filePath));
        }

        final List<FlagStats> passedStats = buildFlagStatsData(passed);
        final List<FlagStats> failedStats = buildFlagStatsData(failed);

        return new FlagStatData(filePath, passedStats, failedStats);
    }

    @NotNull
    private List<FlagStats> buildFlagStatsData(@NotNull final Map<String, Double> data) {
        final List<FlagStats> failedStats = new ArrayList<>();
        final int [] index = {ZERO};
        data.forEach((checkName, value) -> {
            final Optional<FlagStatsType> statsTypeOpt = FlagStatsType.getByIndex(index[ZERO]);
            final FlagStatsType flagStatsType = statsTypeOpt.get();

            final FlagStats flagStats = new FlagStats(flagStatsType, checkName, value);
            failedStats.add(flagStats);

            index[ZERO] += ONE;
        });
        return failedStats;
    }
}