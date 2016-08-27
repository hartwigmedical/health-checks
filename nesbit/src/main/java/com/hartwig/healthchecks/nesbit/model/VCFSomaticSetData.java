package com.hartwig.healthchecks.nesbit.model;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public class VCFSomaticSetData {

    @NotNull
    private final List<String> callers;

    public VCFSomaticSetData(@NotNull final List<String> callers) {
        this.callers = callers;
    }

    @NotNull
    public List<String> getCallers() {
        return callers;
    }

    public int getCallerCount() {
        return callers.size();
    }
}
