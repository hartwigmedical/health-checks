package com.hartwig.healthchecks.boggs.flagstatreader;

import com.hartwig.healthchecks.boggs.healthcheck.mapping.FlagStatsType;

public class FlagStats {

    private final FlagStatsType flagStatsType;

    private final Double value;

    public FlagStats(final FlagStatsType flagStatsType, final Double value) {
        this.flagStatsType = flagStatsType;
        this.value = value;
    }

    public FlagStatsType getFlagStatsType() {
        return flagStatsType;
    }

    public Double getValue() {
        return value;
    }
}
