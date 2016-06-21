package com.hartwig.healthchecks.boggs.model.report;

import org.jetbrains.annotations.NotNull;

public class MappingDataReport {

    @NotNull
    private final Double mappedPercentage;
    @NotNull
    private final Double properlyPairedPercentage;
    @NotNull
    private final Double singletonPercentage;
    @NotNull
    private final Double mateMappedToDifferentChrPercentage;
    @NotNull
    private final Double proportionOfDuplicateRead;

    @NotNull
    private final boolean isAllReadsPresent;

    public MappingDataReport(@NotNull final Double mappedPercentage, @NotNull final Double properlyPairedPercentage,
            @NotNull final Double singletonPercentage, @NotNull final Double mateMappedToDifferentChrPercentage,
            @NotNull final Double proportionOfDuplicateRead, boolean isAllReadsPresent) {

        this.mappedPercentage = mappedPercentage;
        this.properlyPairedPercentage = properlyPairedPercentage;
        this.singletonPercentage = singletonPercentage;
        this.mateMappedToDifferentChrPercentage = mateMappedToDifferentChrPercentage;
        this.proportionOfDuplicateRead = proportionOfDuplicateRead;
        this.isAllReadsPresent = isAllReadsPresent;
    }

    @NotNull
    public Double getMappedPercentage() {
        return mappedPercentage;
    }

    @NotNull
    public Double getProperlyPairedPercentage() {
        return properlyPairedPercentage;
    }

    @NotNull
    public Double getSingletonPercentage() {
        return singletonPercentage;
    }

    @NotNull
    public Double getMateMappedToDifferentChrPercentage() {
        return mateMappedToDifferentChrPercentage;
    }

    @NotNull
    public Double getProportionOfDuplicateRead() {
        return proportionOfDuplicateRead;
    }

    public boolean isAllReadsPresent() {
        return isAllReadsPresent;
    }

    @Override
    public String toString() {
        return "MappingDataReport [mappedPercentage=" + mappedPercentage + ", properlyPairedPercentage="
                + properlyPairedPercentage + ", singletonPercentage=" + singletonPercentage
                + ", mateMappedToDifferentChrPercentage=" + mateMappedToDifferentChrPercentage
                + ", proportionOfDuplicateRead=" + proportionOfDuplicateRead + ", isAllReadsPresent="
                + isAllReadsPresent + "]";
    }
}
