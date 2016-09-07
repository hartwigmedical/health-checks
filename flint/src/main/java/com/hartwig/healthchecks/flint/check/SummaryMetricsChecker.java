package com.hartwig.healthchecks.flint.check;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.path.PathPrefixSuffixFinder;
import com.hartwig.healthchecks.common.io.reader.FileReader;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.PatientResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class SummaryMetricsChecker implements HealthChecker {

    private static final Logger LOGGER = LogManager.getLogger(SummaryMetricsChecker.class);

    // KODU: metrics files stores in {run}/QCStats/{sample}_dedup/{sample}<>.alignment_summary_metrics
    private static final String METRICS_BASE_DIRECTORY = "QCStats";
    private static final String METRICS_SUB_DIRECTORY_SUFFIX = "_dedup";
    private static final String ALIGNMENT_SUMMARY_METRICS_EXTENSION = ".alignment_summary_metrics";

    private static final String PICARD_CATEGORY_TO_READ = "PAIR";
    private static final String VALUE_SEPARATOR = "\t";

    @NotNull
    private final RunContext runContext;

    public SummaryMetricsChecker(@NotNull final RunContext runContext) {
        this.runContext = runContext;
    }

    @NotNull
    @Override
    public BaseResult run() throws IOException, HealthChecksException {
        final List<HealthCheck> referenceSample = getSampleData(runContext.runDirectory(), runContext.refSample());
        final List<HealthCheck> tumorSample = getSampleData(runContext.runDirectory(), runContext.tumorSample());
        return new PatientResult(CheckType.SUMMARY_METRICS, referenceSample, tumorSample);
    }

    @NotNull
    private static List<HealthCheck> getSampleData(@NotNull final String runDirectory, @NotNull final String sampleId)
            throws IOException, HealthChecksException {
        final String basePath = getBasePathForSample(runDirectory, sampleId);
        Path alignmentSummaryMetricsPath = PathPrefixSuffixFinder.build().findPath(basePath, sampleId,
                ALIGNMENT_SUMMARY_METRICS_EXTENSION);
        final List<String> lines = FileReader.build().readLines(alignmentSummaryMetricsPath);

        final Optional<String> searchedLine = lines.stream().filter(
                fileLine -> fileLine.startsWith(PICARD_CATEGORY_TO_READ)).findFirst();
        if (!searchedLine.isPresent()) {
            throw new LineNotFoundException(alignmentSummaryMetricsPath.toString(), PICARD_CATEGORY_TO_READ);
        }

        final HealthCheck pfIndelRate = getValue(searchedLine.get(), sampleId,
                SummaryMetricsCheck.MAPPING_PF_INDEL_RATE);
        final HealthCheck pctAdapter = getValue(searchedLine.get(), sampleId, SummaryMetricsCheck.MAPPING_PCT_ADAPTER);
        final HealthCheck pctChimeras = getValue(searchedLine.get(), sampleId,
                SummaryMetricsCheck.MAPPING_PCT_CHIMERA);
        final HealthCheck pfMisMatch = getValue(searchedLine.get(), sampleId,
                SummaryMetricsCheck.MAPPING_PF_MISMATCH_RATE);
        final HealthCheck strandBalance = getValue(searchedLine.get(), sampleId,
                SummaryMetricsCheck.MAPPING_STRAND_BALANCE);
        return Arrays.asList(pfIndelRate, pctAdapter, pctChimeras, pfMisMatch, strandBalance);
    }

    @NotNull
    private static String getBasePathForSample(@NotNull final String runDirectory, @NotNull final String sampleId) {
        return runDirectory + File.separator + METRICS_BASE_DIRECTORY + File.separator + sampleId
                + METRICS_SUB_DIRECTORY_SUFFIX;
    }

    @NotNull
    private static HealthCheck getValue(@NotNull final String line, @NotNull final String sampleId,
            @NotNull final SummaryMetricsCheck check) throws LineNotFoundException {
        final String value = line.split(VALUE_SEPARATOR)[check.getIndex()];
        final HealthCheck healthCheck = new HealthCheck(sampleId, check.toString(), value);
        healthCheck.log(LOGGER);
        return healthCheck;
    }
}
