package com.hartwig.healthchecks.boggs.model.report;

import org.jetbrains.annotations.NotNull;

public class PrestatsDataReport {

    private final String checkName;
    private final String status;
    private final String file;

    public PrestatsDataReport(@NotNull final String status, @NotNull final String checkName, @NotNull final String file) {
        this.status = status;
        this.checkName = checkName;
        this.file = file;
    }

    @NotNull
    public String getCheckName() {
        return checkName;
    }

    @NotNull
    public String getStatus() {
        return status;
    }

    @NotNull
    public String getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "PrestatsDataReport [checkName=" + checkName + ", status=" + status + ", file=" + file + "]";
    }
}
