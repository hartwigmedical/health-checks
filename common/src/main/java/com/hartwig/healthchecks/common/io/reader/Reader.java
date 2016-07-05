package com.hartwig.healthchecks.common.io.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Reader {

    String FILE_S_NOT_FOUND_MSG = "File %s not Found in path %s";

    @NotNull
    List<String> readLines(@NotNull final String path, @NotNull String extension) throws IOException;

    @NotNull
    static Reader build() {
        return (path, extension) -> {
            final Optional<Path> searchedFile = Files.walk(new File(path).toPath())
                    .filter(filePath -> filePath.getFileName().toString().endsWith(extension)).findFirst();
            if (!searchedFile.isPresent()) {
                throw new FileNotFoundException(String.format(FILE_S_NOT_FOUND_MSG, extension, path));
            }
            final Path fileToRead = searchedFile.get();
            return Files.lines(Paths.get(fileToRead.toString())).collect(Collectors.toList());
        };
    }

}
