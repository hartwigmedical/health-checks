package com.hartwig.healthchecks.nesbit.extractor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.reader.Reader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.SampleReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class SomaticExtractor extends AbstractVCFExtractor {

    private static final String EXT = "_Cosmicv76.vcf";

    private final Reader reader;

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
        validateHeader(headerLine, EXT);
        return Arrays.asList(null, null);
    }
}
