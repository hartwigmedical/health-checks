package com.hartwig.healthchecks.common.io.extractor;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.io.reader.ZipFileReader;

public abstract class AbstractTotalSequenceExtractor extends AbstractDataExtractor {

    protected static final String QC_STATS = "QCStats";

    protected static final String TOTAL_SEQUENCES = "Total Sequences";

    protected static final String MAPPING = "mapping";

    protected static final String FASTQC_DATA_FILE_NAME = "fastqc_data.txt";

    @NotNull
    protected Long sumOfTotalSequences(@NotNull final Path path, final ZipFileReader zipFileReader) throws IOException {
        final Path fastqcDataPath = new File(path + File.separator + QC_STATS + File.separator).toPath();
        final List<String> allLines = zipFileReader.readFieldFromZipFiles(fastqcDataPath, FASTQC_DATA_FILE_NAME,
                        TOTAL_SEQUENCES);

        final List<String> allValues = allLines.stream().map(line -> {
            String totalSequences = null;
            if (line != null) {
                final String[] values = line.split(SEPERATOR_REGEX);
                totalSequences = values[1];
            }
            return totalSequences;
        }).filter(lines -> lines != null).collect(toList());

        return allValues.stream().mapToLong(Long::parseLong).sum();
    }
}