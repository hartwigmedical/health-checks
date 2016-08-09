package com.hartwig.healthchecks.flint.extractor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.path.SamplePathData;
import com.hartwig.healthchecks.common.io.reader.SampleFinderAndReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class SummaryMetricsExtractor extends AbstractFlintExtractor {

    private static final Logger LOGGER = LogManager.getLogger(SummaryMetricsExtractor.class);

    private static final String PICARD_CATEGORY_TO_READ = "PAIR";
    private static final String AL_SUM_METRICS_EXTENSION = ".alignment_summary_metrics";

    @NotNull
    private final SampleFinderAndReader reader;

    public SummaryMetricsExtractor(@NotNull final SampleFinderAndReader reader) {
        super();
        this.reader = reader;
    }

    @NotNull
    @Override
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<BaseDataReport> referenceSample = getSampleData(runDirectory, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumorSample = getSampleData(runDirectory, TUM_SAMPLE_SUFFIX);
        return new SampleReport(CheckType.SUMMARY_METRICS, referenceSample, tumorSample);
    }

    @NotNull
    private List<BaseDataReport> getSampleData(@NotNull final String runDirectory, @NotNull final String sampleType)
            throws IOException, HealthChecksException {
        final String suffix = sampleType + UNDERSCORE + DEDUP_SAMPLE_SUFFIX;
        final String path = runDirectory + File.separator + QC_STATS;

        final SamplePathData samplePath = new SamplePathData(path, SAMPLE_PREFIX, suffix, AL_SUM_METRICS_EXTENSION);

        final List<String> lines = reader.readLines(samplePath);

        final Optional<String> searchedLine = lines.stream().filter(
                fileLine -> fileLine.startsWith(PICARD_CATEGORY_TO_READ)).findFirst();
        if (!searchedLine.isPresent()) {
            throw new LineNotFoundException(suffix, PICARD_CATEGORY_TO_READ);
        }
        final String sampleId = getSampleId(suffix, lines, PICARD_SAMPLE_IDENTIFIER);

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
    private static BaseDataReport getValue(@NotNull final String line, @NotNull final String sampleId,
            @NotNull final SummaryMetricsCheck check) throws LineNotFoundException {
        final String value = line.split(SEPARATOR_REGEX)[check.getIndex()];
        final BaseDataReport baseDataReport = new BaseDataReport(sampleId, check.toString(), value);
        logBaseDataReport(LOGGER, baseDataReport);
        return baseDataReport;
    }
}
