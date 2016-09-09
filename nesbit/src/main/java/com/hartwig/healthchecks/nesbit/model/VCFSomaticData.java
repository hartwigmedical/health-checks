package com.hartwig.healthchecks.nesbit.model;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public class VCFSomaticData {

    @NotNull
    private final VCFType type;
    @NotNull
    private final List<String> callers;
    private final double alleleFrequency;

    VCFSomaticData(@NotNull final VCFType type, @NotNull final List<String> callers, final double alleleFrequency) {
        this.type = type;
        this.callers = callers;
        this.alleleFrequency = alleleFrequency;
    }

    @NotNull
    public VCFType type() {
        return type;
    }

    @NotNull
    public List<String> callers() {
        return callers;
    }

    public long callerCount() {
        return callers.size();
    }

    public double alleleFrequency() {
        return alleleFrequency;
    }
}
