package com.hartwig.healthchecks.common.io.reader;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;

@FunctionalInterface
public interface FileInZipsReader {

    String FILE_S_NOT_FOUND_IN_S = "File %s not found in %s";

    String ERROR_MSG = "Error occurred when" + " reading file. Will return empty stream. Error -> %s";

    Logger LOGGER = LogManager.getLogger(FileInZipsReader.class);

    @NotNull
    List<String> readLines(@NotNull final String zipPath, @NotNull final String fileNameInZip)
                    throws IOException, HealthChecksException;

    @NotNull
    static FileInZipsReader build() {
        return (zipPath, fileNameInZip) -> {
            final List<String> fileLines = read(zipPath, fileNameInZip);
            if (fileLines.isEmpty()) {
                throw new EmptyFileException(fileNameInZip, zipPath);
            }
            return fileLines;

        };
    }

    static List<String> read(final String zipPath, final String fileNameInZip) throws IOException {

        try (final ZipFile zipFile = new ZipFile(zipPath)) {
            final List<? extends ZipEntry> fileEntryInZip = findFileInZip(zipFile, fileNameInZip);
            return fileEntryInZip.stream().map(zipElement -> {
                Stream<String> readLines = Stream.empty();
                try {
                    final InputStream inputStream = zipFile.getInputStream(zipElement);
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    readLines = reader.lines();
                } catch (final IOException e) {
                    LOGGER.error(String.format(ERROR_MSG, e.getMessage()));
                }
                return readLines.filter(line -> line != null && !line.isEmpty()).collect(toList());
            }).flatMap(Collection::stream).collect(toList());
        }
    }

    static List<? extends ZipEntry> findFileInZip(final ZipFile zipFile, final String fileNameInZip)
                    throws FileNotFoundException {
        final Predicate<ZipEntry> isFile = zipEntry -> !zipEntry.isDirectory();
        final Predicate<ZipEntry> isFileName = zipEntry -> zipEntry.getName().contains(fileNameInZip);
        final List<? extends ZipEntry> fileInZip = zipFile.stream().filter(isFile.and(isFileName)).collect(toList());
        if (fileInZip.isEmpty()) {
            throw new FileNotFoundException(String.format(FILE_S_NOT_FOUND_IN_S, fileNameInZip, zipFile.getName()));
        }
        return fileInZip;
    }

}
