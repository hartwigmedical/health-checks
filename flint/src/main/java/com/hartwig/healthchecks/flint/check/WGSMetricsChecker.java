package com.hartwig.healthchecks.flint.check;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.google.common.collect.Lists;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.path.PathPrefixSuffixFinder;
import com.hartwig.healthchecks.common.io.reader.FileReader;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.MultiValueResult;
import com.hartwig.healthchecks.common.result.PatientResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("WeakerAccess")
@ResourceWrapper(type = CheckType.WGS_METRICS)
public class WGSMetricsChecker implements HealthChecker {

    private static final Logger LOGGER = LogManager.getLogger(WGSMetricsChecker.class);

    // KODU: metrics files stores in {run}/QCStats/{sample}_dedup/{sample}_dedup_WGSMetrics.txt
    private static final String METRICS_BASE_DIRECTORY = "QCStats";
    private static final String METRICS_SUB_DIRECTORY_SUFFIX = "_dedup";
    private static final String WGS_METRICS_EXTENSION = "_WGSMetrics.txt";
    private static final String VALUE_SEPARATOR = "\t";

    public WGSMetricsChecker() {
    }

    @NotNull
    @Override
    public CheckType checkType() {
        return CheckType.WGS_METRICS;
    }

    @Override
    @NotNull
    public BaseResult run(@NotNull final RunContext runContext) throws IOException, HealthChecksException {
        final List<HealthCheck> referenceSample = getSampleData(runContext.runDirectory(), runContext.refSample());
        final List<HealthCheck> tumorSample = getSampleData(runContext.runDirectory(), runContext.tumorSample());
        return new PatientResult(checkType(), referenceSample, tumorSample);
    }

    @NotNull
    @Override
    public BaseResult errorResult(@NotNull final RunContext runContext) {
        return new MultiValueResult(checkType(), Lists.newArrayList());
    }

    @NotNull
    private static List<HealthCheck> getSampleData(@NotNull final String runDirectory, @NotNull final String sampleId)
            throws IOException, HealthChecksException {
        final String basePath = getBasePathForSample(runDirectory, sampleId);
        Path wgsMetricsPath = PathPrefixSuffixFinder.build().findPath(basePath, sampleId, WGS_METRICS_EXTENSION);
        final List<String> lines = FileReader.build().readLines(wgsMetricsPath);

        final HealthCheck coverageMean = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_MEAN);
        final HealthCheck coverageMedian = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_MEDIAN);
        final HealthCheck coverageSD = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_SD);
        final HealthCheck coverageBaseQ = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_PCT_EXC_BASEQ);
        final HealthCheck coverageDupe = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_PCT_EXC_DUPE);
        final HealthCheck coverageMapQ = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_PCT_EXC_MAPQ);
        final HealthCheck coverageOverlap = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_PCT_EXC_OVERLAP);
        final HealthCheck coverageTotal = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_PCT_EXC_TOTAL);
        final HealthCheck coverageUnpaired = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_PCT_EXC_UNPAIRED);

        return Arrays.asList(coverageMean, coverageMedian, coverageSD, coverageBaseQ, coverageDupe, coverageMapQ,
                coverageOverlap, coverageTotal, coverageUnpaired);
    }

    @NotNull
    private static String getBasePathForSample(@NotNull final String runDirectory, @NotNull final String sampleId) {
        return runDirectory + File.separator + METRICS_BASE_DIRECTORY + File.separator + sampleId
                + METRICS_SUB_DIRECTORY_SUFFIX;
    }

    @NotNull
    private static HealthCheck getValue(@NotNull final String filePath, @NotNull final List<String> lines,
            @NotNull final String sampleId, @NotNull final WGSMetricsCheck check) throws LineNotFoundException {
        final String value = getValueFromLine(filePath, lines, check.getFieldName(), check.getColumnIndex());
        final HealthCheck healthCheck = new HealthCheck(sampleId, check.toString(), value);
        healthCheck.log(LOGGER);
        return healthCheck;
    }

    @NotNull
    private static String getValueFromLine(@NotNull final String filePath, @NotNull final List<String> lines,
            @NotNull final String filter, final int fieldIndex) throws LineNotFoundException {
        final int index = findLineIndex(filePath, lines, filter);
        final String line = lines.get(index + 1);
        final String[] lineValues = line.split(VALUE_SEPARATOR);
        return lineValues[fieldIndex];
    }

    private static int findLineIndex(@NotNull final String filePath, @NotNull final List<String> lines,
            @NotNull final String filter) throws LineNotFoundException {
        final Optional<Integer> lineNumbers = IntStream.range(0, lines.size()).filter(
                index -> lines.get(index).contains(filter)).mapToObj(index -> index).findFirst();
        if (!lineNumbers.isPresent()) {
            throw new LineNotFoundException(filePath, filter);
        }
        return lineNumbers.get();
    }
}
