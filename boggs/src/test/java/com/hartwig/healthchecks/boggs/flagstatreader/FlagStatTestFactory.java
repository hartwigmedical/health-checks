package com.hartwig.healthchecks.boggs.flagstatreader;

import org.jetbrains.annotations.NotNull;

public final class FlagStatTestFactory {

    private FlagStatTestFactory() {
    }

    @NotNull
    public static FlagStatData createTestData() {
        FlagStatsBuilder builder = new FlagStatsBuilder();
        builder.setMapped(10d);
        builder.setTotal(26d);
        builder.setSingletons(15d);
        return new FlagStatData("AnyPath", builder.build(), builder.build());
    }
}
