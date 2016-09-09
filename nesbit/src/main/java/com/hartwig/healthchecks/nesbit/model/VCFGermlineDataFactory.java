package com.hartwig.healthchecks.nesbit.model;

import org.jetbrains.annotations.NotNull;

public final class VCFGermlineDataFactory {

    private static final String VCF_COLUMN_SEPARATOR = "\t";
    private static final int TUMOR_SAMPLE_COLUMN = 10;
    private static final int REF_SAMPLE_COLUMN = 9;

    private VCFGermlineDataFactory() {
    }

    @NotNull
    public static VCFGermlineData fromVCFLine(@NotNull final String line) {
        final String[] values = line.split(VCF_COLUMN_SEPARATOR);
        final VCFType type = VCFExtractorFunctions.extractVCFType(values);
        final String refData = values[REF_SAMPLE_COLUMN];
        final String tumData = values[TUMOR_SAMPLE_COLUMN];

        return new VCFGermlineData(type, refData, tumData);
    }
}
