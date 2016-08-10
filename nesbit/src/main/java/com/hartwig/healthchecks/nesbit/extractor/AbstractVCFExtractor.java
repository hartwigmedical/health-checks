package com.hartwig.healthchecks.nesbit.extractor;

import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.SAMPLE_PREFIX;
import static com.hartwig.healthchecks.common.io.extractor.ExtractorConstants.SEPARATOR_REGEX;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hartwig.healthchecks.common.exception.HeaderNotFoundException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.nesbit.model.VCFType;

import org.jetbrains.annotations.NotNull;

abstract class AbstractVCFExtractor implements DataExtractor {

    private static final String HEADER_NOT_FOUND_ERROR = "File %s does not contain following headers %s";
    private static final String[] NEEDED_HEADERS = { "FILTER", "REF", "ALT", "INFO", "(CPCT)(\\d+)(T)" };
    private static final String[] NEEDED_HEADERS_VARIANTS = { "(CPCT)(\\d+)(R)" };
    private static final String COMMA_DELIMITER = ",";

    static final int PATIENT_TUM_INDEX = 10;
    static final int PATIENT_REF_INDEX = 9;
    static final int INFO_INDEX = 7;
    static final int ALT_INDEX = 4;
    static final int REF_INDEX = 3;

    @NotNull
    String[] getHeaders(@NotNull final List<String> lines, @NotNull final String extension,
            final boolean isGermlineCheck) throws LineNotFoundException, HeaderNotFoundException {
        final String[] headers = lines.get(0).split(SEPARATOR_REGEX);

        List<String> expectedHeaders = Arrays.stream(NEEDED_HEADERS).collect(Collectors.toList());
        if (isGermlineCheck) {
            expectedHeaders = Stream.concat(Arrays.stream(NEEDED_HEADERS),
                    Arrays.stream(NEEDED_HEADERS_VARIANTS)).collect(Collectors.toList());
        }
        final List<String> validation = expectedHeaders.stream().filter(
                expectedHeader -> Arrays.stream(headers).filter(header -> header.matches(expectedHeader)).count()
                        < 1).collect(Collectors.toList());
        if (!validation.isEmpty()) {
            final String missingHeaders = validation.stream().map(Object::toString).collect(
                    Collectors.joining(COMMA_DELIMITER));
            throw new HeaderNotFoundException(String.format(HEADER_NOT_FOUND_ERROR, extension, missingHeaders));
        }
        return headers;
    }

    @NotNull
    static String getSampleIdFromHeader(@NotNull final String[] headers, @NotNull final String suffix) {
        Optional<String> sampleId = Arrays.stream(headers).filter(
                header -> header.startsWith(SAMPLE_PREFIX) && header.endsWith(suffix)).findFirst();

        assert sampleId.isPresent();
        return sampleId.get();
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
