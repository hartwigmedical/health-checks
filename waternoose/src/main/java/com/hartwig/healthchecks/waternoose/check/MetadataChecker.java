package com.hartwig.healthchecks.waternoose.check;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("WeakerAccess")
@ResourceWrapper(type = CheckType.METADATA)
public class MetadataChecker extends ErrorHandlingChecker implements HealthChecker {

    private static final Logger LOGGER = LogManager.getLogger(MetadataChecker.class);

    private static final int FOLDER_NAME_START_INDEX = 1;

    private static final String LOG_FILENAME_FORMAT = "%s.log";
    private static final String DATE_OUT_FORMAT = "yyyy-MM-dd";
    private static final String DATE_IN_FORMAT = "EEE MMM d HH:mm:ss z yyyy";
    private static final String REGEX_SPLIT = "\t";
    private static final String LINE_TO_GET_DATE_FROM = "End Kinship";
    private static final int DATE_LINE_INDEX = 0;

    private static final String PIPELINE_LOG_REGEX = "PipelineCheck.log";
    private static final String PIPELINE_VERSION = "Pipeline version:";
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
    public BaseResult tryRun(@NotNull final RunContext runContext) throws IOException, HealthChecksException {
        final String runDate = extractRunDate(runContext.runDirectory());
        final String pipelineVersion = extractPipelineVersion(runContext.runDirectory());

        return toPatientResult(runContext, runDate, pipelineVersion);
    }

    @NotNull
    @Override
    public BaseResult errorRun(@NotNull final RunContext runContext) {
        return toPatientResult(runContext, HealthCheckConstants.ERROR_VALUE, HealthCheckConstants.ERROR_VALUE);
    }

    @NotNull
    private BaseResult toPatientResult(@NotNull final RunContext runContext, @NotNull final String runDate,
            @NotNull final String pipelineVersion) {
        final List<HealthCheck> refMetaData = toHealthCheckList(runContext.refSample(), runContext.hasPassedTests(),
                runDate, pipelineVersion);
        final List<HealthCheck> tumorMetaData = toHealthCheckList(runContext.tumorSample(),
                runContext.hasPassedTests(), runDate, pipelineVersion);

        HealthCheck.log(LOGGER, refMetaData);
        HealthCheck.log(LOGGER, tumorMetaData);

        return new PatientResult(checkType(), refMetaData, tumorMetaData);
    }

    @NotNull
    private static List<HealthCheck> toHealthCheckList(@NotNull final String sampleId, final boolean hasPassedTests,
            @NotNull final String runDate, @NotNull final String pipelineVersion) {
        return Lists.newArrayList(
                new HealthCheck(sampleId, MetadataCheck.HAS_PASSED_TESTS.toString(), Boolean.toString(hasPassedTests)),
                new HealthCheck(sampleId, MetadataCheck.RUN_DATE.toString(), runDate),
                new HealthCheck(sampleId, MetadataCheck.PIPELINE_VERSION.toString(), pipelineVersion));
    }

    @NotNull
    private static String extractRunDate(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final String runName = toRunName(runDirectory);

        final Path dateTimeLogPath = PathRegexFinder.build().findPath(runDirectory,
                String.format(LOG_FILENAME_FORMAT, runName));
        final List<String> dateLines = LineReader.build().readLines(dateTimeLogPath,
                doesLineStartWith(LINE_TO_GET_DATE_FROM));
        final String date = dateLines.get(DATE_LINE_INDEX).split(REGEX_SPLIT)[FOLDER_NAME_START_INDEX].trim();
        final DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern(DATE_IN_FORMAT, Locale.ENGLISH);
        final LocalDateTime formattedDate = LocalDateTime.parse(date, inFormatter);
        final DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern(DATE_OUT_FORMAT, Locale.ENGLISH);
        return outFormatter.format(formattedDate);
    }

    @NotNull
    private static String toRunName(@NotNull final String runDirectory) {
        String folderName = runDirectory;
        if (runDirectory.contains(File.separator)) {
            folderName = runDirectory.substring(runDirectory.lastIndexOf(File.separator) + FOLDER_NAME_START_INDEX,
                    runDirectory.length());
        }
        return folderName;
    }

    @NotNull
    private static String extractPipelineVersion(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final Path pipelineLog = PathRegexFinder.build().findPath(runDirectory, PIPELINE_LOG_REGEX);
        final List<String> versionsLines = LineReader.build().readLines(pipelineLog,
                doesLineStartWith(PIPELINE_VERSION));
        return versionsLines.get(PIPELINE_VERSION_LINE_INDEX).split(
                PIPELINE_VERSION_LINE_SEPARATOR)[FOLDER_NAME_START_INDEX].trim();
    }

    @NotNull
    private static Predicate<String> doesLineStartWith(@NotNull final String prefix) {
        return line -> line.startsWith(prefix);
    }
}
