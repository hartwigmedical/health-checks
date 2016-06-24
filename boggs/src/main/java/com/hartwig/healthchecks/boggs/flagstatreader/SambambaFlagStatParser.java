package com.hartwig.healthchecks.boggs.flagstatreader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boggs.healthcheck.mapping.FlagStatsType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;

public class SambambaFlagStatParser implements FlagStatParser {

    private static final Logger LOGGER = LogManager.getLogger(SambambaFlagStatParser.class);

    private static final String EMPTY_FILE_ERROR = "flagstats file empty path -> %s";

    private static final String SEPERATOR_REGEX = " ";

    private static final int ZERO = 0;

    private static final int ONE = 1;

    private static final int TWO = 2;

    private static final int THREE = 3;

    @Override
    @NotNull
    public FlagStatData parse(@NotNull final String filePath) throws IOException, EmptyFileException {
        final int[] index = { ZERO };
        final List<FlagStats> passedStats = new ArrayList<>();
        final List<FlagStats> failedStats = new ArrayList<>();

        Files.lines(Paths.get(filePath)).map(line -> {
            final String qcPassed = line.split(SEPERATOR_REGEX)[ZERO];
            final String qcFailed = line.split(SEPERATOR_REGEX)[TWO];

            final String firstWord = line.split(SEPERATOR_REGEX)[THREE];
            final int firstWordIndex = line.indexOf(firstWord);
            final String checkName = line.substring(firstWordIndex, line.length());

            return new String[] { qcPassed, qcFailed, checkName };
        }).forEach(array -> {
            final double passedValue = Double.parseDouble(array[ZERO]);
            final double failedValue = Double.parseDouble(array[ONE]);
            final Optional<FlagStatsType> statsTypeOpt = FlagStatsType.getByIndex(index[ZERO]);
            final FlagStats passed = new FlagStats(statsTypeOpt.get(), array[TWO], passedValue);
            final FlagStats failed = new FlagStats(statsTypeOpt.get(), array[TWO], failedValue);
            passedStats.add(passed);
            failedStats.add(failed);
            index[ZERO] += ONE;
        });

        if (passedStats.isEmpty() || failedStats.isEmpty()) {
            throw new EmptyFileException(String.format(EMPTY_FILE_ERROR, filePath));
        }

        return new FlagStatData(filePath, passedStats, failedStats);
    }
}