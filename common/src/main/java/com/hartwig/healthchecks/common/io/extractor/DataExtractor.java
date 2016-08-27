package com.hartwig.healthchecks.common.io.extractor;

import java.io.IOException;

import com.hartwig.healthchecks.common.data.BaseResult;
import com.hartwig.healthchecks.common.exception.HealthChecksException;

import org.jetbrains.annotations.NotNull;

public interface DataExtractor {

    @NotNull
    BaseResult extractFromRunDirectory(@NotNull final String runDirectory) throws IOException, HealthChecksException;
}

