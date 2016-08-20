package com.hartwig.healthchecks.nesbit.model;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

public class VCFSomaticSetData {

    private final int totalCallerCount;
    @NotNull
    private final Map<String, Integer> callersCountPerCaller;

    public VCFSomaticSetData(final int totalCallerCount, @NotNull final Map<String, Integer> callersCountPerCaller) {
        super();
        this.totalCallerCount = totalCallerCount;
        this.callersCountPerCaller = callersCountPerCaller;
    }

    @NotNull
    public Map<String, Integer> getCallersCountPerCaller() {
        return callersCountPerCaller;
    }

    public int getTotalCallerCount() {
        return totalCallerCount;
    }
}
