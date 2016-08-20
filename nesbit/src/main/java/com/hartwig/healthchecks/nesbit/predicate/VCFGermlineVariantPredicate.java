package com.hartwig.healthchecks.nesbit.predicate;

import java.util.function.Predicate;

import com.hartwig.healthchecks.nesbit.model.VCFGermlineData;
import com.hartwig.healthchecks.nesbit.model.VCFType;

import org.jetbrains.annotations.NotNull;

public class VCFGermlineVariantPredicate implements Predicate<VCFGermlineData> {

    private static final String INVALID_2 = "0/0";
    private static final String INVALID = "./.";

    @NotNull
    private final VCFType vcfType;
    private final boolean refSample;

    public VCFGermlineVariantPredicate(@NotNull final VCFType vcfType, final boolean refSample) {
        super();
        this.vcfType = vcfType;
        this.refSample = refSample;
    }

    @Override
    public boolean test(@NotNull final VCFGermlineData vcfGermlineData) {
        boolean isVariant = false;
        if (vcfGermlineData.getType() == vcfType) {
            String dataToCheck = vcfGermlineData.getRefData();

            if (!refSample) {
                dataToCheck = vcfGermlineData.getTumData();
            }
            if (!dataToCheck.startsWith(INVALID) && !dataToCheck.startsWith(INVALID_2)) {
                isVariant = true;
            }
        }
        return isVariant;
    }
}
