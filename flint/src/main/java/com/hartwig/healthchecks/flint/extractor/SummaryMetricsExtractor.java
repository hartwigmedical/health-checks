package com.hartwig.healthchecks.flint.extractor;

import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.SEPARATOR_REGEX;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

public class SummaryMetricsExtractor implements DataExtractor {

    private static final Logger LOGGER = LogManager.getLogger(SummaryMetricsExtractor.class);

    // KODU: metrics files stores in {run}/QCStats/{sample}_dedup/{sample}<>.alignment_summary_metrics
    private static final String METRICS_BASE_DIRECTORY = "QCStats";
    private static final String METRICS_SUB_DIRECTORY_SUFFIX = "_dedup";
    private static final String ALIGNMENT_SUMMARY_METRICS_EXTENSION = ".alignment_summary_metrics";

    private static final String PICARD_CATEGORY_TO_READ = "PAIR";

    @NotNull
    private final RunContext runContext;

    public SummaryMetricsExtractor(@NotNull final RunContext runContext) {
        this.runContext = runContext;
    }

    @NotNull
    @Override
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<BaseDataReport> referenceSample = getSampleData(runContext.runDirectory(), runContext.refSample());
        final List<BaseDataReport> tumorSample = getSampleData(runContext.runDirectory(), runContext.tumorSample());
        return new SampleReport(CheckType.SUMMARY_METRICS, referenceSample, tumorSample);
    }

    @NotNull
    private static List<BaseDataReport> getSampleData(@NotNull final String runDirectory,
            @NotNull final String sampleId) throws IOException, HealthChecksException {
        final String basePath = getBasePathForSample(runDirectory, sampleId);
        Path alignmentSummaryMetricsPath = SamplePathFinder.build().findPath(basePath, sampleId,
                ALIGNMENT_SUMMARY_METRICS_EXTENSION);
        final List<String> lines = FileReader.build().readLines(alignmentSummaryMetricsPath);

        final Optional<String> searchedLine = lines.stream().filter(
                fileLine -> fileLine.startsWith(PICARD_CATEGORY_TO_READ)).findFirst();
        if (!searchedLine.isPresent()) {
            throw new LineNotFoundException(PICARD_CATEGORY_TO_READ);
        }

        final BaseDataReport pfIndelRate = getValue(searchedLine.get(), sampleId,
                SummaryMetricsCheck.MAPPING_PF_INDEL_RATE);
        final BaseDataReport pctAdapter = getValue(searchedLine.get(), sampleId,
                SummaryMetricsCheck.MAPPING_PCT_ADAPTER);
        final BaseDataReport pctChimeras = getValue(searchedLine.get(), sampleId,
                SummaryMetricsCheck.MAPPING_PCT_CHIMERA);
        final BaseDataReport pfMisMatch = getValue(searchedLine.get(), sampleId,
                SummaryMetricsCheck.MAPPING_PF_MISMATCH_RATE);
        final BaseDataReport strandBalance = getValue(searchedLine.get(), sampleId,
                SummaryMetricsCheck.MAPPING_STRAND_BALANCE);
        return Arrays.asList(pfIndelRate, pctAdapter, pctChimeras, pfMisMatch, strandBalance);
    }

    @NotNull
    private static String getBasePathForSample(@NotNull final String runDirectory, @NotNull final String sampleId) {
        return runDirectory + File.separator + METRICS_BASE_DIRECTORY + File.separator + sampleId
                + METRICS_SUB_DIRECTORY_SUFFIX;
    }

    @NotNull
    private static BaseDataReport getValue(@NotNull final String line, @NotNull final String sampleId,
            @NotNull final SummaryMetricsCheck check) throws LineNotFoundException {
        final String value = line.split(SEPARATOR_REGEX)[check.getIndex()];
        final BaseDataReport baseDataReport = new BaseDataReport(sampleId, check.toString(), value);
        baseDataReport.log(LOGGER);
        return baseDataReport;
    }
}
