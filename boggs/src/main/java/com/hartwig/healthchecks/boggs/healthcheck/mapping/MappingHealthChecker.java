package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hartwig.healthchecks.boggs.model.report.MappingDataReport;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.BaseReport;

public class MappingHealthChecker implements HealthChecker {

	private static Logger LOGGER = LogManager.getLogger(MappingHealthChecker.class);

	private static final double MIN_MAPPED_PERCENTAGE = 99.2d;
	private static final double MIN_PROPERLY_PAIRED_PERCENTAGE = 99.0d;
	private static final double MAX_SINGLETONS = 0.5d;
	private static final double MAX_MATE_MAPPED_TO_DIFFERENT_CHR = 0.01;

	private String runDirectory;

	private MappingExtractor dataExtractor;

	public MappingHealthChecker(String runDirectory, MappingExtractor dataExtractor) {
		this.runDirectory = runDirectory;
		this.dataExtractor = dataExtractor;
	}

	@Override
	public BaseReport runCheck() throws IOException, EmptyFileException {
		MappingReport mappingReport = dataExtractor.extractFromRunDirectory(runDirectory);
		MappingDataReport mappingDataReport = mappingReport.getMappingDataReport();

		LOGGER.info("Checking mapping health for " + mappingReport.getExternalId());
		if (mappingDataReport.getMappedPercentage() < MIN_MAPPED_PERCENTAGE) {
			LOGGER.info("WARN: Low mapped percentage: " + mappingDataReport.getMappedPercentage());
		} else {
			LOGGER.info("OK: Acceptable mapped percentage: " + mappingDataReport.getMappedPercentage());
		}

		if (mappingDataReport.getProperlyPairedPercentage() < MIN_PROPERLY_PAIRED_PERCENTAGE) {
			LOGGER.info("WARN: Low properly paired percentage: " + mappingDataReport.getProperlyPairedPercentage());
		} else {
			LOGGER.info(
					"OK: Acceptable properly paired percentage: " + mappingDataReport.getProperlyPairedPercentage());
		}

		if (mappingDataReport.getSingletonPercentage() > MAX_SINGLETONS) {
			LOGGER.info("WARN: High singleton percentage: " + mappingDataReport.getSingletonPercentage());
		} else {
			LOGGER.info("OK: Acceptable singleton percentage: " + mappingDataReport.getSingletonPercentage());
		}

		if (mappingDataReport.getMateMappedToDifferentChrPercentage() > MAX_MATE_MAPPED_TO_DIFFERENT_CHR) {
			LOGGER.info("WARN: High mate mapped to different chr percentage: "
					+ mappingDataReport.getMateMappedToDifferentChrPercentage());
		} else {
			LOGGER.info("OK: Acceptable mate mapped to different chr percentage: "
					+ mappingDataReport.getMateMappedToDifferentChrPercentage());
		}

		if (mappingDataReport.getProportionOfDuplicateRead() > MAX_MATE_MAPPED_TO_DIFFERENT_CHR) {
			LOGGER.info("WARN: High proportion of Duplication percentage: "
					+ mappingDataReport.getProportionOfDuplicateRead());
		} else {
			LOGGER.info("OK: Acceptable proportion of Duplication percentage: "
					+ mappingDataReport.getProportionOfDuplicateRead());
		}

		return mappingReport;
	}
}
