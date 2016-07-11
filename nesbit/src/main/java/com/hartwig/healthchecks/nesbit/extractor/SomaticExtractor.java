package com.hartwig.healthchecks.nesbit.extractor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HeaderNotFoundException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.extractor.AbstractDataExtractor;
import com.hartwig.healthchecks.common.io.reader.Reader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.SampleReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class SomaticExtractor extends AbstractDataExtractor {

    private static final String CHROM = "#CHROM";

    private static final String EXT = "_Cosmicv76.vcf";

    private final Reader reader;

    private final String[] headers = {"T.mutect", "T.freebayes", "TUMOR.strelka", "TUMOR.varscan"};

    public SomaticExtractor(final Reader reader) {
        super();
        this.reader = reader;
    }

    @Override
    public BaseReport extractFromRunDirectory(final String runDirectory) throws IOException, HealthChecksException {
        getPatientData(runDirectory);
        return new SampleReport(CheckType.SOMATIC, null, null);
    }

    private List<BaseDataReport> getPatientData(final String runDirectory) throws IOException, HealthChecksException {
        final List<String> lines = reader.readLines(runDirectory, EXT);
        if (lines.isEmpty()) {
            throw new EmptyFileException(String.format(EMPTY_FILES_ERROR, EXT, runDirectory));
        }

        final Optional<String> headerLine = lines.stream().filter(line -> line.contains(CHROM)).findFirst();
        validateHeader(headerLine);
        return Arrays.asList(null, null);
    }

    private void validateHeader(final Optional<String> headerLine)
                    throws LineNotFoundException, HeaderNotFoundException {
        if (!headerLine.isPresent()) {
            throw new LineNotFoundException(String.format(LINE_NOT_FOUND_ERROR, EXT, CHROM));
        }

        final List<String> validation = Arrays.stream(headers).filter(header -> !headerLine.get().contains(header))
                        .collect(Collectors.toList());
        if (!validation.isEmpty()) {
            final String missingHeader = validation.stream().map(Object::toString).collect(Collectors.joining(","));
            throw new HeaderNotFoundException(String.format(HEADER_NOT_FOUND_ERROR, EXT, missingHeader));
        }
    }

}
