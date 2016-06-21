package com.hartwig.healthchecks.boggs.flagstatreader;

import org.jetbrains.annotations.NotNull;

public class FlagStats {

    private final Double total;
    private final Double secondary;
    private final Double supplementary;
    private final Double duplicates;
    private final Double mapped;
    private final Double pairedInSequencing;
    private final Double read1;
    private final Double read2;
    private final Double properlyPaired;
    private final Double itselfAndMateMapped;
    private final Double singletons;
    private final Double mateMappedToDifferentChr;
    private final Double mateMappedToDifferentChrMapQ5;

    FlagStats(final Double total, final Double secondary, final Double supplementary, final Double duplicates,
            final Double mapped, final Double pairedInSequencing, final Double read1, final Double read2,
            final Double properlyPaired, final Double itselfAndMateMapped, final Double singletons,
            final Double mateMappedToDifferentChr, final Double mateMappedToDifferentChrMapQ5) {

        this.total = total;
        this.secondary = secondary;
        this.supplementary = supplementary;
        this.duplicates = duplicates;
        this.mapped = mapped;
        this.pairedInSequencing = pairedInSequencing;
        this.read1 = read1;
        this.read2 = read2;
        this.properlyPaired = properlyPaired;
        this.itselfAndMateMapped = itselfAndMateMapped;
        this.singletons = singletons;
        this.mateMappedToDifferentChr = mateMappedToDifferentChr;
        this.mateMappedToDifferentChrMapQ5 = mateMappedToDifferentChrMapQ5;
    }

    @NotNull public Double total() {
        return total;
    }

    @NotNull public Double secondary() {
        return secondary;
    }

    @NotNull public Double supplementary() {
        return supplementary;
    }

    @NotNull public Double duplicates() {
        return duplicates;
    }

    @NotNull public Double mapped() {
        return mapped;
    }

    @NotNull public Double pairedInSequencing() {
        return pairedInSequencing;
    }

    @NotNull public Double read1() {
        return read1;
    }

    @NotNull public Double read2() {
        return read2;
    }

    @NotNull public Double properlyPaired() {
        return properlyPaired;
    }

    @NotNull public Double itselfAndMateMapped() {
        return itselfAndMateMapped;
    }

    @NotNull public Double singletons() {
        return singletons;
    }

    @NotNull public Double mateMappedToDifferentChr() {
        return mateMappedToDifferentChr;
    }

    @NotNull public Double mateMappedToDifferentChrMapQ5() {
        return mateMappedToDifferentChrMapQ5;
    }

    @NotNull @Override public String toString() {
        return "FlagStats{" + "total=" + total + ", secondary=" + secondary + ", supplementary=" + supplementary
                + ", duplicates=" + duplicates + ", mapped=" + mapped + ", pairedInSequencing=" + pairedInSequencing
                + ", read1=" + read1 + ", read2=" + read2 + ", properlyPaired=" + properlyPaired
                + ", itselfAndMateMapped=" + itselfAndMateMapped + ", singletons=" + singletons
                + ", mateMappedToDifferentChr=" + mateMappedToDifferentChr + ", mateMappedToDifferentChrMapQ5="
                + mateMappedToDifferentChrMapQ5 + '}';
    }
}
