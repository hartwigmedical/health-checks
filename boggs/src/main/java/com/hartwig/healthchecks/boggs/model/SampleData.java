package com.hartwig.healthchecks.boggs.model;

import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SampleData {

    @NotNull
    private final String sampleID = "IRRELEVANT"; // KODU: Eventually should contain sample barcode (FR12345678)
    @NotNull
    private final String externalID;
    @NotNull
    private final List<FlagStatData> rawMappingFlagstats;
    @NotNull
    private final List<FlagStatData> sortedMappingFlagstats;
    @NotNull
    private final FlagStatData markdupFlagstats;
    @NotNull
    private final FlagStatData realignFlagstats;

    public SampleData(@NotNull String externalID, @NotNull List<FlagStatData> rawMappingFlagstats,
                      @NotNull List<FlagStatData> sortedMappingFlagstats, @NotNull FlagStatData markdupFlagstats,
                      @NotNull FlagStatData realignFlagstats) {
        this.externalID = externalID;
        this.rawMappingFlagstats = rawMappingFlagstats;
        this.sortedMappingFlagstats = sortedMappingFlagstats;
        this.markdupFlagstats = markdupFlagstats;
        this.realignFlagstats = realignFlagstats;
    }

    @NotNull
    public String sampleID() {
        return sampleID;
    }

    @NotNull
    public String externalID() {
        return externalID;
    }

    @NotNull
    public List<FlagStatData> rawMappingFlagstats() {
        return rawMappingFlagstats;
    }

    @NotNull
    public List<FlagStatData> sortedMappingFlagstats() {
        return sortedMappingFlagstats;
    }

    @NotNull
    public FlagStatData markdupFlagstat() {
        return markdupFlagstats;
    }

    @NotNull
    public FlagStatData realignFlagstat() {
        return realignFlagstats;
    }

    @Override
    public String toString() {
        return "SampleData{" +
                "sampleID='" + sampleID + '\'' +
                ", externalID='" + externalID + '\'' +
                ", rawMappingFlagstats=" + rawMappingFlagstats +
                ", sortedMappingFlagstats=" + sortedMappingFlagstats +
                ", markdupFlagstats=" + markdupFlagstats +
                ", realignFlagstats=" + realignFlagstats +
                '}';
    }
}
