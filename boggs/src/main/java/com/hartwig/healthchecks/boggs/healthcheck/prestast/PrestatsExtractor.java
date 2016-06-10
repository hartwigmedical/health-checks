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
import java.util.Map;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hartwig.healthchecks.boggs.extractor.BoggsExtractor;
import com.hartwig.healthchecks.boggs.model.report.PrestatsDataReport;
import com.hartwig.healthchecks.boggs.model.report.PrestatsReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class PrestatsExtractor extends BoggsExtractor {

	private static final long MIN_TOTAL_SQ = 85000000l;

	private static Logger LOGGER = LogManager.getLogger(PrestatsExtractor.class);

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
			String[] values = line.split(SEPERATOR_REGEX);
			String status = values[0];
			String check = values[1];
			String file = values[2];
			return new PrestatsDataReport(status, check, file);
		}).forEach(prestatsDataReport -> {
			prestatsData.addData(prestatsDataReport);
		});

		List<Path> fastqcFiles = Files.walk(new File(runDirectory).toPath())
				.filter(p -> p.getFileName().toString().contains(FASTQC_DATA_FILE_NAME)).sorted()
				.collect(toCollection(ArrayList<Path>::new));

		fastqcFiles.stream().map(path -> {
			PrestatsDataReport prestatsDataReport = null;
			try {
				Map<String, String> data = getFastqcData(path);
				String status = "PASS";
				if (Long.parseLong(data.get(TOTAL_SEQUENCES)) < MIN_TOTAL_SQ) {
					status = "FAIL";
				}
				prestatsDataReport = new PrestatsDataReport(status, TOTAL_SEQUENCES, data.get(FILENAME));

			} catch (IOException e) {
				LOGGER.error(String.format("Error occurred when reading file. Will return empty stream. Error -> %s",
						e.getMessage()));
			}
			return prestatsDataReport;
		}).forEach(prestatsDataReport -> {
			prestatsData.addData(prestatsDataReport);
		});
		return prestatsData;
	}
}
