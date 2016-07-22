package com.hartwig.healthchecks.common.io.reader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.io.path.PathExtensionFinder;

@FunctionalInterface
public interface FileFinderAndReader {

    @NotNull
    List<String> readLines(@NotNull final String path, @NotNull String extension) throws IOException;

    @NotNull
    static FileFinderAndReader build() {
        return (path, extension) -> {
            final Path fileToRead = PathExtensionFinder.build().findPath(path, extension);
            return FileReader.build().readLines(fileToRead);
        };
    }
}
