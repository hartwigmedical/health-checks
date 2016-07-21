package com.hartwig.healthchecks.common.adapter;

import java.util.HashMap;
import java.util.Map;

import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.report.StandardOutputReport;

import org.jetbrains.annotations.NotNull;

public final class ReportsFlyweight {

    private static final Map<String, Report> FLYWEIGHT = new HashMap<>();

    private static final ReportsFlyweight INSTANCE = new ReportsFlyweight();

    static {
        FLYWEIGHT.put("json", JsonReport.getInstance());
        FLYWEIGHT.put("stdout", StandardOutputReport.getInstance());
    }

    private ReportsFlyweight() {
    }

    public static ReportsFlyweight getInstance() {
        return INSTANCE;
    }

    @NotNull
    public Report getReport(@NotNull final String reportType) {
        Report defaultReport = StandardOutputReport.getInstance();

        if (FLYWEIGHT.containsKey(reportType)) {
            defaultReport = FLYWEIGHT.get(reportType);
        }
        return defaultReport;
    }
}
