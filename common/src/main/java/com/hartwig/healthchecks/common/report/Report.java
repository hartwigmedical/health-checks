package com.hartwig.healthchecks.common.report;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.util.BaseReport;

public interface Report {

    void addReportData(@NotNull BaseReport reportData);

    @NotNull
    Optional<String> generateReport();
}
