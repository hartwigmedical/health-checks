package com.hartwig.healthchecks.boggs.flagstatreader;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public class FlagStatData {

    @NotNull
    private final String path;

    @NotNull
    private final List<FlagStats> passedStats;

    @NotNull
    private final List<FlagStats> failedStats;

    FlagStatData(@NotNull final String path, @NotNull final List<FlagStats> passedStats,
            @NotNull final List<FlagStats> failedStats) {
        this.path = path;
        this.passedStats = passedStats;
        this.failedStats = failedStats;
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @NotNull
    public List<FlagStats> getPassedStats() {
        return passedStats;
    }

    @NotNull
    public List<FlagStats> getFailedStats() {
        return failedStats;
    }

    @Override
    public String toString() {
        return "FlagStatData{"
                + "path='" + path + '\''
                + ", passedStats=" + passedStats
                + ", failedStats=" + failedStats
                + '}';
    }
}
