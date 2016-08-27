package com.hartwig.healthchecks.common.io.extractor;

import java.io.IOException;

import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.result.BaseResult;

import org.jetbrains.annotations.NotNull;

public interface DataExtractor {

    @NotNull
    BaseResult extract() throws IOException, HealthChecksException;
}

