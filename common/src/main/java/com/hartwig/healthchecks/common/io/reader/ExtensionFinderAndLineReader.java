package com.hartwig.healthchecks.common.io.reader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.PathExtensionFinder;

@FunctionalInterface
public interface ExtensionFinderAndLineReader {

    @NotNull
    List<String> readLines(@NotNull final String path, @NotNull String extension, @NotNull Predicate<String> filter)
                    throws IOException, HealthChecksException;

    @NotNull
    static ExtensionFinderAndLineReader build() {
        return (path, extension, filter) -> {
            final Path filePath = PathExtensionFinder.build().findPath(path, extension);
            return LineReader.build().readLines(filePath, filter);
        };
    }

}
