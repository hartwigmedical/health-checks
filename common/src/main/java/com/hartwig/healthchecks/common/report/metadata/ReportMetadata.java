package com.hartwig.healthchecks.common.report.metadata;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

public class ReportMetadata implements Serializable {

    private static final long serialVersionUID = 5953267743643966306L;

    @NotNull
    private final String date;
    @NotNull
    private final String pipelineVersion;

    public ReportMetadata(@NotNull final String date, @NotNull final String pipelineVersion) {
        super();
        this.date = date;
        this.pipelineVersion = pipelineVersion;
    }

    @NotNull
    public String getPipelineVersion() {
        return pipelineVersion;
    }

    @NotNull
    public String getDate() {
        return date;
    }
}
