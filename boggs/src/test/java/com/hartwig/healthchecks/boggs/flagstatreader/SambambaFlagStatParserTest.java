package com.hartwig.healthchecks.boggs.flagstatreader;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class SambambaFlagStatParserTest {

    @Test
    public void canParseExampleFile() throws IOException, EmptyFileException {
        final URL exampleFlagStatURL = Resources.getResource("flagstats/example.flagstat");
        final String exampleFlagStatFile = exampleFlagStatURL.getPath();

        final FlagStatParser parser = new SambambaFlagStatParser();
        final FlagStatData flagStatData = parser.parse(exampleFlagStatFile);

        assertEquals(Double.valueOf(0), flagStatData.qcPassedReads().total());
        assertEquals(Double.valueOf(1), flagStatData.qcPassedReads().secondary());
        assertEquals(Double.valueOf(2), flagStatData.qcPassedReads().supplementary());
        assertEquals(Double.valueOf(3), flagStatData.qcPassedReads().duplicates());
        assertEquals(Double.valueOf(4), flagStatData.qcPassedReads().mapped());
        assertEquals(Double.valueOf(5), flagStatData.qcPassedReads().pairedInSequencing());
        assertEquals(Double.valueOf(6), flagStatData.qcPassedReads().read1());
        assertEquals(Double.valueOf(7), flagStatData.qcPassedReads().read2());
        assertEquals(Double.valueOf(8), flagStatData.qcPassedReads().properlyPaired());
        assertEquals(Double.valueOf(9), flagStatData.qcPassedReads().itselfAndMateMapped());
        assertEquals(Double.valueOf(10), flagStatData.qcPassedReads().singletons());
        assertEquals(Double.valueOf(11), flagStatData.qcPassedReads().mateMappedToDifferentChr());
        assertEquals(Double.valueOf(12), flagStatData.qcPassedReads().mateMappedToDifferentChrMapQ5());

        assertEquals(Double.valueOf(20), flagStatData.qcFailedReads().total());
        assertEquals(Double.valueOf(21), flagStatData.qcFailedReads().secondary());
        assertEquals(Double.valueOf(22), flagStatData.qcFailedReads().supplementary());
        assertEquals(Double.valueOf(23), flagStatData.qcFailedReads().duplicates());
        assertEquals(Double.valueOf(24), flagStatData.qcFailedReads().mapped());
        assertEquals(Double.valueOf(25), flagStatData.qcFailedReads().pairedInSequencing());
        assertEquals(Double.valueOf(26), flagStatData.qcFailedReads().read1());
        assertEquals(Double.valueOf(27), flagStatData.qcFailedReads().read2());
        assertEquals(Double.valueOf(28), flagStatData.qcFailedReads().properlyPaired());
        assertEquals(Double.valueOf(29), flagStatData.qcFailedReads().itselfAndMateMapped());
        assertEquals(Double.valueOf(30), flagStatData.qcFailedReads().singletons());
        assertEquals(Double.valueOf(31), flagStatData.qcFailedReads().mateMappedToDifferentChr());
        assertEquals(Double.valueOf(32), flagStatData.qcFailedReads().mateMappedToDifferentChrMapQ5());
    }

    @Test(expected = EmptyFileException.class)
    public void canParseEmptyFile() throws IOException, EmptyFileException {
        final URL exampleFlagStatURL = Resources.getResource("emptyFiles/CPCTEMPTY00R/mapping/CPCT_empty_dedup.realigned.flagstat");
        final String exampleFlagStatFile = exampleFlagStatURL.getPath();
        final FlagStatParser parser = new SambambaFlagStatParser();
        parser.parse(exampleFlagStatFile);
    }
}
