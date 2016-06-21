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

    @NotNull
    public Double getTotal() {
        return total;
    }

    @NotNull
    public Double getSecondary() {
        return secondary;
    }

    @NotNull
    public Double getSupplementary() {
        return supplementary;
    }

    @NotNull
    public Double getDuplicates() {
        return duplicates;
    }

    @NotNull
    public Double getMapped() {
        return mapped;
    }

    @NotNull
    public Double getPairedInSequencing() {
        return pairedInSequencing;
    }

    @NotNull
    public Double getRead1() {
        return read1;
    }

    @NotNull
    public Double getRead2() {
        return read2;
    }

    @NotNull
    public Double getProperlyPaired() {
        return properlyPaired;
    }

    @NotNull
    public Double getItselfAndMateMapped() {
        return itselfAndMateMapped;
    }

    @NotNull
    public Double getSingletons() {
        return singletons;
    }

    @NotNull
    public Double getMateMappedToDifferentChr() {
        return mateMappedToDifferentChr;
    }

    @NotNull
    public Double getMateMappedToDifferentChrMapQ5() {
        return mateMappedToDifferentChrMapQ5;
    }

    @NotNull
    @Override
    public String toString() {
        return "FlagStats{" + "getTotal=" + total + ", getSecondary=" + secondary + ", getSupplementary="
                + supplementary + ", getDuplicates=" + duplicates + ", getMapped=" + mapped
                + ", getPairedInSequencing=" + pairedInSequencing + ", getRead1=" + read1 + ", getRead2=" + read2
                + ", getProperlyPaired=" + properlyPaired + ", getItselfAndMateMapped=" + itselfAndMateMapped
                + ", getSingletons=" + singletons + ", getMateMappedToDifferentChr=" + mateMappedToDifferentChr
                + ", getMateMappedToDifferentChrMapQ5=" + mateMappedToDifferentChrMapQ5 + '}';
    }
}
