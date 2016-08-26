package com.hartwig.healthchecks.roz.extractor;

import java.io.IOException;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.reader.ExtensionFinderAndLineReader;
import com.hartwig.healthchecks.common.predicate.VCFDataLinePredicate;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SingleValueReport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class SlicedExtractor implements DataExtractor {

    private static final Logger LOGGER = LogManager.getLogger(SlicedExtractor.class);

    private static final String SLICED_VCF_EXTENSION = "_Cosmicv76_GoNLv5_sliced.vcf";

    @NotNull
    private final ExtensionFinderAndLineReader reader = ExtensionFinderAndLineReader.build();
    @NotNull
    private final RunContext runContext;

    public SlicedExtractor(@NotNull final RunContext runContext) {
        this.runContext = runContext;
    }

    @NotNull
    @Override
    public BaseReport extractFromRunDirectory(@NotNull final String runDirectory)
            throws IOException, HealthChecksException {
        final long value = reader.readLines(runContext.runDirectory(), SLICED_VCF_EXTENSION,
                new VCFDataLinePredicate()).stream().count();
        BaseDataReport sampleData = new BaseDataReport(runContext.refSample(),
                SlicedCheck.SLICED_NUMBER_OF_VARIANTS.toString(), String.valueOf(value));
        sampleData.log(LOGGER);
        return new SingleValueReport(CheckType.SLICED, sampleData);
    }
}
