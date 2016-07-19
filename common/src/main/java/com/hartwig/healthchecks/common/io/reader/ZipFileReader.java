package com.hartwig.healthchecks.common.io.reader;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ZipFileReader {

    private static final Logger LOGGER = LogManager.getLogger(ZipFileReader.class);

    private static final String ZIP_FILES_SUFFIX = ".zip";

    @NotNull
    public List<String> readAllLinesFromZips(final Path path, final String fileName) throws IOException {
        final List<Path> zipPaths = getZipFilesPaths(path);
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
        final List<Path> zipPaths = getZipFilesPaths(path);
        return zipPaths.stream().map(zipPath -> {
            return searchForLineInZip(zipPath, fileName, filter);
        }).filter(line -> line != null).collect(toList());
    }

    private List<Path> getZipFilesPaths(final Path path) throws IOException {
        return Files.walk(path).filter(filePath -> filePath.getFileName().toString().endsWith(ZIP_FILES_SUFFIX))
                        .sorted().collect(toCollection(ArrayList<Path>::new));
    }

    @NotNull
    private List<String> readFileFromZip(@NotNull final String path, @NotNull final String fileName) {
        final List<String> fileLines = new ArrayList<>();
        try (final ZipFile zipFile = new ZipFile(path)) {
            final Predicate<ZipEntry> isFile = zipEntry -> !zipEntry.isDirectory();
            final Predicate<ZipEntry> isFastQC = zipEntry -> zipEntry.getName().contains(fileName);
            final List<String> values = zipFile.stream().filter(isFile.and(isFastQC)).map(zipElement -> {
                Stream<String> readLines = Stream.empty();
                try {
                    final InputStream inputStream = zipFile.getInputStream(zipElement);
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    readLines = reader.lines();
                } catch (final IOException e) {
                    LOGGER.error(String.format(
                                    "Error occurred when reading file. Will return empty stream. Error -> %s",
                                    e.getMessage()));
                }
                return readLines.collect(toList());
            }).flatMap(Collection::stream).collect(toList());

            if (values != null) {
                fileLines.addAll(values);
            }
        } catch (final IOException e) {
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
