package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStats;
import com.hartwig.healthchecks.boggs.model.data.PatientData;
import com.hartwig.healthchecks.boggs.model.data.SampleData;
import com.hartwig.healthchecks.boggs.model.report.MappingDataReport;
import com.hartwig.healthchecks.boggs.model.report.MappingReport;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MappingHealthChecker implements HealthChecker {

	private static Logger LOGGER = LogManager.getLogger(MappingHealthChecker.class);

	private static final double MIN_MAPPED_PERCENTAGE = 0.992;
	private static final double MIN_PROPERLY_PAIRED_PERCENTAGE = 0.99;
	private static final double MAX_SINGLETONS = 0.005;
	private static final double MAX_MATE_MAPPED_TO_DIFFERENT_CHR = 0.0001;

	private String runDirectory;

	private PatientExtractor dataExtractor;

	public MappingHealthChecker(String runDirectory, PatientExtractor dataExtractor) {
		this.runDirectory = runDirectory;
		this.dataExtractor = dataExtractor;
	}

	@NotNull
	private static String toPercentage(double percentage) {
		return (Math.round(percentage * 10000L) / 100D) + "%";
	}

	@Override
	public BaseReport runCheck() throws IOException, EmptyFileException {
		PatientData patientData = dataExtractor.extractFromRunDirectory(runDirectory);
		MappingDataReport refDataReport = checkSample(patientData.getRefSample());
		MappingDataReport tumorDataReport = checkSample(patientData.getTumorSample());
		MappingReport mappingReport = new MappingReport(CheckType.MAPPING, refDataReport, tumorDataReport);

		return mappingReport;
	}

	private MappingDataReport checkSample(@NotNull SampleData sample) {
		LOGGER.info("Checking mapping health for " + sample.getExternalId());
		MappingDataReport dataReport = new MappingDataReport(sample.getExternalId());

		for (FlagStatData flagstatData : sample.getRawMappingFlagstats()) {
			LOGGER.info(" Verifying " + flagstatData.path());
			FlagStats passed = flagstatData.qcPassedReads();

			Double mappedPercentage = passed.mapped() / passed.total();
            Double properlyPairedPercentage = passed.properlyPaired() / passed.total();
            Double singletonPercentage = passed.singletons() / passed.total();
            Double mateMappedToDifferentChrPercentage = passed.mateMappedToDifferentChr() / passed.total();

			dataReport.setMappedPercentage(toPercentage(mappedPercentage));
			dataReport.setProperlyPairedPercentage(toPercentage(properlyPairedPercentage));
			dataReport.setSingletonPercentage(toPercentage(singletonPercentage));
			dataReport.setMateMappedToDifferentChrPercentage(toPercentage(mateMappedToDifferentChrPercentage));

			if (mappedPercentage < MIN_MAPPED_PERCENTAGE) {
				LOGGER.info("  WARN: Low mapped percentage: " + toPercentage(mappedPercentage));
			} else {
				LOGGER.info("  OK: Acceptable mapped percentage: " + toPercentage(mappedPercentage));
			}

			if (properlyPairedPercentage < MIN_PROPERLY_PAIRED_PERCENTAGE) {
				LOGGER.info("  WARN: Low properly paired percentage: " + toPercentage(properlyPairedPercentage));
			} else {
				LOGGER.info("  OK: Acceptable properly paired percentage: " + toPercentage(properlyPairedPercentage));
			}

			if (singletonPercentage > MAX_SINGLETONS) {
				LOGGER.info("  WARN: High singleton percentage: " + toPercentage(singletonPercentage));
			} else {
				LOGGER.info("  OK: Acceptable singleton percentage: " + toPercentage(singletonPercentage));
			}

			if (mateMappedToDifferentChrPercentage > MAX_MATE_MAPPED_TO_DIFFERENT_CHR) {
				LOGGER.info("  WARN: High mate mapped to different chr percentage: "
						+ toPercentage(mateMappedToDifferentChrPercentage));
			} else {
				LOGGER.info("  OK: Acceptable mate mapped to different chr percentage: "
						+ toPercentage(mateMappedToDifferentChrPercentage));
			}
		}
		return dataReport;
	}
}
