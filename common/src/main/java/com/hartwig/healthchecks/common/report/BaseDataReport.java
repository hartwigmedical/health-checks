package com.hartwig.healthchecks.common.report;

import org.jetbrains.annotations.NotNull;

public class BaseDataReport {

    @NotNull
    private final String sampleId;
    @NotNull
    private final String checkName;
    @NotNull
    private final String value;

    public BaseDataReport(@NotNull final String sampleId, @NotNull final String checkName,
                    @NotNull final String value) {
        this.sampleId = sampleId;
        this.checkName = checkName;
        this.value = value;
    }

    @NotNull
    public String getSampleId() {
        return sampleId;
    }

    @NotNull
    public String getCheckName() {
        return checkName;
    }

    @NotNull
    public String getValue() {
        return value;
    }
}
