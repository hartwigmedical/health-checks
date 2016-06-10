package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import java.io.File;
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
	private static final String REALIGN = "realign";
	private static final String SAMPLE_PREFIX = "CPCT";
	private static final String REF_SAMPLE_SUFFIX = "R";
	private static final String FLAGSTAT_SUFFIX = ".flagstat";

	@NotNull
	private final FlagStatParser flagstatParser;

	public MappingExtractor(@NotNull FlagStatParser flagstatParser) {
		this.flagstatParser = flagstatParser;
	}

	public MappingReport extractFromRunDirectory(String runDirectory) throws IOException, EmptyFileException {

		Optional<Path> sampleFile = Files.walk(new File(runDirectory).toPath())
				.filter(p -> p.getFileName().toString().startsWith(SAMPLE_PREFIX)
						&& p.getFileName().toString().endsWith(REF_SAMPLE_SUFFIX))
				.findFirst();

		assert sampleFile.isPresent();

		String externalId = sampleFile.get().getFileName().toString();
		MappingDataReport mappingDataReport = getFlagstatsData(sampleFile.get());
		String totalSequences = getTotalSequences(sampleFile.get());
		return new MappingReport(CheckType.MAPPING, externalId, totalSequences, mappingDataReport);
	}

	private MappingDataReport getFlagstatsData(Path path) throws IOException, EmptyFileException {
		Optional<Path> filePath = Files.walk(new File(path + File.separator + MAPPING + File.separator).toPath())
				.filter(p -> p.getFileName().toString().endsWith(FLAGSTAT_SUFFIX)
						&& p.getFileName().toString().contains(REALIGN))
				.findFirst();
		assert filePath.isPresent();

		FlagStatData flagstatData = flagstatParser.parse(filePath.get().toString());
		FlagStats passed = flagstatData.qcPassedReads();

		Double mappedPercentage = toPercentage(passed.mapped() / passed.total());
		Double properlyPairedPercentage = toPercentage(passed.properlyPaired() / passed.total());
		Double singletonPercentage = toPercentage(passed.singletons() / passed.total());
		Double mateMappedToDifferentChrPercentage = toPercentage(passed.mateMappedToDifferentChr() / passed.total());
		Double proportionOfDuplicateRead = toPercentage(passed.duplicates() / passed.total());
		return new MappingDataReport(mappedPercentage, properlyPairedPercentage, singletonPercentage,
				mateMappedToDifferentChrPercentage, proportionOfDuplicateRead);
	}

	@NotNull
	private static double toPercentage(double percentage) {
		return (Math.round(percentage * 10000L) / 100D);
	}
}
