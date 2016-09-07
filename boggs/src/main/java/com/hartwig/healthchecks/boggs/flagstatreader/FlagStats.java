package com.hartwig.healthchecks.boggs.flagstatreader;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.healthchecks.boggs.check.FlagStatsType;

import org.jetbrains.annotations.NotNull;

public class FlagStats {

    @NotNull
    private final FlagStatsType flagStatsType;
    private final double value;

    FlagStats(@NotNull final FlagStatsType flagStatsType, final double value) {
        this.flagStatsType = flagStatsType;
        this.value = value;
    }

    @NotNull
    @VisibleForTesting
    FlagStatsType getFlagStatsType() {
        return flagStatsType;
    }

    public double getValue() {
        return value;
    }
}
