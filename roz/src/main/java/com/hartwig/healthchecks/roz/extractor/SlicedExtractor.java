package com.hartwig.healthchecks.roz.extractor;

import java.io.IOException;
import java.util.List;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.extractor.AbstractDataExtractor;
import com.hartwig.healthchecks.common.io.reader.ExtensionFinderAndLineReader;
import com.hartwig.healthchecks.common.predicate.VCFDataLinePredicate;
import com.hartwig.healthchecks.common.predicate.VCFHeaderLinePredicate;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.PatientReport;

import org.jetbrains.annotations.NotNull;

public class SlicedExtractor extends AbstractDataExtractor {

    private static final int SAMPLE_ID_INDEX = 9;
    private static final String SLICED_NUM_VARIANTS = "SLICED_NUMBER_OF_VARIANTS";
    private static final String EXT = "_Cosmicv76_GoNLv5_sliced.vcf";

    @NotNull
    private final ExtensionFinderAndLineReader reader;

    public SlicedExtractor(@NotNull final ExtensionFinderAndLineReader reader) {
        super();
        this.reader = reader;
    }

    @NotNull
    @Override
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final BaseDataReport sampleData = getSampleData(runDirectory);
        logBaseDataReport(sampleData);
        return new PatientReport(CheckType.SLICED, sampleData);
    }

    @NotNull
    private BaseDataReport getSampleData(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final List<String> headerLine = reader.readLines(runDirectory, EXT, new VCFHeaderLinePredicate());

        final String sampleId = headerLine.get(0).split(SEPARATOR_REGEX)[SAMPLE_ID_INDEX];

        final long value = reader.readLines(runDirectory, EXT, new VCFDataLinePredicate()).stream().filter(
                line -> !line.startsWith(HASH)).count();
        return new BaseDataReport(sampleId, SLICED_NUM_VARIANTS, String.valueOf(value));
    }
}
