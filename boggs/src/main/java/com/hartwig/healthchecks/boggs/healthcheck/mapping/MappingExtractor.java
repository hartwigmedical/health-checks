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
	private static final String EMPTY_FILES_ERROR = "Found empty Summary files and/or fastqc_data under path -> %s";

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
		if (!sampleFile.isPresent()) {
			throw new FileNotFoundException();
		}
		String externalId = sampleFile.get().getFileName().toString();
		String totalSequences = getTotalSequences(sampleFile.get());
		MappingDataReport mappingDataReport = getFlagstatsData(sampleFile.get(), totalSequences);
		return new MappingReport(CheckType.MAPPING, externalId, totalSequences, mappingDataReport);
	}

	private MappingDataReport getFlagstatsData(Path path, String totalSequences)
			throws IOException, EmptyFileException {
		Optional<Path> filePath = Files.walk(new File(path + File.separator + MAPPING + File.separator).toPath())
				.filter(p -> p.getFileName().toString().endsWith(FLAGSTAT_SUFFIX)
						&& p.getFileName().toString().contains(REALIGN))
				.findFirst();
		if (!filePath.isPresent()) {
			throw new FileNotFoundException();
		}
		FlagStatData flagstatData = flagstatParser.parse(filePath.get().toString());
		if (flagstatData == null) {
			throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, path.toString()));
		}
		FlagStats passed = flagstatData.qcPassedReads();

		Double mappedPercentage = toPercentage(passed.mapped() / passed.total());
		Double properlyPairedPercentage = toPercentage(passed.properlyPaired() / passed.mapped());
		Double singletonPercentage = passed.singletons();
		Double mateMappedToDifferentChrPercentage = passed.mateMappedToDifferentChr();
		Double proportionOfDuplicateRead = toPercentage(passed.duplicates() / passed.total());
		boolean isAllReadsPresent = passed.total() == ((Double.parseDouble(totalSequences) * 2) + passed.secondary());
		return new MappingDataReport(mappedPercentage, properlyPairedPercentage, singletonPercentage,
				mateMappedToDifferentChrPercentage, proportionOfDuplicateRead, isAllReadsPresent);
	}

	@NotNull
	private static double toPercentage(double percentage) {
		return (Math.round(percentage * 10000L) / 100D);
	}
}
