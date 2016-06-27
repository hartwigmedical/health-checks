package com.hartwig.healthchecks.boggs.model.report;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class PrestatsReport extends BaseReport {

    private static final long serialVersionUID = 3588650481644358694L;

    @NotNull
    private final List<BaseDataReport> referenceSample = new ArrayList<>();

    @NotNull
    private final List<BaseDataReport> tumorSample = new ArrayList<>();

    public PrestatsReport(@NotNull final CheckType checkType) {
        super(checkType);
    }

    public void addReferenceData(@NotNull final List<BaseDataReport> prestatsDataReport) {
        referenceSample.addAll(prestatsDataReport);
    }

    public void addTumorData(@NotNull final List<BaseDataReport> prestatsDataReport) {
        tumorSample.addAll(prestatsDataReport);
    }

    @NotNull
    public List<BaseDataReport> getTumorSample() {
        return tumorSample;
    }

    @NotNull
    public List<BaseDataReport> getReferenceSample() {
        return referenceSample;
    }

    @Override
    public String toString() {
        return "PrestatsReport [referenceSample=" + referenceSample + ", tumorSample=" + tumorSample + "]";
    }
}
