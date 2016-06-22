package com.hartwig.healthchecks.boggs.flagstatreader;

import org.jetbrains.annotations.NotNull;

public class FlagStatData {

    @NotNull
    private final String path;

    @NotNull
    private final FlagStats qcPassedReads;

    @NotNull
    private final FlagStats qcFailedReads;

    FlagStatData(@NotNull final String path, @NotNull final FlagStats qcPassedReads,
            @NotNull final FlagStats qcFailedReads) {
        this.path = path;
        this.qcPassedReads = qcPassedReads;
        this.qcFailedReads = qcFailedReads;
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @NotNull
    public FlagStats getQcPassedReads() {
        return qcPassedReads;
    }

    @NotNull
    public FlagStats getQcFailedReads() {
        return qcFailedReads;
    }

    @Override
    public String toString() {
        return "FlagStatData{" + "path='" + path + '\'' + ", getQcPassedReads=" + qcPassedReads + ", getQcFailedReads="
                + qcFailedReads + '}';
    }
}
