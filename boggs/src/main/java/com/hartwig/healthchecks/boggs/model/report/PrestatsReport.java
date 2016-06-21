package com.hartwig.healthchecks.boggs.model.report;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class PrestatsReport extends BaseReport {

    private static final long serialVersionUID = 3588650481644358694L;
    @NotNull
    private final List<PrestatsDataReport> summary = new ArrayList<>();

    public PrestatsReport(@NotNull final CheckType checkType) {
        super(checkType);
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

    @Override
    public String toString() {
        return "PrestatsReport [summary=" + summary + "]";
    }
}
