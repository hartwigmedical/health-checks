package com.hartwig.healthchecks.flint.extractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

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

public class InsertSizeMetricsExtractor implements HealthChecker {

    private static final Logger LOGGER = LogManager.getLogger(InsertSizeMetricsExtractor.class);

    // KODU: metrics files stores in {run}/QCStats/{sample}_dedup/{sample}<>.insert_size_metrics
    private static final String METRICS_BASE_DIRECTORY = "QCStats";
    private static final String METRICS_SUB_DIRECTORY_SUFFIX = "_dedup";
    private static final String INSERT_SIZE_METRICS_EXTENSION = ".insert_size_metrics";
    private static final String VALUE_SEPARATOR = "\t";

    @NotNull
    private final RunContext runContext;

    public InsertSizeMetricsExtractor(@NotNull final RunContext runContext) {
        this.runContext = runContext;
    }

    @NotNull
    @Override
    public BaseResult run() throws IOException, HealthChecksException {
        final List<HealthCheck> referenceSample = getSampleData(runContext.runDirectory(), runContext.refSample());
        final List<HealthCheck> tumorSample = getSampleData(runContext.runDirectory(), runContext.tumorSample());

        return new PatientResult(CheckType.INSERT_SIZE, referenceSample, tumorSample);
    }

    @NotNull
    private static List<HealthCheck> getSampleData(@NotNull final String runDirectory, @NotNull final String sampleId)
            throws IOException, HealthChecksException {
        final String basePath = getBasePathForSample(runDirectory, sampleId);
        final Path insertSizeMetricsPath = PathPrefixSuffixFinder.build().findPath(basePath, sampleId,
                INSERT_SIZE_METRICS_EXTENSION);
        final List<String> lines = FileReader.build().readLines(insertSizeMetricsPath);

        final HealthCheck medianReport = getValue(insertSizeMetricsPath.toString(), lines, sampleId,
                InsertSizeMetricsCheck.MAPPING_MEDIAN_INSERT_SIZE);
        final HealthCheck width70PerReport = getValue(insertSizeMetricsPath.toString(), lines, sampleId,
                InsertSizeMetricsCheck.MAPPING_WIDTH_OF_70_PERCENT);
        return Arrays.asList(medianReport, width70PerReport);
    }

    @NotNull
    private static String getBasePathForSample(@NotNull final String runDirectory, @NotNull final String sampleId) {
        return runDirectory + File.separator + METRICS_BASE_DIRECTORY + File.separator + sampleId
                + METRICS_SUB_DIRECTORY_SUFFIX;
    }

    @NotNull
    private static HealthCheck getValue(@NotNull final String filePath, @NotNull final List<String> lines,
            @NotNull final String sampleId, @NotNull final InsertSizeMetricsCheck check) throws LineNotFoundException {
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
