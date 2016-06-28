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
import com.hartwig.healthchecks.common.extractor.AbstractDataExtractor;

public abstract class AbstractBoggsExtractor extends AbstractDataExtractor {

    protected static final String QC_STATS = "QCStats";

    protected static final String TOTAL_SEQUENCES = "Total Sequences";

    protected static final String MAPPING = "mapping";

    protected static final String FASTQC_DATA_FILE_NAME = "fastqc_data.txt";

    @NotNull
    protected Long sumOfTotalSequences(@NotNull final Path path, final ZipFileReader zipFileReader) throws IOException {
        final Path totalSequencesPath = new File(path + File.separator + QC_STATS + File.separator).toPath();
        final List<Path> zipFiles = Files.walk(totalSequencesPath)
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
