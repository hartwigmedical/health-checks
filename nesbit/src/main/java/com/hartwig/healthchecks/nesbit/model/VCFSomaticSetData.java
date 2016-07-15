package com.hartwig.healthchecks.nesbit.model;

import java.util.Map;

public class VCFSomaticSetData {

    private final int totalCallerCount;

    private final Map<String, Integer> callersCountPerCaller;

    public VCFSomaticSetData(final int totalCallerCount, final Map<String, Integer> callersCountPerCaller) {
        super();
        this.totalCallerCount = totalCallerCount;
        this.callersCountPerCaller = callersCountPerCaller;
    }

    public Map<String, Integer> getCallersCountPerCaller() {
        return callersCountPerCaller;
    }

    public int getTotalCallerCount() {
        return totalCallerCount;
    }
}
