package com.hartwig.healthchecks.boggs.model.report;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class PrestatsReport extends BaseReport {

    @NotNull
    private final String externalId;

    @NotNull
    private final List<PrestatsDataReport> summary = new ArrayList<>();

    public PrestatsReport(@NotNull final CheckType checkType, @NotNull final String externalId) {
        super(checkType);
        this.externalId = externalId;
    }

    public void addData(@NotNull final PrestatsDataReport prestatsDataReport) {
        summary.add(prestatsDataReport);
    }

    public void addAllData(@NotNull final List<PrestatsDataReport> prestatsDataReport) {
        summary.addAll(prestatsDataReport);
    }

    @NotNull
    public List<PrestatsDataReport> getSummary() {
        return summary;
    }
    
    @NotNull
    public String getExternalId() {
        return externalId;
    }
    
    @Override
    public String toString() {
        return "PrestatsReport [externalId=" + externalId + ", summary=" + summary + "]";
    }
}
