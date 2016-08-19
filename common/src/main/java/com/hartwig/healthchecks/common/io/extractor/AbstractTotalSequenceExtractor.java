package com.hartwig.healthchecks.common.io.extractor;

import static java.util.stream.Collectors.toList;

import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.SEPARATOR_REGEX;

import java.io.IOException;
import java.util.List;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.reader.ZipFilesReader;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractTotalSequenceExtractor implements DataExtractor {

    private static final String FASTQC_DATA_FILE_NAME = "fastqc_data.txt";
    private static final String TOTAL_SEQUENCES_PATTERN = "Total Sequences";

    protected static long sumOfTotalSequencesFromFastQC(@NotNull final String basePath,
            @NotNull final ZipFilesReader zipFileReader) throws IOException, HealthChecksException {
        final List<String> allLines = zipFileReader.readFieldFromZipFiles(basePath, FASTQC_DATA_FILE_NAME,
                TOTAL_SEQUENCES_PATTERN);

        final List<String> allValues = allLines.stream().map(line -> {
            String totalSequences = null;
            if (line != null) {
                final String[] values = line.split(SEPARATOR_REGEX);
                totalSequences = values[1];
            }
            return totalSequences;
        }).filter(lines -> lines != null).collect(toList());

        long totalSequences = !allValues.isEmpty() ? allValues.stream().mapToLong(Long::parseLong).sum() : 0L;
        if (totalSequences == 0) {
            throw new EmptyFileException(FASTQC_DATA_FILE_NAME, basePath);
        }
        return totalSequences;
    }
}
