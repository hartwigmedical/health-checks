package com.hartwig.healthchecks.nesbit.model;

import org.jetbrains.annotations.NotNull;

public class VCFGermlineData {

    @NotNull
    private final String refData;
    @NotNull
    private final String tumData;
    @NotNull
    private final VCFType type;

    public VCFGermlineData(@NotNull final VCFType type, @NotNull final String refData, @NotNull final String tumData) {
        this.refData = refData;
        this.tumData = tumData;
        this.type = type;
    }

    @NotNull
    public VCFType getType() {
        return type;
    }

    @NotNull
    public String getRefData() {
        return refData;
    }

    @NotNull
    public String getTumData() {
        return tumData;
    }
}
