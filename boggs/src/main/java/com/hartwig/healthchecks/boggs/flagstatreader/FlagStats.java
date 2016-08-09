package com.hartwig.healthchecks.boggs.flagstatreader;

import com.hartwig.healthchecks.boggs.extractor.FlagStatsType;

import org.jetbrains.annotations.NotNull;

public class FlagStats {

    @NotNull
    private final FlagStatsType flagStatsType;
    private final double value;

    public FlagStats(@NotNull final FlagStatsType flagStatsType, final double value) {
        this.flagStatsType = flagStatsType;
        this.value = value;
    }

    @NotNull
    FlagStatsType getFlagStatsType() {
        return flagStatsType;
    }

    public double getValue() {
        return value;
    }
}
