package com.hartwig.healthchecks.flint.reader;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public class InsertSizeMetricsReader {

    private static final String INSERT_SIZE_METRICS = ".insert_size_metrics";

    private static final String FILE_S_NOT_FOUND_MSG = "File %s not Found in path %s";

    @NotNull
    public List<String> readLines(@NotNull final String runDirectory, @NotNull final String prefix,
                    @NotNull final String suffix) throws IOException {
        final Path path = getFilesPaths(runDirectory, prefix, suffix);
        final Path filePath = findFileInPath(path.toString(), INSERT_SIZE_METRICS);
        return Files.lines(Paths.get(filePath.toString())).collect(toList());
    }

    @NotNull
    private Path findFileInPath(@NotNull final String searchPath, @NotNull final String suffix) throws IOException {
        final Optional<Path> searchedFile = Files.walk(new File(searchPath).toPath())
                        .filter(path -> path.getFileName().toString().endsWith(suffix)).findFirst();
        if (!searchedFile.isPresent()) {
            throw new FileNotFoundException(String.format(FILE_S_NOT_FOUND_MSG, suffix, searchPath));
        }
        return searchedFile.get();
    }

    @NotNull
    private Path getFilesPaths(@NotNull final String runDirectory, @NotNull final String prefix,
                    @NotNull final String suffix) throws IOException {
        final Optional<Path> filePath = Files.walk(new File(runDirectory).toPath())
                        .filter(path -> path.getFileName().toString().startsWith(prefix)
                                        && path.getFileName().toString().endsWith(suffix)
                                        && path.toString().contains(runDirectory + File.separator + prefix))
                        .findFirst();
        if (!filePath.isPresent()) {
            throw new FileNotFoundException(String.format(FILE_S_NOT_FOUND_MSG, suffix, runDirectory));
        }
        return filePath.get();
    }
}
