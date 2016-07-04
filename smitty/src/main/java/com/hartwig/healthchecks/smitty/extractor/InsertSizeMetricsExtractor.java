package com.hartwig.healthchecks.smitty.extractor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.extractor.AbstractDataExtractor;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.smitty.reader.InsertSizeMetricsReader;
import com.hartwig.healthchecks.smitty.report.InsertSizeMetricsReport;

public class InsertSizeMetricsExtractor extends AbstractDataExtractor {

    private static final String INPUT = "INPUT";

    private static final String SPACE = " ";

    private static final String EQUAL_REGEX = "=";

    private static final String BAM_EXT = "_dedup.bam";

    private static final String QC_STATS = "QCStats";

    private final InsertSizeMetricsReader reader;

    public InsertSizeMetricsExtractor(final InsertSizeMetricsReader reader) {
        super();
        this.reader = reader;
    }

    @Override
    public BaseReport extractFromRunDirectory(final String runDirectory) throws IOException, HealthChecksException {
        final List<BaseDataReport> referenceSample = getSampleData(runDirectory, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumorSample = getSampleData(runDirectory, TUM_SAMPLE_SUFFIX);

        return new InsertSizeMetricsReport(CheckType.INSERT_SIZE, referenceSample, tumorSample);
    }

    private List<BaseDataReport> getSampleData(final String runDirectory, final String sampleType)
                    throws IOException, HealthChecksException {
        final String suffix = sampleType + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final String path = runDirectory + File.separator + QC_STATS;
        final List<String> lines = reader.readLines(path, SAMPLE_PREFIX, suffix);
        if (lines.isEmpty()) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, suffix, runDirectory));
        }

        final String patientId = getPatientId(suffix, lines, INPUT);

        final BaseDataReport medianReport = getValue(lines, suffix, patientId,
                        InsertSizeMetricsCheck.MAPPING_MEDIAN_INSERT_SIZE);
        final BaseDataReport width70PerReport = getValue(lines, suffix, patientId,
                        InsertSizeMetricsCheck.MAPPING_WIDTH_OF_70_PERCENT);
        return Arrays.asList(medianReport, width70PerReport);
    }

    private String getPatientId(final String suffix, final List<String> lines, final String filter)
                    throws LineNotFoundException {
        final Integer index = findLineIndex(suffix, lines, filter);
        final String value = Arrays.stream(lines.get(index).split(SPACE)).filter(line -> line.contains(filter))
                        .map(inputLine -> {
                            final String[] values = inputLine.split(EQUAL_REGEX);
                            return values[ONE];
                        }).findFirst().get();
        return value.substring(value.lastIndexOf(File.separator) + ONE, value.indexOf(BAM_EXT));
    }

    private BaseDataReport getValue(final List<String> lines, final String suffix, final String patientId,
                    final InsertSizeMetricsCheck check) throws LineNotFoundException {
        final String value = getValueFromLine(lines, suffix, check.getFieldName(), check.getIndex());
        final BaseDataReport baseDataReport = new BaseDataReport(patientId, check.getName(), value);
        logBaseDataReport(baseDataReport);
        return baseDataReport;
    }

    private String getValueFromLine(final List<String> lines, final String suffix, final String filter,
                    final int fieldIndex) throws LineNotFoundException {
        final Integer index = findLineIndex(suffix, lines, filter);
        final String line = lines.get(index + ONE);
        final String[] lineValues = line.split(SEPERATOR_REGEX);
        return lineValues[fieldIndex];
    }

    private Integer findLineIndex(final String suffix, final List<String> lines, final String filter)
                    throws LineNotFoundException {
        final Optional<Integer> lineNumbers = IntStream.range(0, lines.size())
                        .filter(index -> lines.get(index).contains(filter)).mapToObj(index -> index).findFirst();
        if (!lineNumbers.isPresent()) {
            throw new LineNotFoundException(String.format(LINE_NOT_FOUND_ERROR, suffix, filter));
        }
        return lineNumbers.get();
    }
}
