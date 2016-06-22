package com.hartwig.healthchecks.boggs.flagstatreader;

import org.jetbrains.annotations.NotNull;

class FlagStatsBuilder {

    private double total;

    private double secondary;

    private double supplementary;

    private double duplicates;

    private double mapped;

    private double pairedInSequencing;

    private double read1;

    private double read2;

    private double properlyPaired;

    private double itselfAndMateMapped;

    private double singletons;

    private double mateMappedToDifferentChr;

    private double mateMappedToDifferentChrMapQ5;

    @NotNull
    public FlagStatsBuilder setTotal(final double total) {
        this.total = total;
        return this;
    }

    @NotNull
    public FlagStatsBuilder setSecondary(final double secondary) {
        this.secondary = secondary;
        return this;
    }

    @NotNull
    public FlagStatsBuilder setSupplementary(final double supplementary) {
        this.supplementary = supplementary;
        return this;
    }

    @NotNull
    public FlagStatsBuilder setDuplicates(final double duplicates) {
        this.duplicates = duplicates;
        return this;
    }

    @NotNull
    public FlagStatsBuilder setMapped(final double mapped) {
        this.mapped = mapped;
        return this;
    }

    @NotNull
    public FlagStatsBuilder setPairedInSequencing(final double pairedInSequencing) {
        this.pairedInSequencing = pairedInSequencing;
        return this;
    }

    @NotNull
    public FlagStatsBuilder setRead1(final double read1) {
        this.read1 = read1;
        return this;
    }

    @NotNull
    public FlagStatsBuilder setRead2(final double read2) {
        this.read2 = read2;
        return this;
    }

    @NotNull
    public FlagStatsBuilder setProperlyPaired(final double properlyPaired) {
        this.properlyPaired = properlyPaired;
        return this;
    }

    @NotNull
    public FlagStatsBuilder setItselfAndMateMapped(final double itselfAndMateMapped) {
        this.itselfAndMateMapped = itselfAndMateMapped;
        return this;
    }

    @NotNull
    public FlagStatsBuilder setSingletons(final double singletons) {
        this.singletons = singletons;
        return this;
    }

    @NotNull
    public FlagStatsBuilder setMateMappedToDifferentChr(final double mateMappedToDifferentChr) {
        this.mateMappedToDifferentChr = mateMappedToDifferentChr;
        return this;
    }

    @NotNull
    public FlagStatsBuilder setMateMappedToDifferentChrMapQ5(final double mateMappedToDifferentChrMapQ5) {
        this.mateMappedToDifferentChrMapQ5 = mateMappedToDifferentChrMapQ5;
        return this;
    }

    @NotNull
    public FlagStats build() {
        return new FlagStats(total, secondary, supplementary, duplicates, mapped, pairedInSequencing, read1, read2,
                properlyPaired, itselfAndMateMapped, singletons, mateMappedToDifferentChr,
                mateMappedToDifferentChrMapQ5);
    }
}
