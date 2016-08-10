package com.hartwig.healthchecks.common.io.path;

import org.jetbrains.annotations.NotNull;

public class SamplePathData {

    @NotNull
    private final String path;
    @NotNull
    private final String extension;
    @NotNull
    private final String prefix;
    @NotNull
    private final String suffix;

    public SamplePathData(@NotNull final String path, @NotNull final String prefix, @NotNull final String suffix,
                    @NotNull final String extension) {
        this.path = path;
        this.extension = extension;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @NotNull
    public String getExtension() {
        return extension;
    }

    @NotNull
    public String getPrefix() {
        return prefix;
    }

    @NotNull
    public String getSuffix() {
        return suffix;
    }
}
