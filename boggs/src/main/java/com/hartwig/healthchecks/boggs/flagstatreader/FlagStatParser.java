package com.hartwig.healthchecks.boggs.flagstatreader;

import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public interface FlagStatParser {

    @NotNull
    FlagStatData parse(@NotNull File file) throws IOException;
}
