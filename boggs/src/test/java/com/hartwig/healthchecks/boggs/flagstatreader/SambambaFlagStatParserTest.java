package com.hartwig.healthchecks.boggs.flagstatreader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

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

        assertEquals(Double.valueOf(0), flagStatData.getQcPassedReads().getTotal());
        assertEquals(Double.valueOf(1), flagStatData.getQcPassedReads().getSecondary());
        assertEquals(Double.valueOf(2), flagStatData.getQcPassedReads().getSupplementary());
        assertEquals(Double.valueOf(3), flagStatData.getQcPassedReads().getDuplicates());
        assertEquals(Double.valueOf(4), flagStatData.getQcPassedReads().getMapped());
        assertEquals(Double.valueOf(5), flagStatData.getQcPassedReads().getPairedInSequencing());
        assertEquals(Double.valueOf(6), flagStatData.getQcPassedReads().getRead1());
        assertEquals(Double.valueOf(7), flagStatData.getQcPassedReads().getRead2());
        assertEquals(Double.valueOf(8), flagStatData.getQcPassedReads().getProperlyPaired());
        assertEquals(Double.valueOf(9), flagStatData.getQcPassedReads().getItselfAndMateMapped());
        assertEquals(Double.valueOf(10), flagStatData.getQcPassedReads().getSingletons());
        assertEquals(Double.valueOf(11), flagStatData.getQcPassedReads().getMateMappedToDifferentChr());
        assertEquals(Double.valueOf(12), flagStatData.getQcPassedReads().getMateMappedToDifferentChrMapQ5());

        assertEquals(Double.valueOf(20), flagStatData.getQcFailedReads().getTotal());
        assertEquals(Double.valueOf(21), flagStatData.getQcFailedReads().getSecondary());
        assertEquals(Double.valueOf(22), flagStatData.getQcFailedReads().getSupplementary());
        assertEquals(Double.valueOf(23), flagStatData.getQcFailedReads().getDuplicates());
        assertEquals(Double.valueOf(24), flagStatData.getQcFailedReads().getMapped());
        assertEquals(Double.valueOf(25), flagStatData.getQcFailedReads().getPairedInSequencing());
        assertEquals(Double.valueOf(26), flagStatData.getQcFailedReads().getRead1());
        assertEquals(Double.valueOf(27), flagStatData.getQcFailedReads().getRead2());
        assertEquals(Double.valueOf(28), flagStatData.getQcFailedReads().getProperlyPaired());
        assertEquals(Double.valueOf(29), flagStatData.getQcFailedReads().getItselfAndMateMapped());
        assertEquals(Double.valueOf(30), flagStatData.getQcFailedReads().getSingletons());
        assertEquals(Double.valueOf(31), flagStatData.getQcFailedReads().getMateMappedToDifferentChr());
        assertEquals(Double.valueOf(32), flagStatData.getQcFailedReads().getMateMappedToDifferentChrMapQ5());
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
