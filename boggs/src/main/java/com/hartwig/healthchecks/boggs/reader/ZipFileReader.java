package com.hartwig.healthchecks.boggs.reader;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ZipFileReader {

    private static final Logger LOGGER = LogManager.getLogger(ZipFileReader.class);

    @NotNull
    public List<String> readFileFromZip(@NotNull final String path, @NotNull final String fileName) {
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
}
