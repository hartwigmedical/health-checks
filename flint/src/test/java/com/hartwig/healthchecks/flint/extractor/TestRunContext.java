package com.hartwig.healthchecks.flint.extractor;

import java.io.File;
import java.nio.file.Path;

import com.hartwig.healthchecks.common.io.path.RunContext;

import org.jetbrains.annotations.NotNull;

class TestRunContext implements RunContext {

    @NotNull
    private final String refSample;
    @NotNull
    private final String tumorSample;
    @NotNull
    private final Path dummyPath = new File("").toPath();

    TestRunContext(@NotNull final String refSample, @NotNull final String tumorSample) {
        this.refSample = refSample;
        this.tumorSample = tumorSample;
    }

    @NotNull
    @Override
    public String getRunDirectory() {
        return "TEST";
    }

    @NotNull
    @Override
    public String getRefSample() {
        return refSample;
    }

    @NotNull
    @Override
    public String getTumorSample() {
        return tumorSample;
    }

    @NotNull
    @Override
    public Path getRefSampleInsertSizeMetricsPath() {
        return dummyPath;
    }

    @NotNull
    @Override
    public Path getTumorSampleInsertSizeMetricsPath() {
        return dummyPath;
    }
}
