package com.hartwig.healthchecks.common.io.path;

import org.jetbrains.annotations.NotNull;

class SampleContextImpl implements SampleContext {

    @NotNull
    private final String sampleId;
    @NotNull
    private final String mapping;
    @NotNull
    private final String sampleQcStats;
    @NotNull
    private final String runQcStats;

    SampleContextImpl(@NotNull final String sampleId, @NotNull final String mapping,
            @NotNull final String sampleQcStats, @NotNull final String runQcStats) {
        this.sampleId = sampleId;
        this.mapping = mapping;
        this.sampleQcStats = sampleQcStats;
        this.runQcStats = runQcStats;
    }

    @NotNull
    @Override
    public String sampleId() {
        return sampleId;
    }

    @NotNull
    @Override
    public String mapping() {
        return mapping;
    }

    @NotNull
    @Override
    public String sampleQcStats() {
        return sampleQcStats;
    }

    @NotNull
    @Override
    public String runQcStats() {
        return runQcStats;
    }
}
