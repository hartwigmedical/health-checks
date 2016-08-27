package com.hartwig.healthchecks.roz.extractor;

import java.io.IOException;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.data.BaseResult;
import com.hartwig.healthchecks.common.data.SingleValueResult;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.reader.ExtensionFinderAndLineReader;
import com.hartwig.healthchecks.common.predicate.VCFDataLinePredicate;

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
    public BaseResult extract()
            throws IOException, HealthChecksException {
        final long value = reader.readLines(runContext.runDirectory(), SLICED_VCF_EXTENSION,
                new VCFDataLinePredicate()).stream().count();
        HealthCheck sampleData = new HealthCheck(runContext.refSample(),
                SlicedCheck.SLICED_NUMBER_OF_VARIANTS.toString(), String.valueOf(value));
        sampleData.log(LOGGER);
        return new SingleValueResult(CheckType.SLICED, sampleData);
    }
}
