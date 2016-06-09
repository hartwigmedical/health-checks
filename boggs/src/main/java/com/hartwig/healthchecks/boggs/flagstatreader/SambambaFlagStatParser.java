package com.hartwig.healthchecks.boggs.flagstatreader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.EmptyFileException;

public class SambambaFlagStatParser implements FlagStatParser {
	private static final String FLAGSTATS_FILE_EMPTY_ERROR = "flagstats file empty path -> %s";
	private static final String SEPERATOR_REGEX = " ";

	@NotNull
	public FlagStatData parse(@NotNull String filePath) throws IOException, EmptyFileException {
		List<Long> passed = new ArrayList<>();
		List<Long> failed = new ArrayList<>();

		Files.lines(Paths.get(filePath)).map(line -> {
			Long qcPassed = Long.parseLong(line.split(SEPERATOR_REGEX)[0]);
			Long qcFailed = Long.parseLong(line.split(SEPERATOR_REGEX)[2]);
			return new Long[] { qcPassed, qcFailed };
		}).forEach(line -> {
			passed.add(line[0]);
			failed.add(line[1]);
		});

		if (failed.isEmpty() || passed.isEmpty()) {
			throw new EmptyFileException(String.format(FLAGSTATS_FILE_EMPTY_ERROR, filePath));
		}

		FlagStats passedFlagStats = buildFlagStatsData(passed.stream().toArray(Long[]::new));
		FlagStats failedFlagStats = buildFlagStatsData(failed.stream().toArray(Long[]::new));
		return new FlagStatData(filePath, passedFlagStats, failedFlagStats);
	}

	private FlagStats buildFlagStatsData(Long[] data) {
		return new FlagStatsBuilder().setTotal(data[0]).setSecondary(data[1]).setSupplementary(data[2])
				.setDuplicates(data[3]).setMapped(data[4]).setPairedInSequencing(data[5]).setRead1(data[6])
				.setRead2(data[7]).setProperlyPaired(data[8]).setItselfAndMateMapped(data[9]).setSingletons(data[10])
				.setMateMappedToDifferentChr(data[11]).setMateMappedToDifferentChrMapQ5(data[12]).build();
	}
}