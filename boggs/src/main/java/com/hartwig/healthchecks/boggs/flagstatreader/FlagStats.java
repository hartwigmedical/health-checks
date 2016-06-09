package com.hartwig.healthchecks.boggs.flagstatreader;

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

    FlagStats(Double total, Double secondary, Double supplementary, Double duplicates, Double mapped, Double pairedInSequencing,
              Double read1, Double read2, Double properlyPaired, Double itselfAndMateMapped, Double singletons,
              Double mateMappedToDifferentChr, Double mateMappedToDifferentChrMapQ5) {
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

    public Double total() {
        return total;
    }

    public Double secondary() {
        return secondary;
    }

    public Double supplementary() {
        return supplementary;
    }

    public Double duplicates() {
        return duplicates;
    }

    public Double mapped() {
        return mapped;
    }

    public Double pairedInSequencing() {
        return pairedInSequencing;
    }

    public Double read1() {
        return read1;
    }

    public Double read2() {
        return read2;
    }

    public Double properlyPaired() {
        return properlyPaired;
    }

    public Double itselfAndMateMapped() {
        return itselfAndMateMapped;
    }

    public Double singletons() {
        return singletons;
    }

    public Double mateMappedToDifferentChr() {
        return mateMappedToDifferentChr;
    }

    public Double mateMappedToDifferentChrMapQ5() {
        return mateMappedToDifferentChrMapQ5;
    }

    @Override
    public String toString() {
        return "FlagStats{" +
                "total=" + total +
                ", secondary=" + secondary +
                ", supplementary=" + supplementary +
                ", duplicates=" + duplicates +
                ", mapped=" + mapped +
                ", pairedInSequencing=" + pairedInSequencing +
                ", read1=" + read1 +
                ", read2=" + read2 +
                ", properlyPaired=" + properlyPaired +
                ", itselfAndMateMapped=" + itselfAndMateMapped +
                ", singletons=" + singletons +
                ", mateMappedToDifferentChr=" + mateMappedToDifferentChr +
                ", mateMappedToDifferentChrMapQ5=" + mateMappedToDifferentChrMapQ5 +
                '}';
    }
}
