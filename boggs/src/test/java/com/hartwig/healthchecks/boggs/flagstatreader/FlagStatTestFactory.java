package com.hartwig.healthchecks.boggs.flagstatreader;

import org.jetbrains.annotations.NotNull;

public final class FlagStatTestFactory {

    private FlagStatTestFactory() {
    }

    @NotNull
    public static FlagStatData createTestData() {
        final FlagStatsBuilder builder = new FlagStatsBuilder();
        builder.setMapped(17885d);
        builder.setTotal(17940d);
        builder.setSingletons(55d);
        builder.setSecondary(20d);
        builder.setSupplementary(0d);
        builder.setDuplicates(1068d);
        builder.setRead1(8960d);
        builder.setRead2(8960d);
        builder.setPairedInSequencing(17920d);
        builder.setProperlyPaired(17808d);
        builder.setItselfAndMateMapped(17810d);
        builder.setMateMappedToDifferentChr(0d);
        builder.setMateMappedToDifferentChrMapQ5(0d);
        return new FlagStatData("AnyPath", builder.build(), builder.build());
    }

}
