package com.hartwig.healthchecks.common.io.reader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.io.path.PathExtensionFinder;
import com.hartwig.healthchecks.common.io.path.SamplePathData;
import com.hartwig.healthchecks.common.io.path.SamplePathFinder;

@FunctionalInterface
public interface SampleFinderAndReader {

    @NotNull
    List<String> readLines(@NotNull final SamplePathData samplePathData) throws IOException;

    @NotNull
    static SampleFinderAndReader build() {
        return (samplePathData) -> {
            final Path samplePath = SamplePathFinder.build().findPath(samplePathData.getPath(),
                            samplePathData.getPrefix(), samplePathData.getSuffix());
            final Path fileToRead = PathExtensionFinder.build().findPath(samplePath.toString(),
                            samplePathData.getExtension());
            return FileReader.build().readLines(fileToRead);
        };
    }
}
