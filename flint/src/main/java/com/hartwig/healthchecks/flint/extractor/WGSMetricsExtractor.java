package com.hartwig.healthchecks.flint.extractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.path.SamplePathFinder;
import com.hartwig.healthchecks.common.io.reader.FileReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class WGSMetricsExtractor implements DataExtractor {

    private static final Logger LOGGER = LogManager.getLogger(WGSMetricsExtractor.class);

    // KODU: metrics files stores in {run}/QCStats/{sample}_dedup/{sample}_dedup_WGSMetrics.txt
    private static final String METRICS_BASE_DIRECTORY = "QCStats";
    private static final String METRICS_SUB_DIRECTORY_SUFFIX = "_dedup";
    private static final String WGS_METRICS_EXTENSION = "_WGSMetrics.txt";
    private static final String VALUE_SEPARATOR = "\t";

    @NotNull
    private final RunContext runContext;

    public WGSMetricsExtractor(@NotNull final RunContext runContext) {
        this.runContext = runContext;
    }

    @NotNull
    @Override
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<BaseDataReport> referenceSample = getSampleData(runContext.runDirectory(), runContext.refSample());
        final List<BaseDataReport> tumorSample = getSampleData(runContext.runDirectory(), runContext.tumorSample());
        return new SampleReport(CheckType.COVERAGE, referenceSample, tumorSample);
    }

    @NotNull
    private static List<BaseDataReport> getSampleData(@NotNull final String runDirectory,
            @NotNull final String sampleId) throws IOException, HealthChecksException {
        final String basePath = getBasePathForSample(runDirectory, sampleId);
        Path wgsMetricsPath = SamplePathFinder.build().findPath(basePath, sampleId, WGS_METRICS_EXTENSION);
        final List<String> lines = FileReader.build().readLines(wgsMetricsPath);

        final BaseDataReport coverageMean = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_MEAN);
        final BaseDataReport coverageMedian = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_MEDIAN);
        final BaseDataReport coverageSD = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_SD);
        final BaseDataReport coverageBaseQ = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_PCT_EXC_BASEQ);
        final BaseDataReport coverageDupe = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_PCT_EXC_DUPE);
        final BaseDataReport coverageMapQ = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_PCT_EXC_MAPQ);
        final BaseDataReport coverageOverlap = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_PCT_EXC_OVERLAP);
        final BaseDataReport coverageTotal = getValue(wgsMetricsPath.toString(), lines, sampleId,
                WGSMetricsCheck.COVERAGE_PCT_EXC_TOTAL);
        final BaseDataReport coverageUnpaired = getValue(wgsMetricsPath.toString(), lines, sampleId,
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
    private static BaseDataReport getValue(@NotNull final String filePath, @NotNull final List<String> lines,
            @NotNull final String sampleId, @NotNull final WGSMetricsCheck check) throws LineNotFoundException {
        final String value = getValueFromLine(filePath, lines, check.getFieldName(), check.getColumnIndex());
        final BaseDataReport baseDataReport = new BaseDataReport(sampleId, check.toString(), value);
        baseDataReport.log(LOGGER);
        return baseDataReport;
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
