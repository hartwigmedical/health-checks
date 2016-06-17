package com.hartwig.healthchecks.boggs.extractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

public class BoggsExtractor {
    private static final Logger LOGGER = LogManager.getLogger(BoggsExtractor.class);

    protected static final String FILENAME = "Filename";
    protected static final String SEPERATOR_REGEX = "\t";
    protected static final String TOTAL_SEQUENCES = "Total Sequences";
    protected static final String MAPPING = "mapping";
    protected static final String FASTQC_DATA_FILE_NAME = "fastqc_data.txt";
    protected static final String FILE_NOT_FOUND = "File %s was not found";

    protected Long sumOfTotalSequences(@NotNull final String runDirectory) throws IOException {
        final List<Path> fastqcFiles = Files.walk(new File(runDirectory).toPath())
                .filter(p -> p.getFileName().toString().contains(FASTQC_DATA_FILE_NAME)).sorted()
                .collect(toCollection(ArrayList<Path>::new));

        return fastqcFiles.stream().map(path -> {
             Stream<String> fileLines = Stream.empty();
            try {
                fileLines = Files.lines(path);
            } catch (IOException e) {
                LOGGER.error(String.format("Error occurred when reading file. Will return empty stream. Error -> %s",
                        e.getMessage()));
            }
            return fileLines.collect(toList());
        }).flatMap(Collection::stream).filter(line -> line.contains(TOTAL_SEQUENCES)).map(line -> {
            final String[] values = line.split(SEPERATOR_REGEX);
            return values[1];
        }).collect(toList()).stream().mapToLong(Long::parseLong).sum();
    }

}
