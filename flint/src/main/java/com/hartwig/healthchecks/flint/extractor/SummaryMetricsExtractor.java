package com.hartwig.healthchecks.flint.extractor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.path.SamplePathData;
import com.hartwig.healthchecks.common.io.reader.SampleFinderAndReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

public class SummaryMetricsExtractor extends AbstractFlintExtractor {

    private static final String PAIR = "PAIR";

    private static final String AL_SUM_METRICS = ".alignment_summary_metrics";

    private final SampleFinderAndReader reader;

    public SummaryMetricsExtractor(final SampleFinderAndReader reader) {
        super();
        this.reader = reader;
    }

    @Override
    public BaseReport extractFromRunDirectory(final String runDirectory) throws IOException, HealthChecksException {
        final List<BaseDataReport> referenceSample = getSampleData(runDirectory, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumorSample = getSampleData(runDirectory, TUM_SAMPLE_SUFFIX);
        return new SampleReport(CheckType.SUMMARY_METRICS, referenceSample, tumorSample);
    }

    private List<BaseDataReport> getSampleData(final String runDirectory, final String sampleType)
                    throws IOException, HealthChecksException {
        final String suffix = sampleType + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final String path = runDirectory + File.separator + QC_STATS;

        final SamplePathData samplePath = new SamplePathData(path, SAMPLE_PREFIX, suffix, AL_SUM_METRICS);

        final List<String> lines = reader.readLines(samplePath);
        if (lines.isEmpty()) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, suffix, runDirectory));
        }
        final Optional<String> searchedLine = lines.stream().filter(fileLine -> fileLine.startsWith(PAIR)).findFirst();
        if (!searchedLine.isPresent()) {
            throw new LineNotFoundException(suffix, PAIR);
        }
        final String patientId = getPatientId(suffix, lines, INPUT);

        final BaseDataReport pfIndelRate = getValue(searchedLine.get(), patientId,
                        SummaryMetricsCheck.MAPPING_PF_INDEL_RATE);
        final BaseDataReport pctAdapter = getValue(searchedLine.get(), patientId,
                        SummaryMetricsCheck.MAPPING_PCT_ADAPTER);
        final BaseDataReport pctChimeras = getValue(searchedLine.get(), patientId,
                        SummaryMetricsCheck.MAPPING_PCT_CHIMERA);
        final BaseDataReport pfMisMatch = getValue(searchedLine.get(), patientId,
                        SummaryMetricsCheck.MAPPING_PF_MISMATCH_RATE);
        final BaseDataReport strandBalance = getValue(searchedLine.get(), patientId,
                        SummaryMetricsCheck.MAPPING_STRAND_BALANCE);
        return Arrays.asList(pfIndelRate, pctAdapter, pctChimeras, pfMisMatch, strandBalance);
    }

    private BaseDataReport getValue(final String line, final String patientId, final SummaryMetricsCheck check)
                    throws LineNotFoundException {
        final String value = line.split(SEPERATOR_REGEX)[check.getIndex()];
        final BaseDataReport baseDataReport = new BaseDataReport(patientId, check.toString(), value);
        logBaseDataReport(baseDataReport);
        return baseDataReport;
    }
}
