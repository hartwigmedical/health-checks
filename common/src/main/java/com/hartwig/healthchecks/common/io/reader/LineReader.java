package com.hartwig.healthchecks.common.io.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;

@FunctionalInterface
public interface LineReader {

    @NotNull
    List<String> readLines(@NotNull final Path filePath, @NotNull Predicate<String> filter)
                    throws IOException, HealthChecksException;

    @NotNull
    static LineReader build() {
        return (filePath, filter) -> {
            final List<String> searchedLines = read(filePath, filter);
            if (searchedLines.isEmpty()) {
                throw new LineNotFoundException(filePath.toString(), filter.toString());
            }
            return searchedLines;
        };
    }

    static List<String> read(final Path filePath, final Predicate<String> filter) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(filePath.toString()))) {
            return lines.filter(filter).collect(Collectors.toList());
        }
    }

}
