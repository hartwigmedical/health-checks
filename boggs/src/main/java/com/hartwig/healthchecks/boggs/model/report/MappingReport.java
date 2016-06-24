package com.hartwig.healthchecks.boggs.model.report;

import java.util.ArrayList;
import java.util.List;

import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import org.jetbrains.annotations.NotNull;

public class MappingReport extends BaseReport {

    private static final long serialVersionUID = 7647060563039702736L;

    @NotNull
    private final List<BaseDataReport> mapping = new ArrayList<>();

    public MappingReport(@NotNull final CheckType checkType) {
        super(checkType);
    }

    public void addData(@NotNull  final BaseDataReport dataReport) {
        mapping.add(dataReport);
    }

    public void addAll(List<BaseDataReport> mappingReportList) {
        mapping.addAll(mappingReportList);
    }

    @NotNull
    public List<BaseDataReport> getMapping() {
        return mapping;
    }

    @Override
    public String toString() {
        return "MappingReport{" +
                "mapping=" + mapping +
                '}';
    }
}
