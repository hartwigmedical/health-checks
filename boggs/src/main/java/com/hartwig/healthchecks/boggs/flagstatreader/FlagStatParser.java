package com.hartwig.healthchecks.boggs.flagstatreader;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.EmptyFileException;

public interface FlagStatParser {

    @NotNull
    FlagStatData parse(@NotNull final String filePath, String filter) throws IOException, EmptyFileException;
}
