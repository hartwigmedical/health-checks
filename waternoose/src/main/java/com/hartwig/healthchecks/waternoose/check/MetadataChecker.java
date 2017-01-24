package com.hartwig.healthchecks.waternoose.check;

import static com.google.common.collect.Iterables.getLast;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import com.google.common.collect.Lists;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.ErrorHandlingChecker;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.checks.HealthCheckConstants;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.path.PathRegexFinder;
import com.hartwig.healthchecks.common.io.reader.LineReader;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.PatientResult;

@SuppressWarnings("WeakerAccess")
@ResourceWrapper(type = CheckType.METADATA)
public class MetadataChecker extends ErrorHandlingChecker implements HealthChecker {

    private static final Logger LOGGER = LogManager.getLogger(MetadataChecker.class);

    private static final String LOG_FILENAME_FORMAT = "%s.log";
    private static final String DATE_OUT_FORMAT = "yyyy-MM-dd";
    private static final String DATE_IN_FORMAT = "[EEE MMM d HH:mm:ss z yyyy]"
                                                 + "[EEE MMM ppd HH:mm:ss z yyyy]"
                                                 + "[EEE d MMM HH:mm:ss z yyyy]"
                                                 + "[EEE d MMM yyyy HH:mm:ss z]"
                                                 + "[EEE MMM d yyyy HH:mm:ss z]";
    private static final String REGEX_SPLIT = "\t";
    private static final String LINE_TO_GET_DATE_FROM_REGEX = "End\\s+(Kinship|Finalize)";

    private static final String PIPELINE_LOG_REGEX = "PipelineCheck.log";
    private static final String PIPELINE_VERSION_REGEX = "Pipeline version:";
    private static final String PIPELINE_VERSION_LINE_SEPARATOR = ":";
    private static final int PIPELINE_VERSION_LINE_INDEX = 0;

    public MetadataChecker() {
    }

    @NotNull
    @Override
    public CheckType checkType() {
        return CheckType.METADATA;
    }

    @NotNull
    @Override
    public BaseResult tryRun(@NotNull final RunContext runContext)
            throws IOException, HealthChecksException {
        final String runDate = extractRunDate(runContext);
        final String pipelineVersion = extractPipelineVersion(runContext.runDirectory());

        return toPatientResult(runContext, runDate, pipelineVersion);
    }

    @NotNull
    @Override
    public BaseResult errorRun(@NotNull final RunContext runContext) {
        String pipelineVersion = HealthCheckConstants.ERROR_VALUE;
        try {
            pipelineVersion = extractPipelineVersion(runContext.runDirectory());
        } catch (IOException | HealthChecksException exception) {
            // KODU: This is to work around the fact that pipeline version and run date are linked.
            // If run date extraction fails, pipeline version wont be available.
            // Should be two separate checkers...
        }
        return toPatientResult(runContext, HealthCheckConstants.ERROR_VALUE, pipelineVersion);
    }

    @NotNull
    private BaseResult toPatientResult(@NotNull final RunContext runContext,
                                       @NotNull final String runDate,
                                       @NotNull final String pipelineVersion) {
        final List<HealthCheck> refMetaData = toHealthCheckList(runContext.refSample(),
                                                                runContext,
                                                                runDate,
                                                                pipelineVersion);
        final List<HealthCheck> tumorMetaData = toHealthCheckList(runContext.tumorSample(),
                                                                  runContext,
                                                                  runDate,
                                                                  pipelineVersion);

        HealthCheck.log(LOGGER, refMetaData);
        HealthCheck.log(LOGGER, tumorMetaData);

        return new PatientResult(checkType(), refMetaData, tumorMetaData);
    }

    @NotNull
    private static List<HealthCheck> toHealthCheckList(@NotNull final String sampleId,
                                                       @NotNull final RunContext runContext,
                                                       @NotNull final String runDate,
                                                       @NotNull final String pipelineVersion) {
        return Lists.newArrayList(new HealthCheck(sampleId, MetadataCheck.RUN_NAME.toString(), runContext.runName()),
                                  new HealthCheck(sampleId,
                                                  MetadataCheck.HAS_PASSED_TESTS.toString(),
                                                  Boolean.toString(runContext.hasPassedTests())),
                                  new HealthCheck(sampleId, MetadataCheck.RUN_DATE.toString(), runDate),
                                  new HealthCheck(sampleId,
                                                  MetadataCheck.PIPELINE_VERSION.toString(),
                                                  pipelineVersion));
    }

    @NotNull
    private static String extractRunDate(@NotNull final RunContext runContext)
            throws IOException, HealthChecksException {
        final Path dateTimeLogPath = PathRegexFinder.build()
                                                    .findPath(runContext.runDirectory(),
                                                              String.format(LOG_FILENAME_FORMAT,
                                                                            runContext.runName()));
        final List<String> dateLines = LineReader.build()
                                                .readLines(dateTimeLogPath, doesLineStartWith(
                                                        LINE_TO_GET_DATE_FROM_REGEX));
        final String date = datePart(getLast(dateLines).split(REGEX_SPLIT));
        final DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern(DATE_IN_FORMAT, Locale.ENGLISH);
        final LocalDateTime formattedDate = LocalDateTime.parse(date, inFormatter);
        final DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern(DATE_OUT_FORMAT, Locale.ENGLISH);
        return outFormatter.format(formattedDate);
    }

    @NotNull
    private static String datePart(@NotNull String[] parts) {
        if (parts[0].contains(" ")) {
            return parts[1].trim();
        } else {
            return parts[2].trim();
        }
    }

    @NotNull
    private static String extractPipelineVersion(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final Path pipelineLogPath = PathRegexFinder.build().findPath(runDirectory, PIPELINE_LOG_REGEX);
        final List<String> versionsLine = LineReader.build()
                                                    .readLines(pipelineLogPath,
                                                               doesLineStartWith(PIPELINE_VERSION_REGEX));
        return versionsLine.get(PIPELINE_VERSION_LINE_INDEX).split(PIPELINE_VERSION_LINE_SEPARATOR)[1].trim();
    }

    @NotNull
    private static Predicate<String> doesLineStartWith(@NotNull final String regex) {
        return line -> line.matches(String.format("%s.*", regex));
    }
}
