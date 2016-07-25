package com.hartwig.healthchecks.common.io.reader;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;

@FunctionalInterface
public interface FileInZipsReader {

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
            final List<? extends ZipEntry> fileEntryInZip = FileInZipsFinder.build().findFileInZip(zipFile,
                            fileNameInZip);
            return fileEntryInZip.stream().map(zipElement -> {
                return ZipEntryReader.build().readZipElement(zipFile, zipElement).collect(toList());
            }).flatMap(Collection::stream).collect(toList());
        }
    }

}
