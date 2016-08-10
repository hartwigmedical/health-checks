package com.hartwig.healthchecks.common.io.path;

import static org.junit.Assert.assertEquals;

import com.hartwig.healthchecks.common.exception.MalformedRunDirException;

import org.junit.Test;

public class RunPathDataTest {

    @Test
    public void resolveCorrectSampleNames() throws MalformedRunDirException {
        String correctRunDir = "160101_HMFRegCPCT_FR10002000_FR20003000_CPCT12345678";
        RunPathData runPathData = RunPathData.fromRunDirectory(correctRunDir);
        assertEquals("CPCT12345678R", runPathData.getRefSample());
        assertEquals("CPCT12345678T", runPathData.getTumorSample());
    }

    @Test(expected = MalformedRunDirException.class)
    public void exceptionOnRandomRunDir() throws MalformedRunDirException {
        RunPathData.fromRunDirectory("RandomRunDir");
    }

    @Test(expected = MalformedRunDirException.class)
    public void exceptionOnRunDirWithTooShortPatientName() throws MalformedRunDirException {
        RunPathData.fromRunDirectory("RunDir_CPCT123456");
    }
}