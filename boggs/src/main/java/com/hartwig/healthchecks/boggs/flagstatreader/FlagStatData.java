package com.hartwig.healthchecks.boggs.flagstatreader;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public class FlagStatData {

    @NotNull
    private final List<FlagStats> passedStats;

    @NotNull
    private final List<FlagStats> failedStats;

    public FlagStatData(@NotNull final List<FlagStats> passedStats, @NotNull final List<FlagStats> failedStats) {
        this.passedStats = passedStats;
        this.failedStats = failedStats;
    }

    @NotNull
    public List<FlagStats> getPassedStats() {
        return passedStats;
    }

    @NotNull
    public List<FlagStats> getFailedStats() {
        return failedStats;
    }
}
