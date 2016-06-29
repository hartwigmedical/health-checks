package com.hartwig.healthchecks.common.extractor;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.util.BaseReport;

public interface DataExtractor {

    BaseReport extractFromRunDirectory(@NotNull final String runDirectory) throws IOException, HealthChecksException;
}
