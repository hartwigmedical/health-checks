package com.hartwig.healthchecks.boggs.flagstatreader;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface FlagStatParser {

    @NotNull
    FlagStatData parse(@NotNull final String filePath) throws IOException, EmptyFileException;
}
