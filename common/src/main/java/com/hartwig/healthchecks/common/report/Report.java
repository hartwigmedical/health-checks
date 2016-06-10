package com.hartwig.healthchecks.common.report;

import com.hartwig.healthchecks.common.util.BaseReport;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface Report {

    void addReportData(@NotNull BaseReport reportData);

    @NotNull
    Optional<String> generateReport() ;
}
