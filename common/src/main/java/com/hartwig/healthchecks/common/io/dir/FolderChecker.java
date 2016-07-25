package com.hartwig.healthchecks.common.io.dir;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.EmptyFolderException;
import com.hartwig.healthchecks.common.exception.FolderDoesNotExistException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.NotFolderException;

@FunctionalInterface
public interface FolderChecker {

    @NotNull
    String checkFolder(@NotNull final String directory) throws IOException, HealthChecksException;

    @NotNull
    static FolderChecker build() {
        return (directory) -> {
            final File folder = new File(directory);
            checkIfDirectoryExist(folder);
            checkIfIsDirectory(folder);
            checkIfDirectoryIsEmpty(folder);
            return folder.getPath();
        };
    }

    static void checkIfDirectoryExist(final File folder) throws FolderDoesNotExistException {
        if (!folder.exists()) {
            throw new FolderDoesNotExistException(folder.toPath().toString());
        }
    }

    static void checkIfIsDirectory(final File folder) throws NotFolderException {
        if (!folder.isDirectory()) {
            throw new NotFolderException(folder.toPath().toString());
        }
    }

    static void checkIfDirectoryIsEmpty(final File directory) throws IOException, EmptyFolderException {
        final Path path = directory.toPath();
        if (!isDirectoryNotEmpty(path)) {
            throw new EmptyFolderException(path.toString());
        }
    }

    static boolean isDirectoryNotEmpty(final Path path) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, getHiddenFilesFilter())) {
            return directoryStream.iterator().hasNext();
        }
    }

    static DirectoryStream.Filter<Path> getHiddenFilesFilter() {
        return new DirectoryStream.Filter<Path>() {

            @Override
            public boolean accept(final Path entry) throws IOException {
                return !Files.isHidden(entry);
            }
        };
    }
}
