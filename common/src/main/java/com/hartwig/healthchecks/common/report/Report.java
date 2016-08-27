package com.hartwig.healthchecks.common.report;

import java.util.Optional;

import com.hartwig.healthchecks.common.data.BaseResult;
import com.hartwig.healthchecks.common.exception.GenerateReportException;

import org.jetbrains.annotations.NotNull;

public interface Report {

    void addReportData(@NotNull BaseResult reportData);

    @NotNull
    Optional<String> generateReport(@NotNull String runDirectory) throws GenerateReportException;
}
