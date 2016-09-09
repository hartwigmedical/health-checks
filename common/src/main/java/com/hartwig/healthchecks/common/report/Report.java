package com.hartwig.healthchecks.common.report;

import java.util.Optional;

import com.hartwig.healthchecks.common.exception.GenerateReportException;
import com.hartwig.healthchecks.common.result.BaseResult;

import org.jetbrains.annotations.NotNull;

public interface Report {

    void addResult(@NotNull BaseResult result);

    @NotNull
    Optional<String> generateReport(@NotNull final String runDirectory, @NotNull final String outputPath)
            throws GenerateReportException;
}
