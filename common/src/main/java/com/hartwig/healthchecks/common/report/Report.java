package com.hartwig.healthchecks.common.report;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.GenerateReportException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.result.BaseResult;

public interface Report {

    void addResult(@NotNull BaseResult result);

    @NotNull
    Optional<String> generateReport(@NotNull RunContext runContext, @NotNull String outputPath)
            throws GenerateReportException;
}
