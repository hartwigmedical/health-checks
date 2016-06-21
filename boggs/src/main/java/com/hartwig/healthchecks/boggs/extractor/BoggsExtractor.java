package com.hartwig.healthchecks.boggs.extractor;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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

public class BoggsExtractor {
    private static final Logger LOGGER = LogManager.getLogger(BoggsExtractor.class);

    protected static final String FILENAME = "Filename";
    protected static final String SEPERATOR_REGEX = "\t";
    protected static final String TOTAL_SEQUENCES = "Total Sequences";
    protected static final String MAPPING = "mapping";
    protected static final String FASTQC_DATA_FILE_NAME = "fastqc_data.txt";
    protected static final String ZIP_FILES_SUFFIX = ".zip";

    protected static final String FILE_NOT_FOUND = "File %s was not found";
    protected static final String SAMPLE_PREFIX = "CPCT";
    protected static final String REF_SAMPLE_SUFFIX = "R";
    protected static final String EMPTY_FILES_ERROR = "Found empty Summary files under path -> %s";
    protected static final String FILE_NOT_FOUND_ERROR = "File with prefix %s and suffix %s was not found in path %s";

    protected Optional<Path> getFilesPath(@NotNull final String runDirectory, @NotNull final String prefix,
            @NotNull final String suffix) throws IOException, FileNotFoundException {
        final Optional<Path> filePath = Files.walk(new File(runDirectory).toPath())
                .filter(path -> path.getFileName().toString().startsWith(prefix)
                        && path.getFileName().toString().endsWith(suffix))
                .findFirst();
        if (!filePath.isPresent()) {
            throw new FileNotFoundException(String.format(FILE_NOT_FOUND_ERROR, prefix, suffix, runDirectory));
        }
        return filePath;
    }

    protected Long sumOfTotalSequences(@NotNull final Path path) throws IOException {
        final List<Path> zipFiles = Files.walk(path)
                .filter(filePath -> filePath.getFileName().toString().endsWith(ZIP_FILES_SUFFIX)).sorted()
                .collect(toCollection(ArrayList<Path>::new));
        final List<String> allValues = zipFiles.stream().map(zipPath -> {
            return this.getLineFromFile(zipPath, FASTQC_DATA_FILE_NAME, TOTAL_SEQUENCES);
        }).map(line -> {
            String totalSequences = null;
            if (line != null) {
                final String[] values = line.split(SEPERATOR_REGEX);
                totalSequences = values[1];
            }
            return totalSequences;
        }).filter(lines -> lines != null).collect(toList());

        return allValues.stream().mapToLong(Long::parseLong).sum();
    }

    protected String getLineFromFile(@NotNull final Path path, @NotNull final String fileName,
            @NotNull final String filter) {
        String searchedLine = null;
        final Optional<String> optinalValue = this.getLinesFromFile(path, fileName).stream()
                .filter(line -> line.contains(filter)).findFirst();
        if (optinalValue.isPresent()) {
            searchedLine = optinalValue.get();
        }
        return searchedLine;
    }

    protected List<String> getLinesFromFile(@NotNull final Path path, @NotNull final String fileName) {
        final List<String> fileLines = new ArrayList<>();
        try (final ZipFile zipFile = new ZipFile(path.toString())) {
            final Predicate<ZipEntry> isFile = zipEntry -> !zipEntry.isDirectory();
            final Predicate<ZipEntry> isFastQC = zipEntry -> zipEntry.getName().contains(fileName);
            final List<String> values = zipFile.stream().filter(isFile.and(isFastQC)).map(zipElement -> {
                Stream<String> readLines = Stream.empty();
                try {
                    InputStream inputStream = zipFile.getInputStream(zipElement);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    readLines = reader.lines();
                } catch (IOException e) {
                    LOGGER.error(String.format(
                            "Error occurred when reading file. Will return empty stream. Error -> %s", e.getMessage()));
                }
                return readLines.collect(toList());
            }).flatMap(Collection::stream).collect(toList());

            if (values != null) {
                fileLines.addAll(values);
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Error occurred when reading file %s. Will return empty stream. Error -> %s",
                    path, e.getMessage()));
        }
        return fileLines;
    }
}
