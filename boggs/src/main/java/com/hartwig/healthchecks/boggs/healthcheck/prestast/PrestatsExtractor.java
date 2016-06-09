package com.hartwig.healthchecks.boggs.healthcheck.prestast;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hartwig.healthchecks.boggs.model.report.PrestatsDataReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class PrestatsExtractor {

	private static Logger LOGGER = LogManager.getLogger(PrestatsExtractor.class);

	private static final String SEPERATOR_REGEX = "\t";
	private static String DATA_FILE_NAME = "summary.txt";

	public PrestatsReport extractFromRunDirectory(String runDirectory) throws IOException {
		List<Path> summaryFiles = Files.walk(new File(runDirectory).toPath())
				.filter(p -> p.getFileName().toString().startsWith(DATA_FILE_NAME)).sorted()
				.collect(toCollection(ArrayList<Path>::new));

		PrestatsReport prestatsData = new PrestatsReport(CheckType.PRESTATS);

		summaryFiles.stream().map(path -> {
			Stream<String> fileLines = Stream.empty();
			try {
				fileLines = Files.lines(path);
			} catch (IOException e) {
				LOGGER.error(String.format("Error occurred when reading file. Will return empty stream. Error -> %s",
						e.getMessage()));
			}
			return fileLines.collect(toList());
		}).flatMap(Collection::stream).map(line -> {
			String status = line.split(SEPERATOR_REGEX)[0];
			String check = line.split(SEPERATOR_REGEX)[1];
			String file = line.split(SEPERATOR_REGEX)[2];
			return new PrestatsDataReport(status, check, file);
		}).forEach(prestatsDataReport -> {
			prestatsData.addData(prestatsDataReport);
		});
		return prestatsData;
	}
}
