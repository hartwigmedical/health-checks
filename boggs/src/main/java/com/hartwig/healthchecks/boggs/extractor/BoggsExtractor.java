package com.hartwig.healthchecks.boggs.extractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BoggsExtractor {
	protected static final String FILENAME = "Filename";
	protected static final String SEPERATOR_REGEX = "\t";
	protected static final String TOTAL_SEQUENCES = "Total Sequences";
	protected static final String MAPPING = "mapping";
	protected static final String FASTQC_DATA_FILE_NAME = "fastqc_data.txt";

	protected String getTotalSequences(Path path) throws IOException {
		Map<String, String> data = getFastqcData(path);
		return data.get(TOTAL_SEQUENCES);
	}

	protected Map<String, String> getFastqcData(Path path) throws IOException {
		Optional<Path> fastqcDataPath = Files.walk(path)
				.filter(p -> p.getFileName().toString().startsWith(FASTQC_DATA_FILE_NAME)).findFirst();
		assert fastqcDataPath.isPresent();
		return Files.lines(Paths.get(fastqcDataPath.get().toString()))
				.filter(line -> line.contains(TOTAL_SEQUENCES) || line.contains(FILENAME)).map(line -> {
					return line.split(SEPERATOR_REGEX);
				}).collect(Collectors.toMap(values -> values[0], values -> values[1]));
	}
}
