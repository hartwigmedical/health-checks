package com.hartwig.healthchecks.common.report.metadata;

import java.io.Serializable;

public class ReportMetadata implements Serializable {

    private static final long serialVersionUID = 5953267743643966306L;

    private final String date;

    private final String pipelineVersion;

    public ReportMetadata(final String date, final String pipelineVersion) {
        super();
        this.date = date;
        this.pipelineVersion = pipelineVersion;
    }

    public String getPipelineVersion() {
        return pipelineVersion;
    }

    public String getDate() {
        return date;
    }
}
