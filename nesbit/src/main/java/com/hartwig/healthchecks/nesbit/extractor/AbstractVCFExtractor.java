package com.hartwig.healthchecks.nesbit.extractor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.hartwig.healthchecks.common.exception.HeaderNotFoundException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.extractor.AbstractDataExtractor;
import com.hartwig.healthchecks.nesbit.model.VCFData;
import com.hartwig.healthchecks.nesbit.model.VCFType;

public abstract class AbstractVCFExtractor extends AbstractDataExtractor {

    protected static final String CHROM = "#CHROM";

    private static final int INFO_INDEX = 7;

    private static final int ALT_INDEX = 4;

    private static final int REF_INDEX = 3;

    private static final int FILTER_INDEX = 6;

    private final String[] neededHeaders = {"REF", "ALT", "INFO", "T", "R"};

    protected void validateHeader(final Optional<String> headerLine, final String extension)
                    throws LineNotFoundException, HeaderNotFoundException {
        if (!headerLine.isPresent()) {
            throw new LineNotFoundException(String.format(LINE_NOT_FOUND_ERROR, extension, CHROM));
        }
        final String[] headers = headerLine.get().split(SEPERATOR_REGEX);
        final List<String> validation = Arrays.stream(neededHeaders)
                        .filter(expectedHeader -> Arrays.stream(headers)
                                        .filter(header -> header.endsWith(expectedHeader)).count() < 1)
                        .collect(Collectors.toList());
        if (!validation.isEmpty()) {
            final String missingHeaders = validation.stream().map(Object::toString)
                            .collect(Collectors.joining(COMMA_DELIMITER));
            throw new HeaderNotFoundException(String.format(HEADER_NOT_FOUND_ERROR, extension, missingHeaders));
        }
    }

    protected List<VCFData> getVCFData(final List<String> lines) {
        return lines.stream().filter(line -> !line.startsWith(HASH)).map(line -> {
            final String[] values = line.split(SEPERATOR_REGEX);
            VCFData vcfData = null;
            final String filterValue = values[FILTER_INDEX];
            if (filterValue.contains(PASS) || filterValue.contains(DOT)) {
                final String ref = values[REF_INDEX];
                final String alt = values[ALT_INDEX];
                final String info = values[INFO_INDEX];
                VCFType type = VCFType.INDEL;
                if (ref.length() == alt.length()) {
                    type = VCFType.SNP;
                }
                vcfData = new VCFData(type, ref, alt, info);
            }
            return vcfData;
        }).filter(vcfData -> vcfData != null).collect(Collectors.toList());
    }
}
