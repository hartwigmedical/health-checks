package com.hartwig.healthchecks.common.report.metadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.PathRegexFinder;
import com.hartwig.healthchecks.common.io.reader.LineReader;

import org.jetbrains.annotations.NotNull;

public class MetadataExtractor {

    private static final int ZERO = 0;
    private static final int ONE = 1;

    private static final String LOG_FILENAME_FORMAT = "%s.log";
    private static final String DATE_OUT_FORMAT = "yyyy-MMM-dd'T'HH.mm.ss";
    private static final String DATE_IN_FORMATTER = "EEE MMM d HH:mm:ss z yyyy";
    private static final String REGEX_SPLIT = "\t";
    private static final String LAST_LINE = "End Kinship";

    private static final String PIPELINE_LOG_REGEX = "PipelineCheck.log";
    private static final String PIPELINE_VERSION = "Pipeline version:";
    private static final String PIPELINE_VERSION_LINE_SEPARATOR = ":";

    @NotNull
    private final PathRegexFinder pathFinder;
    @NotNull
    private final LineReader lineReader;

    public MetadataExtractor(@NotNull final PathRegexFinder pathFinder, @NotNull final LineReader lineReader) {
        this.pathFinder = pathFinder;
        this.lineReader = lineReader;
    }

    @NotNull
    public ReportMetadata extractMetadata(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        String folderName = runDirectory;
        if (runDirectory.contains(File.separator)) {
            folderName = runDirectory.substring(runDirectory.lastIndexOf(File.separator) + ONE, runDirectory.length());
        }
        final Path dateTimeLogPath = pathFinder.findPath(runDirectory, String.format(LOG_FILENAME_FORMAT, folderName));
        final List<String> dateLines = lineReader.readLines(dateTimeLogPath, doesLineStartWith(LAST_LINE));
        final String date = dateLines.get(ZERO).split(REGEX_SPLIT)[ONE].trim();
        final DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern(DATE_IN_FORMATTER, Locale.ENGLISH);
        final LocalDateTime formattedDate = LocalDateTime.parse(date, inFormatter);
        final DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern(DATE_OUT_FORMAT, Locale.ENGLISH);

        final Path pipelineLog = pathFinder.findPath(runDirectory, PIPELINE_LOG_REGEX);
        final List<String> versionsLines = lineReader.readLines(pipelineLog, doesLineStartWith(PIPELINE_VERSION));
        final String pipelineVersion = versionsLines.get(ZERO).split(PIPELINE_VERSION_LINE_SEPARATOR)[ONE];
        return new ReportMetadata(formattedDate.format(outFormatter), pipelineVersion.trim());
    }

    @NotNull
    private static Predicate<String> doesLineStartWith(@NotNull final String prefix) {
        return line -> line.startsWith(prefix);
    }
}
