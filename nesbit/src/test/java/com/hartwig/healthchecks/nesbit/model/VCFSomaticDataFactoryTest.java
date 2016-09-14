package com.hartwig.healthchecks.nesbit.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VCFSomaticDataFactoryTest {

    @Test
    public void incorrectAFFieldYieldsNaN() {
        final String line = "0 \t 1 \t 2 \t 3 \t 4 \t 5 \t 6 \t ;set=Intersection; \t 8 \t 0/1:60:113";

        final VCFSomaticData data = VCFSomaticDataFactory.fromVCFLine(line);
        assertEquals(Double.NaN, data.alleleFrequency(), 1.0e-10);
    }
}