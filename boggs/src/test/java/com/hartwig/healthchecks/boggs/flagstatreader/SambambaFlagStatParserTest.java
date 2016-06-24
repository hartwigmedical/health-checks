package com.hartwig.healthchecks.boggs.flagstatreader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.IntStream;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.exception.EmptyFileException;

import org.junit.Test;

public class SambambaFlagStatParserTest {

    @Test
    public void canParseExampleFile() throws IOException, EmptyFileException {
        final URL exampleFlagStatURL = Resources.getResource(
                "rundir/CPCT12345678R/mapping/CPCT12345678R_FLOWCELL_S2_L001_001.flagstat");
        final String exampleFlagStatFile = exampleFlagStatURL.getPath();

        final FlagStatParser parser = new SambambaFlagStatParser();
        final FlagStatData flagStatData = parser.parse(exampleFlagStatFile);

        List<FlagStats> passedStats = flagStatData.getPassedStats();
        IntStream.range(0, passedStats.size()).forEach(index -> {
            assertEquals(Double.valueOf(index), passedStats.get(index).getValue());
        });

        List<FlagStats> failedStats = flagStatData.getFailedStats();
        IntStream.range(0, failedStats.size()).forEach(index -> {
            assertEquals(Double.valueOf(index), failedStats.get(index).getValue());
        });
    }

    @Test(expected = EmptyFileException.class)
    public void canParseEmptyFile() throws IOException, EmptyFileException {
        final URL exampleFlagStatURL = Resources.getResource(
                "emptyFiles/CPCT12345678R/mapping/CPCT12345678R_FLOWCELL_S2_L001_001.flagstat");
        final String exampleFlagStatFile = exampleFlagStatURL.getPath();
        final FlagStatParser parser = new SambambaFlagStatParser();
        parser.parse(exampleFlagStatFile);
    }
}
