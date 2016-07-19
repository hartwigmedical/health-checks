package com.hartwig.healthchecks.common.io.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;

@FunctionalInterface
public interface LineReader {

    String LINE_NOT_FOUND_ERROR = "File %s does not contain lines with value %s";

    @NotNull
    List<String> readLines(@NotNull final Path filePath, @NotNull Predicate<String> filter)
                    throws IOException, HealthChecksException;

    @NotNull
    static LineReader build() {
        return (filePath, filter) -> {
            final List<String> searchedLines = Files.lines(Paths.get(filePath.toString())).filter(filter)
                            .collect(Collectors.toList());
            if (searchedLines.isEmpty()) {
                throw new LineNotFoundException(String.format(LINE_NOT_FOUND_ERROR, filePath, filter.toString()));
            }
            return searchedLines;
        };
    }

}
