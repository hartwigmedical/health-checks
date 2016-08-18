package com.hartwig.healthchecks.flint.extractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.path.SamplePathFinder;
import com.hartwig.healthchecks.common.io.reader.FileReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class InsertSizeMetricsExtractor extends AbstractFlintExtractor {

    private static final Logger LOGGER = LogManager.getLogger(InsertSizeMetricsExtractor.class);

    private static final String RUN_QCSTATS_DIR = "QCStats";
    private static final String RUN_QCSTATS_DIR_SUFFIX = "_dedup";
    private static final String INSERT_SIZE_METRICS_EXTENSION = ".insert_size_metrics";

    @NotNull
    private final RunContext runContext;

    public InsertSizeMetricsExtractor(@NotNull final RunContext runContext) {
        this.runContext = runContext;
    }

    @NotNull
    @Override
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<BaseDataReport> referenceSample = getSampleData(runContext.runDirectory(), runContext.refSample());
        final List<BaseDataReport> tumorSample = getSampleData(runContext.runDirectory(), runContext.tumorSample());

        return new SampleReport(CheckType.INSERT_SIZE, referenceSample, tumorSample);
    }

    @NotNull
    private static List<BaseDataReport> getSampleData(@NotNull final String runDirectory,
            @NotNull final String sampleId) throws IOException, HealthChecksException {
        final String basePath = getBasePathForSample(runDirectory, sampleId);
        Path insertSizeMetricsPath = SamplePathFinder.build().findPath(basePath, sampleId,
                INSERT_SIZE_METRICS_EXTENSION);
        final List<String> lines = FileReader.build().readLines(insertSizeMetricsPath);

        final BaseDataReport medianReport = getValue(lines, sampleId,
                InsertSizeMetricsCheck.MAPPING_MEDIAN_INSERT_SIZE);
        final BaseDataReport width70PerReport = getValue(lines, sampleId,
                InsertSizeMetricsCheck.MAPPING_WIDTH_OF_70_PERCENT);
        return Arrays.asList(medianReport, width70PerReport);
    }

    @NotNull
    private static String getBasePathForSample(@NotNull final String runDirectory, @NotNull final String sampleId) {
        return runDirectory + File.separator + RUN_QCSTATS_DIR + File.separator + sampleId + RUN_QCSTATS_DIR_SUFFIX;
    }

    @NotNull
    private static BaseDataReport getValue(@NotNull final List<String> lines, @NotNull final String sampleId,
            @NotNull final InsertSizeMetricsCheck check) throws LineNotFoundException {
        final String value = getValueFromLine(lines, "DUMMY", check.getFieldName(), check.getColumnIndex());
        final BaseDataReport baseDataReport = new BaseDataReport(sampleId, check.toString(), value);
        baseDataReport.log(LOGGER);
        return baseDataReport;
    }
}
