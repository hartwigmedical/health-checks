package com.hartwig.healthchecks.boggs.extractor;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boggs.reader.ZipFileReader;

public class BoggsExtractor {

    protected static final String FILENAME = "Filename";

    protected static final String SEPERATOR_REGEX = "\t";

    protected static final String TOTAL_SEQUENCES = "Total Sequences";

    protected static final String MAPPING = "mapping";

    protected static final String FASTQC_DATA_FILE_NAME = "fastqc_data.txt";

    protected static final String ZIP_FILES_SUFFIX = ".zip";

    protected static final String FILE_NOT_FOUND = "File %s was not found";

    protected static final String SAMPLE_PREFIX = "CPCT";

    protected static final String REF_SAMPLE_SUFFIX = "R";

    protected static final String TUM_SAMPLE_SUFFIX = "T";

    protected static final String EMPTY_FILES_ERROR = "Found empty Summary files under path -> %s";

    protected static final String FILE_NOT_FOUND_ERROR = "File with prefix %s and suffix %s was not found in path %s";

    @NotNull
    protected Optional<Path> getFilesPath(@NotNull final String runDirectory, @NotNull final String prefix,
                    @NotNull final String suffix) throws IOException {
        final Optional<Path> filePath = Files.walk(new File(runDirectory).toPath())
                        .filter(path -> path.getFileName().toString().startsWith(prefix)
                                        && path.getFileName().toString().endsWith(suffix))
                        .findFirst();
        return filePath;
    }

    protected Long sumOfTotalSequences(@NotNull final Path path, final ZipFileReader zipFileReader) throws IOException {
        final List<Path> zipFiles = Files.walk(path)
                        .filter(filePath -> filePath.getFileName().toString().endsWith(ZIP_FILES_SUFFIX)).sorted()
                        .collect(toCollection(ArrayList<Path>::new));
        final List<String> allValues = zipFiles.stream().map(zipPath -> {
            return getLineFromFile(zipPath, FASTQC_DATA_FILE_NAME, TOTAL_SEQUENCES, zipFileReader);
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

    @NotNull
    protected String getLineFromFile(@NotNull final Path path, @NotNull final String fileName,
                    @NotNull final String filter, final ZipFileReader zipFileReader) {
        String searchedLine = null;
        final Optional<String> optinalValue = zipFileReader.readFileFromZip(path.toString(), fileName).stream()
                        .filter(line -> line.contains(filter)).findFirst();
        if (optinalValue.isPresent()) {
            searchedLine = optinalValue.get();
        }
        return searchedLine;
    }
}
