package com.hartwig.healthchecks.nesbit.extractor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hartwig.healthchecks.common.exception.HeaderNotFoundException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.extractor.AbstractDataExtractor;
import com.hartwig.healthchecks.nesbit.model.VCFType;

import org.jetbrains.annotations.NotNull;

abstract class AbstractVCFExtractor extends AbstractDataExtractor {

    static final int PATIENT_TUM_INDEX = 10;
    static final int PATIENT_REF_INDEX = 9;
    static final int INFO_INDEX = 7;
    static final int ALT_INDEX = 4;
    static final int REF_INDEX = 3;

    private final String[] neededHeaders = { "FILTER", "REF", "ALT", "INFO", "(CPCT)(\\d+)(T)" };
    private final String[] neededHeadersVariants = { "(CPCT)(\\d+)(R)" };

    @NotNull
    String[] getHeaders(@NotNull final List<String> lines, @NotNull final String extension,
            final boolean isGermlineCheck) throws LineNotFoundException, HeaderNotFoundException {
        final String[] headers = lines.get(ZERO).split(SEPARATOR_REGEX);

        List<String> expectedHeaders = Arrays.stream(neededHeaders).collect(Collectors.toList());
        if (isGermlineCheck) {
            expectedHeaders = Stream.concat(Arrays.stream(neededHeaders),
                    Arrays.stream(neededHeadersVariants)).collect(Collectors.toList());
        }
        final List<String> validation = expectedHeaders.stream().filter(
                expectedHeader -> Arrays.stream(headers).filter(header -> header.matches(expectedHeader)).count()
                        < ONE).collect(Collectors.toList());
        if (!validation.isEmpty()) {
            final String missingHeaders = validation.stream().map(Object::toString).collect(
                    Collectors.joining(COMMA_DELIMITER));
            throw new HeaderNotFoundException(String.format(HEADER_NOT_FOUND_ERROR, extension, missingHeaders));
        }
        return headers;
    }

    @NotNull
    static String getSampleIdFromHeader(@NotNull final String[] headers, @NotNull final String suffix) {
        return Arrays.stream(headers).filter(
                header -> header.startsWith(SAMPLE_PREFIX) && header.endsWith(suffix)).findFirst().get();
    }

    @NotNull
    static VCFType getVCFType(@NotNull final String ref, @NotNull final String alt) {
        VCFType type = VCFType.INDELS;
        if (ref.length() == alt.length()) {
            type = VCFType.SNP;
        }
        return type;
    }
}
