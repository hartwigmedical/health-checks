package com.hartwig.healthchecks.common.report;

import java.util.Optional;

import com.hartwig.healthchecks.common.util.BaseReport;

import org.jetbrains.annotations.NotNull;

public interface Report {

    void addReportData(@NotNull BaseReport reportData);

    @NotNull
    Optional<String> generateReport();
}
