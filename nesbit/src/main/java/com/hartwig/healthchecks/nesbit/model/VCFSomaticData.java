package com.hartwig.healthchecks.nesbit.model;

import org.jetbrains.annotations.NotNull;

public class VCFSomaticData {

    @NotNull
    private final String info;
    @NotNull
    private final VCFType type;

    public VCFSomaticData(@NotNull final VCFType type, @NotNull final String info) {
        super();
        this.type = type;
        this.info = info;
    }

    @NotNull
    public VCFType getType() {
        return type;
    }

    @NotNull
    public String getInfo() {
        return info;
    }
}
