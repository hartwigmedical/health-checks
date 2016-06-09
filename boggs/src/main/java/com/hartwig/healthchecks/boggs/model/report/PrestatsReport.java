package com.hartwig.healthchecks.boggs.model.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class PrestatsReport extends BaseReport {

    @NotNull
    private Map<String, List<String>> summary;

    public PrestatsReport(CheckType checkType) {
        super(checkType);
        this.summary = new HashMap<>();
    }

    public void addData(String filename, String check) {
        List<String> checks = summary.getOrDefault(filename, new ArrayList<>());
        checks.add(check);

        summary.put(filename, checks);
    }

    @NotNull
    public Map<String, List<String>> getSummary() {
        return summary;
    }

    @Override
    public String toString() {
        return "PrestatsData{" +
                "summary=" + summary +
                '}';
    }
}
