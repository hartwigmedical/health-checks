package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

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

	private final String runDirectory;

	private final MappingExtractor dataExtractor;

	public MappingHealthChecker(@NotNull final String runDirectory,@NotNull final MappingExtractor dataExtractor) {
		this.runDirectory = runDirectory;
		this.dataExtractor = dataExtractor;
	}

	@Override
	public BaseReport runCheck() throws IOException, EmptyFileException {
		MappingReport mappingReport = dataExtractor.extractFromRunDirectory(runDirectory);
		MappingDataReport mappingDataReport = mappingReport.getMappingDataReport();

		LOGGER.info("Checking mapping health for " + mappingReport.getExternalId());
		String allReadsMessage = "OK : All Reads are present";

		if (!mappingDataReport.isAllReadsPresent()) {
			allReadsMessage = "WARN : Not All Reads are present";
		}
		LOGGER.info(allReadsMessage);

		String mappedMessage = "OK: Acceptable mapped percentage: " + mappingDataReport.getMappedPercentage();
		if (mappingDataReport.getMappedPercentage() < MIN_MAPPED_PERCENTAGE) {
			mappedMessage = "WARN: Low mapped percentage: " + mappingDataReport.getMappedPercentage();
		}
		LOGGER.info(mappedMessage);

		String properlyPairedMessage = "OK: Acceptable properly paired percentage: "
				+ mappingDataReport.getProperlyPairedPercentage();
		if (mappingDataReport.getProperlyPairedPercentage() < MIN_PROPERLY_PAIRED_PERCENTAGE) {
			properlyPairedMessage = "WARN: Low properly paired percentage: "
					+ mappingDataReport.getProperlyPairedPercentage();
		}
		LOGGER.info(properlyPairedMessage);

		String singletonMessage = "OK: Acceptable singleton percentage: " + mappingDataReport.getSingletonPercentage();
		if (mappingDataReport.getSingletonPercentage() > MAX_SINGLETONS) {
			singletonMessage = "WARN: High singleton percentage: " + mappingDataReport.getSingletonPercentage();
		}
		LOGGER.info(singletonMessage);

		String mateMappedMessage = "OK: Acceptable mate mapped to different chr percentage: "
				+ mappingDataReport.getMateMappedToDifferentChrPercentage();
		if (mappingDataReport.getMateMappedToDifferentChrPercentage() > MAX_MATE_MAPPED_TO_DIFFERENT_CHR) {
			mateMappedMessage = "WARN: High mate mapped to different chr percentage: "
					+ mappingDataReport.getMateMappedToDifferentChrPercentage();
		}
		LOGGER.info(mateMappedMessage);

		String proportionPercentageMessage = "OK: Acceptable proportion of Duplication percentage: "
				+ mappingDataReport.getProportionOfDuplicateRead();
		if (mappingDataReport.getProportionOfDuplicateRead() > MAX_MATE_MAPPED_TO_DIFFERENT_CHR) {
			proportionPercentageMessage = "WARN: High proportion of Duplication percentage: "
					+ mappingDataReport.getProportionOfDuplicateRead();
		}
		LOGGER.info(proportionPercentageMessage);

		return mappingReport;
	}
}
