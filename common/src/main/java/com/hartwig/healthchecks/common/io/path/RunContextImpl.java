package com.hartwig.healthchecks.common.io.path;

import org.jetbrains.annotations.NotNull;

class RunContextImpl implements RunContext {

    @NotNull
    private final String runDirectory;
    @NotNull
    private final SampleContext refSample;
    @NotNull
    private final SampleContext tumorSample;
    @NotNull
    private final String logs;
    @NotNull
    private final String somatics;

    RunContextImpl(@NotNull final String runDirectory, @NotNull final SampleContext refSample,
            @NotNull final SampleContext tumorSample, @NotNull final String logs, @NotNull final String somatics) {
        this.runDirectory = runDirectory;
        this.refSample = refSample;
        this.tumorSample = tumorSample;
        this.logs = logs;
        this.somatics = somatics;
    }

    @NotNull
    @Override
    public String runDirectory() {
        return runDirectory;
    }

    @NotNull
    @Override
    public SampleContext refSample() {
        return refSample;
    }

    @NotNull
    @Override
    public SampleContext tumorSample() {
        return tumorSample;
    }

    @NotNull
    @Override
    public String logs() {
        return logs;
    }

    @NotNull
    @Override
    public String somatics() {
        return somatics;
    }
}

