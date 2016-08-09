package com.hartwig.healthchecks.flint.extractor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.path.SamplePathData;
import com.hartwig.healthchecks.common.io.reader.SampleFinderAndReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.jetbrains.annotations.NotNull;

public class WGSMetricsExtractor extends AbstractFlintExtractor {

    private static final String WGS_EXT = "dedup_WGSMetrics.txt";

    @NotNull
    private final SampleFinderAndReader reader;

    public WGSMetricsExtractor(@NotNull final SampleFinderAndReader reader) {
        super();
        this.reader = reader;
    }

    @NotNull
    @Override
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<BaseDataReport> referenceSample = getSampleData(runDirectory, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumorSample = getSampleData(runDirectory, TUM_SAMPLE_SUFFIX);
        return new SampleReport(CheckType.COVERAGE, referenceSample, tumorSample);
    }

    @NotNull
    private List<BaseDataReport> getSampleData(@NotNull final String runDirectory, @NotNull final String sampleType)
            throws IOException, HealthChecksException {
        final String suffix = sampleType + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final String path = runDirectory + File.separator + QC_STATS;
        final SamplePathData samplePath = new SamplePathData(path, SAMPLE_PREFIX, suffix, WGS_EXT);
        final List<String> lines = reader.readLines(samplePath);

        if (lines.isEmpty()) {
            throw new EmptyFileException(suffix, runDirectory);
        }

        final String sampleId = getSampleId(suffix, lines, SAMPLE_IDENTIFIER);

        final BaseDataReport coverageMean = getValue(lines, suffix, sampleId, CoverageCheck.COVERAGE_MEAN);
        final BaseDataReport coverageMedian = getValue(lines, suffix, sampleId, CoverageCheck.COVERAGE_MEDIAN);
        final BaseDataReport coverageBaseQ = getValue(lines, suffix, sampleId, CoverageCheck.COVERAGE_PCT_EXC_BASEQ);
        final BaseDataReport coverageDupe = getValue(lines, suffix, sampleId, CoverageCheck.COVERAGE_PCT_EXC_DUPE);
        final BaseDataReport coverageMapQ = getValue(lines, suffix, sampleId, CoverageCheck.COVERAGE_PCT_EXC_MAPQ);
        final BaseDataReport coverageOverlap = getValue(lines, suffix, sampleId,
                CoverageCheck.COVERAGE_PCT_EXC_OVERLAP);
        final BaseDataReport coverageTotal = getValue(lines, suffix, sampleId, CoverageCheck.COVERAGE_PCT_EXC_TOTAL);
        final BaseDataReport coverageUnpaired = getValue(lines, suffix, sampleId,
                CoverageCheck.COVERAGE_PCT_EXC_UNPAIRED);
        final BaseDataReport coverageSD = getValue(lines, suffix, sampleId, CoverageCheck.COVERAGE_SD);
        return Arrays.asList(coverageMean, coverageMedian, coverageBaseQ, coverageDupe, coverageMapQ, coverageOverlap,
                coverageTotal, coverageUnpaired, coverageSD);
    }

    @NotNull
    private BaseDataReport getValue(@NotNull final List<String> lines, @NotNull final String suffix,
            @NotNull final String sampleId, @NotNull final CoverageCheck check) throws LineNotFoundException {
        final String value = getValueFromLine(lines, suffix, check.getFieldName(), check.getIndex());
        final BaseDataReport baseDataReport = new BaseDataReport(sampleId, check.toString(), value);
        logBaseDataReport(baseDataReport);
        return baseDataReport;
    }
}
