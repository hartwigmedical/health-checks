package com.hartwig.healthchecks.common.io.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hartwig.healthchecks.common.io.path.PathExtensionFinder;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Reader {

    String FILE_S_NOT_FOUND_MSG = "File %s not Found in path %s";

    @NotNull
    List<String> readLines(@NotNull final String path, @NotNull String extension) throws IOException;

    @NotNull
    static Reader build() {
        return (path, extension) -> {
            final Path fileToRead = PathExtensionFinder.build().findPath(path, extension);
            try (Stream<String> lines = Files.lines(Paths.get(fileToRead.toString()))) {
                return lines.collect(Collectors.toList());
            }
        };
    }
}
