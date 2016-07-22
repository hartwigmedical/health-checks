package com.hartwig.healthchecks.common.io.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface FileReader {

    @NotNull
    List<String> readLines(Path fileToRead) throws IOException;

    @NotNull
    static FileReader build() {
        return (fileToRead) -> {
            try (Stream<String> lines = Files.lines(Paths.get(fileToRead.toString()))) {
                return lines.collect(Collectors.toList());
            }
        };
    }
}
