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

import com.hartwig.healthchecks.common.io.path.SamplePathFinder;

@FunctionalInterface
public interface SampleReader {

    String FILE_S_NOT_FOUND_MSG = "File %s not Found in path %s";

    @NotNull
    List<String> readLines(@NotNull final SamplePath samplePath) throws IOException;

    @NotNull
    static SampleReader build() {
        return (samplePath) -> {
            final Path filePath = SamplePathFinder.build().findPath(samplePath.getPath(), samplePath.getPrefix(),
                            samplePath.getSuffix());
            final Path fileToRead = findFileInPath(filePath.toString(), samplePath.getExtension());
            return Files.lines(Paths.get(fileToRead.toString())).collect(Collectors.toList());
        };
    }

    @NotNull
    static Path findFileInPath(@NotNull final String searchPath, @NotNull final String suffix) throws IOException {
        final Optional<Path> searchedFile = Files.walk(new File(searchPath).toPath())
                        .filter(path -> path.getFileName().toString().endsWith(suffix)).findFirst();
        if (!searchedFile.isPresent()) {
            throw new FileNotFoundException(String.format(FILE_S_NOT_FOUND_MSG, suffix, searchPath));
        }
        return searchedFile.get();
    }
}
