package com.hartwig.healthchecks.smitty.reader;

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

public class KinshipReader {

    private static final String KINSHIP = ".kinship";

    private static final String FILE_S_NOT_FOUND_MSG = "File %s not Found in path %s";

    @NotNull
    public List<String> readLinesFromKinship(@NotNull final String path) throws IOException {
        final Path kinshipFile = findKinshipInPath(path, KINSHIP);
        return Files.lines(Paths.get(kinshipFile.toString())).collect(toList());
    }

    @NotNull
    private Path findKinshipInPath(@NotNull final String searchPath, @NotNull final String suffix) throws IOException {
        final Optional<Path> searchedFile = Files.walk(new File(searchPath).toPath())
                        .filter(path -> path.getFileName().toString().endsWith(suffix)).findFirst();
        if (!searchedFile.isPresent()) {
            throw new FileNotFoundException(String.format(FILE_S_NOT_FOUND_MSG, suffix, searchPath));
        }
        return searchedFile.get();
    }

}
