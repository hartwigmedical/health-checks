package com.hartwig.healthchecks.nesbit.extractor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hartwig.healthchecks.common.exception.HeaderNotFoundException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.extractor.AbstractDataExtractor;
import com.hartwig.healthchecks.nesbit.model.VCFType;

public abstract class AbstractVCFExtractor extends AbstractDataExtractor {

    protected static final int PATIENT_TUM_INDEX = 10;

    protected static final int PATIENT_REF_INDEX = 9;

    protected static final int INFO_INDEX = 7;

    protected static final int ALT_INDEX = 4;

    protected static final int REF_INDEX = 3;

    private final String[] neededHeaders = {"FILTER", "REF", "ALT", "INFO", "(CPCT)(\\d+)(T)"};

    private final String[] neededHeadersVariants = {"(CPCT)(\\d+)(R)"};

    protected String[] getHeaders(final List<String> lines, final String extension, final boolean isGermlineCheck)
                    throws LineNotFoundException, HeaderNotFoundException {
        final String[] headers = lines.get(ZERO).split(SEPERATOR_REGEX);

        List<String> expecetedHeaders = Arrays.stream(neededHeaders).collect(Collectors.toList());
        if (isGermlineCheck) {
            expecetedHeaders = Stream.concat(Arrays.stream(neededHeaders), Arrays.stream(neededHeadersVariants))
                            .collect(Collectors.toList());
        }
        final List<String> validation = expecetedHeaders.stream()
                        .filter(expectedHeader -> Arrays.stream(headers)
                                        .filter(header -> header.matches(expectedHeader)).count() < ONE)
                        .collect(Collectors.toList());
        if (!validation.isEmpty()) {
            final String missingHeaders = validation.stream().map(Object::toString)
                            .collect(Collectors.joining(COMMA_DELIMITER));
            throw new HeaderNotFoundException(String.format(HEADER_NOT_FOUND_ERROR, extension, missingHeaders));
        }
        return headers;
    }

    protected String getPatientIdFromHeader(final String[] headers, final String suffix) {
        return Arrays.stream(headers).filter(header -> header.startsWith(SAMPLE_PREFIX) && header.endsWith(suffix))
                        .findFirst().get();
    }

    protected VCFType getVCFType(final String ref, final String alt) {
        VCFType type = VCFType.INDELS;
        if (ref.length() == alt.length()) {
            type = VCFType.SNP;
        }
        return type;
    }
}
