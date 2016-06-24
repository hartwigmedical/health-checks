package com.hartwig.healthchecks.boggs.flagstatreader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.FlagStatsType;
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

        final List<FlagStats> passedStats = flagStatData.getPassedStats();
        assertEquals("Did not get the expected value.", 13, passedStats.size());

        final FlagStats flagStat = passedStats
                .stream()
                .filter(flagStats -> flagStats.getFlagStatsType() == FlagStatsType.TOTAL_INDEX)
                .findFirst()
                .get();

        assertNotNull(flagStat);
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
