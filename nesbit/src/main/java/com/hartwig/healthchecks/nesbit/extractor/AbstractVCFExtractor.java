package com.hartwig.healthchecks.nesbit.extractor;

import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.nesbit.model.VCFType;

import org.jetbrains.annotations.NotNull;

abstract class AbstractVCFExtractor implements DataExtractor {

    static final int TUMOR_SAMPLE_INDEX = 10;
    static final int REF_SAMPLE_INDEX = 9;
    static final int INFO_INDEX = 7;
    static final int ALT_INDEX = 4;
    static final int REF_INDEX = 3;

    @NotNull
    static VCFType getVCFType(@NotNull final String ref, @NotNull final String alt) {
        VCFType type = VCFType.INDELS;
        if (ref.length() == alt.length()) {
            type = VCFType.SNP;
        }
        return type;
    }
}
