package com.hartwig.healthchecks.bile.extractor;

import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.REF_SAMPLE_SUFFIX;
import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.SAMPLE_PREFIX;
import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.TUM_SAMPLE_SUFFIX;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.exception.MalformedFileException;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.path.SamplePathData;
import com.hartwig.healthchecks.common.io.path.SamplePathFinder;
import com.hartwig.healthchecks.common.io.reader.SampleFinderAndReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class RealignerExtractor implements DataExtractor {

    private static final Logger LOGGER = LogManager.getLogger(RealignerExtractor.class);

    private static final String DECIMAL_PRECISION = "#0.00000";
    private static final String MAPPED = "mapped";
    private static final String SLICED_EXT = ".postrealign.sliced.flagstat";
    private static final String BAM_DIFF_EXT = ".prepostrealign.diff";

    private static final String MAP_REALIGN_CHAN_ALIGN = "MAPPING_REALIGNER_CHANGED_ALIGNMENTS";
    private static final String MALFORMED_FILE_MSG = "Malformed %s file is path %s was expecting %s in file";

    private static final String BIGGER_THAN = ">";
    private static final String SMALLER_THAN = "<";
    private static final String PLUS = "+";
    private static final String QC_STATS = "QCStats";
    private static final String UNDERSCORE = "_";
    private static final String DEDUP_SAMPLE_SUFFIX = "dedup";

    @NotNull
    private final SampleFinderAndReader reader;
    @NotNull
    private final SamplePathFinder samplePathFinder;

    public RealignerExtractor(@NotNull final SampleFinderAndReader reader,
            @NotNull final SamplePathFinder samplePathFinder) {
        this.reader = reader;
        this.samplePathFinder = samplePathFinder;
    }

    @NotNull
    @Override
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<BaseDataReport> referenceSample = getSampleData(runDirectory, SAMPLE_PREFIX, REF_SAMPLE_SUFFIX);
        final List<BaseDataReport> tumorSample = getSampleData(runDirectory, SAMPLE_PREFIX, TUM_SAMPLE_SUFFIX);
        return new SampleReport(CheckType.REALIGNER, referenceSample, tumorSample);
    }

    @NotNull
    private List<BaseDataReport> getSampleData(@NotNull final String runDirectory, @NotNull final String prefix,
            @NotNull final String suffix) throws IOException, HealthChecksException {
        final Path sampleFile = samplePathFinder.findPath(runDirectory, prefix, suffix);
        final String sampleId = sampleFile.getFileName().toString();

        final long diffCount = getDiffCount(runDirectory, suffix, "");
        final long mappedValue = getMappedValue(runDirectory, suffix, "");
        final String value = new DecimalFormat(DECIMAL_PRECISION).format((double) diffCount / mappedValue);
        final BaseDataReport baseDataReport = new BaseDataReport(sampleId, MAP_REALIGN_CHAN_ALIGN, value);
        baseDataReport.log(LOGGER);
        return Collections.singletonList(baseDataReport);
    }

    private long getMappedValue(@NotNull final String runDirectory, @NotNull final String suffix,
            @NotNull final String path) throws IOException, HealthChecksException {
        final SamplePathData samplePath = new SamplePathData(path, SAMPLE_PREFIX, suffix, SLICED_EXT);

        final List<String> lines = reader.readLines(samplePath);
        if (lines.isEmpty()) {
            throw new EmptyFileException(SLICED_EXT, runDirectory);
        }

        final Optional<String> mappedLine = lines.stream().filter(line -> line.contains(MAPPED)).findFirst();
        if (!mappedLine.isPresent()) {
            throw new LineNotFoundException(suffix, MAPPED);
        }
        final String mapped = mappedLine.get();
        if (!mapped.contains(PLUS)) {
            throw new MalformedFileException(String.format(MALFORMED_FILE_MSG, SLICED_EXT, runDirectory, PLUS));
        }
        final String mappedValue = mapped.substring(0, mapped.indexOf(PLUS));
        return Long.valueOf(mappedValue.trim());
    }

    private long getDiffCount(@NotNull final String runDirectory, @NotNull final String suffix,
            @NotNull final String path) throws IOException, HealthChecksException {
        final SamplePathData samplePath = new SamplePathData(path, SAMPLE_PREFIX, suffix, BAM_DIFF_EXT);
        final List<String> lines = reader.readLines(samplePath);
        if (lines.isEmpty()) {
            throw new EmptyFileException(suffix, runDirectory);
        }
        return lines.stream().filter(line -> !(line.startsWith(SMALLER_THAN) || line.startsWith(BIGGER_THAN))).count();
    }
}
