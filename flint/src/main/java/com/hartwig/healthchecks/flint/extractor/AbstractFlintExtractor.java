package com.hartwig.healthchecks.flint.extractor;

import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.SEPARATOR_REGEX;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;

import org.jetbrains.annotations.NotNull;

abstract class AbstractFlintExtractor implements DataExtractor {

    static final String PICARD_SAMPLE_IDENTIFIER = "INPUT";

    private static final String SPACE = " ";
    private static final String EQUAL_REGEX = "=";

    private static final String BAM_EXT = "_dedup.bam";

    @NotNull
    static String getSampleId(@NotNull final String suffix, @NotNull final List<String> lines,
            @NotNull final String filter) throws LineNotFoundException {
        final int index = findLineIndex(suffix, lines, filter);
        final Optional<String> optValue = Arrays.stream(lines.get(index).split(SPACE)).filter(
                line -> line.contains(filter)).map(inputLine -> {
            final String[] values = inputLine.split(EQUAL_REGEX);
            return values[1];
        }).findFirst();

        if (optValue.isPresent()) {
            final String value = optValue.get();
            return value.substring(value.lastIndexOf(File.separator) + 1, value.indexOf(BAM_EXT));
        }

        // KODU: Not sure this makes sense or could ever happen...
        throw new IllegalStateException("No sample ID found");
    }

    @NotNull
    static String getValueFromLine(@NotNull final List<String> lines, @NotNull final String suffix,
            @NotNull final String filter, final int fieldIndex) throws LineNotFoundException {
        final int index = findLineIndex(suffix, lines, filter);
        final String line = lines.get(index + 1);
        final String[] lineValues = line.split(SEPARATOR_REGEX);
        return lineValues[fieldIndex];
    }

    private static int findLineIndex(@NotNull final String suffix, @NotNull final List<String> lines,
            @NotNull final String filter) throws LineNotFoundException {
        final Optional<Integer> lineNumbers = IntStream.range(0, lines.size()).filter(
                index -> lines.get(index).contains(filter)).mapToObj(index -> index).findFirst();
        if (!lineNumbers.isPresent()) {
            throw new LineNotFoundException(suffix, filter);
        }
        return lineNumbers.get();
    }
}
