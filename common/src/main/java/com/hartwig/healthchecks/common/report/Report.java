package com.hartwig.healthchecks.common.report;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.GenerateReportException;

public interface Report {

    void addReportData(@NotNull BaseReport reportData);

    @NotNull
    Optional<String> generateReport(String runDirectory) throws GenerateReportException;
}
