package com.hartwig.healthchecks.boggs.flagstatreader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.boggs.extractor.FlagStatsType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class SambambaFlagStatParserTest {

    @Test
    public void parseExampleFile() throws IOException, EmptyFileException {
        final URL exampleFlagStatURL = Resources.getResource("run/sample1/mapping/");
        final String exampleFlagStatFile = exampleFlagStatURL.getPath();

        final FlagStatParser parser = new SambambaFlagStatParser();
        final FlagStatData flagStatData = parser.parse(exampleFlagStatFile, ".realign");

        assertFlagStatData(flagStatData.getPassedStats(), 13, 17940.0d);
        assertFlagStatData(flagStatData.getFailedStats(), 13, 0.0d);
    }

    @Test(expected = EmptyFileException.class)
    public void parseEmptyFile() throws IOException, EmptyFileException {
        final URL exampleFlagStatURL = Resources.getResource("run/sample3/mapping/");
        final String exampleFlagStatFile = exampleFlagStatURL.getPath();
        final FlagStatParser parser = new SambambaFlagStatParser();
        parser.parse(exampleFlagStatFile, "realign");
    }

    @Test(expected = NoSuchFileException.class)
    public void parseFileNotFound() throws IOException, EmptyFileException {
        final FlagStatParser parser = new SambambaFlagStatParser();
        parser.parse("run/sample4/mapping/", "realign");
    }

    private static void assertFlagStatData(@NotNull final List<FlagStats> flagStat, final int expectedSize,
            final double expectedTotalIndex) {
        assertEquals(expectedSize, flagStat.size());

        final Optional<FlagStats> passedFlagStat = flagStat.stream().filter(
                flagStats -> flagStats.getFlagStatsType() == FlagStatsType.TOTAL_INDEX).findFirst();

        assert passedFlagStat.isPresent();

        assertNotNull(passedFlagStat.get());
        assertEquals(expectedTotalIndex, passedFlagStat.get().getValue(), 0.0d);
    }
}
