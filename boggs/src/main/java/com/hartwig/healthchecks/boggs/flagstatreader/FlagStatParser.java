package com.hartwig.healthchecks.boggs.flagstatreader;

import java.io.IOException;

import com.hartwig.healthchecks.common.exception.EmptyFileException;

import org.jetbrains.annotations.NotNull;

interface FlagStatParser {

    @NotNull
    FlagStatData parse(@NotNull final String filePath, @NotNull final String filter)
                    throws IOException, EmptyFileException;
}
