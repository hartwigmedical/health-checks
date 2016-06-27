package com.hartwig.healthchecks.boggs.flagstatreader;

import com.hartwig.healthchecks.boggs.healthcheck.mapping.FlagStatsType;

public class FlagStats {

    private final FlagStatsType flagStatsType;

    private final String checkType;

    private final Double value;

    public FlagStats(final FlagStatsType flagStatsType, final String checkType, final Double value) {
        this.flagStatsType = flagStatsType;
        this.checkType = checkType;
        this.value = value;
    }

    public FlagStatsType getFlagStatsType() {
        return flagStatsType;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "FlagStats{" + "flagStatsType=" + flagStatsType + ", checkType='" + checkType + '\'' + ", value=" + value
                        + '}';
    }
}
