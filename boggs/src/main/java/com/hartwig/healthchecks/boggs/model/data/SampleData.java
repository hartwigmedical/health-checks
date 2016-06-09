package com.hartwig.healthchecks.boggs.model.data;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;

public class SampleData{

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

    public SampleData( @NotNull String externalID, @NotNull List<FlagStatData> rawMappingFlagstats,
                      @NotNull List<FlagStatData> sortedMappingFlagstats, @NotNull FlagStatData markdupFlagstats,
                      @NotNull FlagStatData realignFlagstats) {
        this.externalID = externalID;
        this.rawMappingFlagstats = rawMappingFlagstats;
        this.sortedMappingFlagstats = sortedMappingFlagstats;
        this.markdupFlagstats = markdupFlagstats;
        this.realignFlagstats = realignFlagstats;
    }

    @NotNull
    public String getSampleId() {
        return sampleID;
    }

    @NotNull
    public String getExternalId() {
        return externalID;
    }

    @NotNull
    public List<FlagStatData> getRawMappingFlagstats() {
        return rawMappingFlagstats;
    }

    @NotNull
    public List<FlagStatData> getSortedMappingFlagstats() {
        return sortedMappingFlagstats;
    }

    @NotNull
    public FlagStatData getMarkdupFlagstat() {
        return markdupFlagstats;
    }

    @NotNull
    public FlagStatData getRealignFlagstat() {
        return realignFlagstats;
    }

    @Override
    public String toString() {
        return "SampleData{" +
                "getSampleId='" + sampleID + '\'' +
                ", getExternalId='" + externalID + '\'' +
                ", getRawMappingFlagstats=" + rawMappingFlagstats +
                ", getSortedMappingFlagstats=" + sortedMappingFlagstats +
                ", markdupFlagstats=" + markdupFlagstats +
                ", realignFlagstats=" + realignFlagstats +
                '}';
    }
}
