package com.hartwig.healthchecks.flint.extractor;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.extractor.AbstractDataExtractor;

public abstract class AbstractFlintExtractor extends AbstractDataExtractor {

    protected static final String INPUT = "INPUT";

    private static final String SPACE = " ";

    private static final String EQUAL_REGEX = "=";

    private static final String BAM_EXT = "_dedup.bam";

    protected String getPatientId(final String suffix, final List<String> lines, final String filter)
                    throws LineNotFoundException {
        final Integer index = findLineIndex(suffix, lines, filter);
        final String value = Arrays.stream(lines.get(index).split(SPACE)).filter(line -> line.contains(filter))
                        .map(inputLine -> {
                            final String[] values = inputLine.split(EQUAL_REGEX);
                            return values[ONE];
                        }).findFirst().get();
        return value.substring(value.lastIndexOf(File.separator) + ONE, value.indexOf(BAM_EXT));
    }

    protected Integer findLineIndex(final String suffix, final List<String> lines, final String filter)
                    throws LineNotFoundException {
        final Optional<Integer> lineNumbers = IntStream.range(0, lines.size())
                        .filter(index -> lines.get(index).contains(filter)).mapToObj(index -> index).findFirst();
        if (!lineNumbers.isPresent()) {
            throw new LineNotFoundException(suffix, filter);
        }
        return lineNumbers.get();
    }

    protected String getValueFromLine(final List<String> lines, final String suffix, final String filter,
                    final int fieldIndex) throws LineNotFoundException {
        final Integer index = findLineIndex(suffix, lines, filter);
        final String line = lines.get(index + ONE);
        final String[] lineValues = line.split(SEPARATOR_REGEX);
        return lineValues[fieldIndex];
    }
}
