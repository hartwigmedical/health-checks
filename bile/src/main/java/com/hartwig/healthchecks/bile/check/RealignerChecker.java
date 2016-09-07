package com.hartwig.healthchecks.bile.check;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.exception.MalformedFileException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.path.PathPrefixSuffixFinder;
import com.hartwig.healthchecks.common.io.reader.FileReader;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.PatientResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@ResourceWrapper(type = CheckType.REALIGNER)
public class RealignerChecker implements HealthChecker {

    @VisibleForTesting
    static final String REALIGNER_CHECK_NAME = "MAPPING_REALIGNER_CHANGED_ALIGNMENTS";

    private static final Logger LOGGER = LogManager.getLogger(RealignerChecker.class);

    private static final String REALIGNER_CHECK_PRECISION = "#0.00000";
    private static final String MALFORMED_FILE_MSG = "Malformed %s path was expecting %s in file";
    private static final String REALIGNER_BASE_DIRECTORY = "mapping";

    private static final String BAM_DIFF_EXTENSION = ".prepostrealign.diff";
    private static final String IGNORE_FOR_DIFF_COUNT_PATTERN_2 = ">";
    private static final String IGNORE_FOR_DIFF_COUNT_PATTERN_1 = "<";

    private static final String FLAGSTAT_EXTENSION = ".postrealign.sliced.flagstat";
    private static final String FLAGSTAT_MAPPED_PATTERN = "mapped";
    private static final String FLAGSTAT_END_OF_MAPPED_VALUE_PATTERN = "+";

    @NotNull
    private final RunContext runContext;

    public RealignerChecker(@NotNull final RunContext runContext) {
        this.runContext = runContext;
    }

    @NotNull
    @Override
    public BaseResult run() throws IOException, HealthChecksException {
        final HealthCheck referenceSample = getSampleData(runContext.runDirectory(), runContext.refSample());
        final HealthCheck tumorSample = getSampleData(runContext.runDirectory(), runContext.tumorSample());

        return new PatientResult(checkType(), Collections.singletonList(referenceSample),
                Collections.singletonList(tumorSample));
    }

    @NotNull
    @Override
    public CheckType checkType() {
        return CheckType.REALIGNER;
    }

    @NotNull
    private static HealthCheck getSampleData(@NotNull final String runDirectory, @NotNull final String sampleId)
            throws IOException, HealthChecksException {
        final String basePath = getBasePathForSample(runDirectory, sampleId);

        final Path bamDiffPath = PathPrefixSuffixFinder.build().findPath(basePath, sampleId, BAM_DIFF_EXTENSION);
        final long diffCount = readDiffCountFromBamDiff(bamDiffPath);

        final Path flagStatPath = PathPrefixSuffixFinder.build().findPath(basePath, sampleId, FLAGSTAT_EXTENSION);
        final long mappedValue = readMappedFromFlagstat(flagStatPath);

        final String value = new DecimalFormat(REALIGNER_CHECK_PRECISION).format((double) diffCount / mappedValue);
        final HealthCheck healthCheck = new HealthCheck(sampleId, REALIGNER_CHECK_NAME, value);
        healthCheck.log(LOGGER);
        return healthCheck;
    }

    private static long readMappedFromFlagstat(@NotNull final Path flagStatPath)
            throws IOException, HealthChecksException {
        final List<String> lines = FileReader.build().readLines(flagStatPath);

        final Optional<String> mappedLine = lines.stream().filter(
                line -> line.contains(FLAGSTAT_MAPPED_PATTERN)).findFirst();
        if (!mappedLine.isPresent()) {
            throw new LineNotFoundException(flagStatPath.toString(), FLAGSTAT_MAPPED_PATTERN);
        }
        final String mapped = mappedLine.get();
        if (!mapped.contains(FLAGSTAT_END_OF_MAPPED_VALUE_PATTERN)) {
            throw new MalformedFileException(
                    String.format(MALFORMED_FILE_MSG, flagStatPath.toString(), FLAGSTAT_END_OF_MAPPED_VALUE_PATTERN));
        }
        final String mappedValue = mapped.substring(0, mapped.indexOf(FLAGSTAT_END_OF_MAPPED_VALUE_PATTERN));
        return Long.valueOf(mappedValue.trim());
    }

    private static long readDiffCountFromBamDiff(@NotNull final Path bamDiffPath)
            throws IOException, HealthChecksException {
        final List<String> lines = FileReader.build().readLines(bamDiffPath);
        return lines.stream().filter(line -> !(line.startsWith(IGNORE_FOR_DIFF_COUNT_PATTERN_1) || line.startsWith(
                IGNORE_FOR_DIFF_COUNT_PATTERN_2))).count();
    }

    @NotNull
    private static String getBasePathForSample(@NotNull final String runDirectory, @NotNull final String sampleId) {
        return runDirectory + File.separator + sampleId + File.separator + REALIGNER_BASE_DIRECTORY;
    }
}
