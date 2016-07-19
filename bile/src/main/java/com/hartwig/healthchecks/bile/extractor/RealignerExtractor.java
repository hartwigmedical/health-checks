package com.hartwig.healthchecks.bile.extractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.exception.MalformedFileException;
import com.hartwig.healthchecks.common.io.extractor.AbstractDataExtractor;
import com.hartwig.healthchecks.common.io.path.SamplePath;
import com.hartwig.healthchecks.common.io.path.SamplePathFinder;
import com.hartwig.healthchecks.common.io.reader.SampleReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

public class RealignerExtractor extends AbstractDataExtractor {

    private static final String DECIMAL_PREC = "#0.00000";

    private static final String MAPPED = "mapped";

    private static final String SLICED_EXT = ".postrealign.sliced.flagstat";

    private static final String BAM_DIFF_EXT = ".prepostrealign.diff";

    private static final String MAP_REALI_CHAN_ALIGN = "MAPPING_REALIGNER_CHANGED_ALIGNMENTS";

    private final SampleReader reader;

    private final SamplePathFinder samplePathFinder;

    public RealignerExtractor(final SampleReader reader, final SamplePathFinder samplePathFinder) {
        super();
        this.reader = reader;
        this.samplePathFinder = samplePathFinder;
    }

    @Override
    public BaseReport extractFromRunDirectory(final String runDirectory) throws IOException, HealthChecksException {
        final List<BaseDataReport> referenceSample = getSampleData(runDirectory, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumorSample = getSampleData(runDirectory, TUM_SAMPLE_SUFFIX);
        return new SampleReport(CheckType.REALIGNER, referenceSample, tumorSample);
    }

    private List<BaseDataReport> getSampleData(final String runDirectory, final String sampleType)
                    throws IOException, HealthChecksException {
        final String suffix = sampleType + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final String path = runDirectory + File.separator + QC_STATS;
        final Path pathFound = samplePathFinder.findPath(path, SAMPLE_PREFIX, suffix);

        final String patientId = pathFound.toString().substring(pathFound.toString().lastIndexOf("/") + ONE,
                        pathFound.toString().lastIndexOf("_dedup"));

        final long diffCount = getDiffCount(runDirectory, suffix, path);
        final long mappedValue = getMappedValue(runDirectory, suffix, path);
        final String value = new DecimalFormat(DECIMAL_PREC).format((double) diffCount / mappedValue);
        final BaseDataReport baseDataReport = new BaseDataReport(patientId, MAP_REALI_CHAN_ALIGN, value);
        logBaseDataReport(baseDataReport);
        return Arrays.asList(baseDataReport);
    }

    private long getMappedValue(final String runDirectory, final String suffix, final String path)
                    throws IOException, HealthChecksException {
        final SamplePath samplePath = new SamplePath(path, SAMPLE_PREFIX, suffix, SLICED_EXT);

        final List<String> lines = reader.readLines(samplePath);
        if (lines.isEmpty()) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, SLICED_EXT, runDirectory));
        }

        final Optional<String> mappedLine = lines.stream().filter(line -> line.contains(MAPPED)).findFirst();
        if (!mappedLine.isPresent()) {
            throw new LineNotFoundException(String.format(LINE_NOT_FOUND_ERROR, suffix, runDirectory));
        }
        final String mapped = mappedLine.get();
        if (!mapped.contains(PLUS)) {
            throw new MalformedFileException(String.format(MALFORMED_FILE_MSG, SLICED_EXT, runDirectory, PLUS));
        }
        final String mappedValue = mapped.substring(ZERO, mapped.indexOf(PLUS));
        return Long.valueOf(mappedValue.trim());
    }

    private long getDiffCount(final String runDirectory, final String suffix, final String path)
                    throws IOException, EmptyFileException {
        final SamplePath samplePath = new SamplePath(path, SAMPLE_PREFIX, suffix, BAM_DIFF_EXT);
        final List<String> lines = reader.readLines(samplePath);
        if (lines.isEmpty()) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, suffix, runDirectory));
        }
        return lines.stream().filter(line -> !(line.startsWith(SMALLER_THAN) || line.startsWith(BIGGER_THAN))).count();
    }

}
