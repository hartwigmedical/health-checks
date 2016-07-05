package com.hartwig.healthchecks.common.io.reader;

import org.jetbrains.annotations.NotNull;

public class SamplePath {

    @NotNull
    private final String path;

    @NotNull
    private final String extension;

    @NotNull
    private final String prefix;

    @NotNull
    private final String suffix;

    public SamplePath(@NotNull final String path, @NotNull final String prefix, @NotNull final String suffix,
                    @NotNull final String extension) {
        this.path = path;
        this.extension = extension;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPath() {
        return path;
    }

    public String getExtension() {
        return extension;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }
}
