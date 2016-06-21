package com.hartwig.healthchecks.boggs.flagstatreader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.exception.EmptyFileException;

import org.junit.Test;

public class SambambaFlagStatParserTest {

    @Test public void canParseExampleFile() throws IOException, EmptyFileException {
        final URL exampleFlagStatURL = Resources.getResource(
                "rundir/CPCT12345678R/mapping/CPCT12345678R_FLOWCELL_S2_L001_001.flagstat");
        final String exampleFlagStatFile = exampleFlagStatURL.getPath();

        final FlagStatParser parser = new SambambaFlagStatParser();
        final FlagStatData flagStatData = parser.parse(exampleFlagStatFile);

        assertEquals(Double.valueOf(0), flagStatData.getQcPassedReads().total());
        assertEquals(Double.valueOf(1), flagStatData.getQcPassedReads().secondary());
        assertEquals(Double.valueOf(2), flagStatData.getQcPassedReads().supplementary());
        assertEquals(Double.valueOf(3), flagStatData.getQcPassedReads().duplicates());
        assertEquals(Double.valueOf(4), flagStatData.getQcPassedReads().mapped());
        assertEquals(Double.valueOf(5), flagStatData.getQcPassedReads().pairedInSequencing());
        assertEquals(Double.valueOf(6), flagStatData.getQcPassedReads().read1());
        assertEquals(Double.valueOf(7), flagStatData.getQcPassedReads().read2());
        assertEquals(Double.valueOf(8), flagStatData.getQcPassedReads().properlyPaired());
        assertEquals(Double.valueOf(9), flagStatData.getQcPassedReads().itselfAndMateMapped());
        assertEquals(Double.valueOf(10), flagStatData.getQcPassedReads().singletons());
        assertEquals(Double.valueOf(11), flagStatData.getQcPassedReads().mateMappedToDifferentChr());
        assertEquals(Double.valueOf(12), flagStatData.getQcPassedReads().mateMappedToDifferentChrMapQ5());

        assertEquals(Double.valueOf(20), flagStatData.getQcFailedReads().total());
        assertEquals(Double.valueOf(21), flagStatData.getQcFailedReads().secondary());
        assertEquals(Double.valueOf(22), flagStatData.getQcFailedReads().supplementary());
        assertEquals(Double.valueOf(23), flagStatData.getQcFailedReads().duplicates());
        assertEquals(Double.valueOf(24), flagStatData.getQcFailedReads().mapped());
        assertEquals(Double.valueOf(25), flagStatData.getQcFailedReads().pairedInSequencing());
        assertEquals(Double.valueOf(26), flagStatData.getQcFailedReads().read1());
        assertEquals(Double.valueOf(27), flagStatData.getQcFailedReads().read2());
        assertEquals(Double.valueOf(28), flagStatData.getQcFailedReads().properlyPaired());
        assertEquals(Double.valueOf(29), flagStatData.getQcFailedReads().itselfAndMateMapped());
        assertEquals(Double.valueOf(30), flagStatData.getQcFailedReads().singletons());
        assertEquals(Double.valueOf(31), flagStatData.getQcFailedReads().mateMappedToDifferentChr());
        assertEquals(Double.valueOf(32), flagStatData.getQcFailedReads().mateMappedToDifferentChrMapQ5());
    }

    @Test(expected = EmptyFileException.class) public void canParseEmptyFile() throws IOException, EmptyFileException {
        final URL exampleFlagStatURL = Resources.getResource(
                "emptyFiles/CPCT12345678R/mapping/CPCT12345678R_FLOWCELL_S2_L001_001.flagstat");
        final String exampleFlagStatFile = exampleFlagStatURL.getPath();
        final FlagStatParser parser = new SambambaFlagStatParser();
        parser.parse(exampleFlagStatFile);
    }
}
