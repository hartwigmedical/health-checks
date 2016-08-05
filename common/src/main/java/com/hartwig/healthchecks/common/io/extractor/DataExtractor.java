package com.hartwig.healthchecks.common.io.extractor;

import java.io.IOException;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.report.BaseReport;

import org.jetbrains.annotations.NotNull;

public interface DataExtractor {

    @NotNull
    BaseReport extractFromRunDirectory(@NotNull final String runDirectory) throws IOException, HealthChecksException;
}
