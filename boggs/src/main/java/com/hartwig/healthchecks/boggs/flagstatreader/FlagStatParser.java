package com.hartwig.healthchecks.boggs.flagstatreader;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.exception.EmptyFileException;

public interface FlagStatParser {

	@NotNull
	FlagStatData parse(@NotNull String filePath) throws IOException, EmptyFileException;
}
