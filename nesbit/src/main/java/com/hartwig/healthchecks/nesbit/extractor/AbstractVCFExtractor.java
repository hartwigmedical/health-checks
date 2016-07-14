package com.hartwig.healthchecks.nesbit.extractor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hartwig.healthchecks.common.exception.HeaderNotFoundException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.extractor.AbstractDataExtractor;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.nesbit.model.VCFData;
import com.hartwig.healthchecks.nesbit.model.VCFType;

public abstract class AbstractVCFExtractor extends AbstractDataExtractor {

    protected static final String CHROM = "#CHROM";

    private static final int INFO_INDEX = 7;

    private static final int ALT_INDEX = 4;

    private static final int REF_INDEX = 3;

    private static final int FILTER_INDEX = 6;

    private final String[] neededHeaders = {"FILTER", "REF", "ALT", "INFO", "(CPCT)(\\d+)(T)"};

    private final String[] neededHeadersVariants = {"(CPCT)(\\d+)(R)"};

    protected String getHeaders(final List<String> lines, final String extension, final boolean isVariantsCheck)
                    throws LineNotFoundException, HeaderNotFoundException {
        final Optional<String> headerLine = lines.stream().filter(line -> line.contains(CHROM)).findFirst();

        if (!headerLine.isPresent()) {
            throw new LineNotFoundException(String.format(LINE_NOT_FOUND_ERROR, extension, CHROM));
        }
        final String[] headers = headerLine.get().split(SEPERATOR_REGEX);

        List<String> expecetedHeaders = Arrays.stream(neededHeaders).collect(Collectors.toList());
        if (isVariantsCheck) {
            expecetedHeaders = Stream.concat(Arrays.stream(neededHeaders), Arrays.stream(neededHeadersVariants))
                            .collect(Collectors.toList());
        }
        final List<String> validation = expecetedHeaders.stream()
                        .filter(expectedHeader -> Arrays.stream(headers)
                                        .filter(header -> header.matches(expectedHeader)).count() < 1)
                        .collect(Collectors.toList());
        if (!validation.isEmpty()) {
            final String missingHeaders = validation.stream().map(Object::toString)
                            .collect(Collectors.joining(COMMA_DELIMITER));
            throw new HeaderNotFoundException(String.format(HEADER_NOT_FOUND_ERROR, extension, missingHeaders));
        }
        return headerLine.get();
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
                VCFType type = VCFType.INDELS;
                if (ref.length() == alt.length()) {
                    type = VCFType.SNP;
                }
                vcfData = new VCFData(type, ref, alt, info);
            }
            return vcfData;
        }).filter(vcfData -> vcfData != null).collect(Collectors.toList());
    }

    protected String getPatientIdFromHeader(final String[] headers, final String suffix) {
        return Arrays.stream(headers).filter(header -> header.startsWith(SAMPLE_PREFIX) && header.endsWith(suffix))
                        .findFirst().get();
    }

    protected BaseDataReport getCountCheck(final String patientId, final List<VCFData> vcfData, final VCFType vcfType,
                    final String checkName) {
        final Long count = vcfData.stream().filter(data -> data.getType().equals(vcfType)).count();
        final BaseDataReport dataReport = new BaseDataReport(patientId, checkName, String.valueOf(count));
        logBaseDataReport(dataReport);
        return dataReport;
    }
}
