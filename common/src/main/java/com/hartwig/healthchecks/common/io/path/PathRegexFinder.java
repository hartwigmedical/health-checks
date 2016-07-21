package com.hartwig.healthchecks.common.io.path;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PathRegexFinder {

    String FILE_S_NOT_FOUND_MSG = "No Match for %s not Found in path %s";

    @NotNull
    Path findPath(@NotNull final String path, @NotNull String regex) throws IOException;

    @NotNull
    static PathRegexFinder build() {
        return (path, regex) -> {
            final Optional<Path> searchedFile = Files.walk(new File(path).toPath())
                            .filter(filePath -> filePath.getFileName().toString().matches(regex)).findFirst();
            if (!searchedFile.isPresent()) {
                throw new FileNotFoundException(String.format(FILE_S_NOT_FOUND_MSG, regex, path));
            }
            return searchedFile.get();
        };
    }
}