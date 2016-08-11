package com.hartwig.healthchecks.flint.extractor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.reader.FileReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class InsertSizeMetricsExtractor extends AbstractFlintExtractor {

    private static final Logger LOGGER = LogManager.getLogger(InsertSizeMetricsExtractor.class);

    @NotNull
    private final RunContext runContext;
    @NotNull
    private final FileReader reader;

    public InsertSizeMetricsExtractor(@NotNull final RunContext runContext) {
        this(runContext, FileReader.build());
    }

    @VisibleForTesting
    InsertSizeMetricsExtractor(@NotNull final RunContext runContext, @NotNull final FileReader reader) {
        super();
        this.runContext = runContext;
        this.reader = reader;
    }

    @NotNull
    @Override
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<BaseDataReport> referenceSample = getSampleData(runContext.getRefSample(),
                runContext.getRefSampleInsertSizeMetricsPath());
        final List<BaseDataReport> tumorSample = getSampleData(runContext.getTumorSample(),
                runContext.getTumorSampleInsertSizeMetricsPath());

        return new SampleReport(CheckType.INSERT_SIZE, referenceSample, tumorSample);
    }

    @NotNull
    private List<BaseDataReport> getSampleData(@NotNull final String sampleId,
            @NotNull final Path insertSizeMetricsPath) throws IOException, HealthChecksException {
        final List<String> lines = reader.readLines(insertSizeMetricsPath);

        final BaseDataReport medianReport = getValue(lines, sampleId,
                InsertSizeMetricsCheck.MAPPING_MEDIAN_INSERT_SIZE);
        final BaseDataReport width70PerReport = getValue(lines, sampleId,
                InsertSizeMetricsCheck.MAPPING_WIDTH_OF_70_PERCENT);
        return Arrays.asList(medianReport, width70PerReport);
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
