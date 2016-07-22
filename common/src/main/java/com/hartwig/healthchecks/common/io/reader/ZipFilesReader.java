package com.hartwig.healthchecks.common.io.reader;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.PathsExtensionFinder;

public class ZipFilesReader {

    private static final Logger LOGGER = LogManager.getLogger(ZipFilesReader.class);

    private static final String ZIP_FILES_SUFFIX = ".zip";

    @NotNull
    public List<String> readAllLinesFromZips(final Path path, final String fileName) throws IOException {
        final List<Path> zipPaths = PathsExtensionFinder.build().findPaths(path.toString(), ZIP_FILES_SUFFIX);
        return zipPaths.stream().map(zipPath -> readFileFromZip(zipPath.toString(), fileName))
                        .flatMap(Collection::stream).collect(toList());
    }

    @NotNull
    public Path getZipFilesPath(@NotNull final String runDirectory, @NotNull final String prefix,
                    @NotNull final String suffix) throws IOException {
        return Files.walk(new File(runDirectory).toPath())
                        .filter(path -> path.getFileName().toString().startsWith(prefix)
                                        && path.getFileName().toString().endsWith(suffix)
                                        && path.toString().contains(runDirectory + File.separator + prefix))
                        .findFirst().get();
    }

    @NotNull
    public List<String> readFieldFromZipFiles(final Path path, final String fileName, final String filter)
                    throws IOException {
        final List<Path> zipPaths = PathsExtensionFinder.build().findPaths(path.toString(), ZIP_FILES_SUFFIX);
        return zipPaths.stream().map(zipPath -> {
            return searchForLineInZip(zipPath, fileName, filter);
        }).filter(line -> line != null).collect(toList());
    }

    @NotNull
    private List<String> readFileFromZip(@NotNull final String path, @NotNull final String fileName) {
        final List<String> fileLines = new ArrayList<>();
        try {
            fileLines.addAll(FileInZipsReader.build().readLines(path.toString(), fileName));
        } catch (IOException | HealthChecksException e) {
            LOGGER.error(String.format("Error occurred when reading file %s. Will return empty stream. Error -> %s",
                            path, e.getMessage()));
        }
        return fileLines;
    }

    @NotNull
    private String searchForLineInZip(@NotNull final Path path, @NotNull final String fileName,
                    @NotNull final String filter) {
        String searchedLine = null;
        final Optional<String> optinalValue = readFileFromZip(path.toString(), fileName).stream()
                        .filter(line -> line.contains(filter)).findFirst();
        if (optinalValue.isPresent()) {
            searchedLine = optinalValue.get();
        }
        return searchedLine;
    }

}
