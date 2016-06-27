package com.hartwig.healthchecks.boggs.flagstatreader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.FlagStatsType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;

public class SambambaFlagStatParserTest {

    private static final String DID_NOT_GET_THE_EXPECTED_VALUE = "Did not get the expected value.";

    @Test
    public void canParseExampleFile() throws IOException, EmptyFileException {
        final URL exampleFlagStatURL = Resources
                        .getResource("rundir/CPCT12345678R/mapping/CPCT12345678R_FLOWCELL_S2_L001_001.flagstat");
        final String exampleFlagStatFile = exampleFlagStatURL.getPath();

        final FlagStatParser parser = new SambambaFlagStatParser();
        final FlagStatData flagStatData = parser.parse(exampleFlagStatFile);

        assertFlagStatData(flagStatData.getPassedStats(), 13, 0.0d);
        assertFlagStatData(flagStatData.getFailedStats(), 13, 20.0d);
    }

    @Test(expected = EmptyFileException.class)
    public void canParseEmptyFile() throws IOException, EmptyFileException {
        final URL exampleFlagStatURL = Resources
                        .getResource("emptyFiles/CPCT12345678R/mapping/CPCT12345678R_FLOWCELL_S2_L001_001.flagstat");
        final String exampleFlagStatFile = exampleFlagStatURL.getPath();
        final FlagStatParser parser = new SambambaFlagStatParser();
        parser.parse(exampleFlagStatFile);
    }

    private void assertFlagStatData(final List<FlagStats> flagStat, final int size, final double expectedTotalIndex) {
        assertEquals(DID_NOT_GET_THE_EXPECTED_VALUE, size, flagStat.size());

        final FlagStats passedFlagStat = flagStat.stream()
                        .filter(flagStats -> flagStats.getFlagStatsType() == FlagStatsType.TOTAL_INDEX).findFirst()
                        .get();

        assertNotNull(passedFlagStat);
        assertEquals(expectedTotalIndex, passedFlagStat.getValue(), 0.0d);
    }
}
