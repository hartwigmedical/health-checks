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

public class WGSExtractor extends AbstractFlintExtractor {

    private static final String WGS_EXT = "dedup_WGSMetrics.txt";

    private final SampleFinderAndReader reader;

    public WGSExtractor(final SampleFinderAndReader reader) {
        super();
        this.reader = reader;
    }

    @Override
    public BaseReport extractFromRunDirectory(final String runDirectory) throws IOException, HealthChecksException {
        final List<BaseDataReport> referenceSample = getSampleData(runDirectory, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumorSample = getSampleData(runDirectory, TUM_SAMPLE_SUFFIX);
        return new SampleReport(CheckType.COVERAGE, referenceSample, tumorSample);
    }

    private List<BaseDataReport> getSampleData(final String runDirectory, final String sampleType)
                    throws IOException, HealthChecksException {
        final String suffix = sampleType + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final String path = runDirectory + File.separator + QC_STATS;
        final SamplePathData samplePath = new SamplePathData(path, SAMPLE_PREFIX, suffix, WGS_EXT);
        final List<String> lines = reader.readLines(samplePath);

        if (lines.isEmpty()) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, suffix, runDirectory));
        }

        final String patientId = getPatientId(suffix, lines, INPUT);

        final BaseDataReport coverageMean = getValue(lines, suffix, patientId, CoverageCheck.COVERAGE_MEAN);
        final BaseDataReport coverageMedian = getValue(lines, suffix, patientId, CoverageCheck.COVERAGE_MEDIAN);
        final BaseDataReport coverageBaseQ = getValue(lines, suffix, patientId, CoverageCheck.COVERAGE_PCT_EXC_BASEQ);
        final BaseDataReport coverageDupe = getValue(lines, suffix, patientId, CoverageCheck.COVERAGE_PCT_EXC_DUPE);
        final BaseDataReport coverageMapQ = getValue(lines, suffix, patientId, CoverageCheck.COVERAGE_PCT_EXC_MAPQ);
        final BaseDataReport coverageOvelap = getValue(lines, suffix, patientId,
                        CoverageCheck.COVERAGE_PCT_EXC_OVERLAP);
        final BaseDataReport coverageTotal = getValue(lines, suffix, patientId, CoverageCheck.COVERAGE_PCT_EXC_TOTAL);
        final BaseDataReport coverageUnpaired = getValue(lines, suffix, patientId,
                        CoverageCheck.COVERAGE_PCT_EXC_UNPAIRED);
        final BaseDataReport coverageSD = getValue(lines, suffix, patientId, CoverageCheck.COVERAGE_SD);
        return Arrays.asList(coverageMean, coverageMedian, coverageBaseQ, coverageDupe, coverageMapQ, coverageOvelap,
                        coverageTotal, coverageUnpaired, coverageSD);
    }

    private BaseDataReport getValue(final List<String> lines, final String suffix, final String patientId,
                    final CoverageCheck check) throws LineNotFoundException {
        final String value = getValueFromLine(lines, suffix, check.getFieldName(), check.getIndex());
        final BaseDataReport baseDataReport = new BaseDataReport(patientId, check.toString(), value);
        logBaseDataReport(baseDataReport);
        return baseDataReport;
    }
}
