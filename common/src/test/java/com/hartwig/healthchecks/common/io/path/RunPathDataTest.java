package com.hartwig.healthchecks.common.io.path;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.exception.MalformedRunDirException;

import org.junit.Test;

public class RunPathDataTest {

    @Test
    public void resolveCorrectPaths() throws MalformedRunDirException, IOException {
        final String dirPath = "160101_HMFregCPCT_FR10002000_FR20003000_CPCT12345678";
        final URL testPath = Resources.getResource(dirPath);
        final String runDirectory = testPath.getPath();

        RunPathData runPathData = RunPathData.fromRunDirectory(runDirectory);
        assertEquals("CPCT12345678R", runPathData.getRefSample());
        assertEquals("CPCT12345678T", runPathData.getTumorSample());

        System.out.println(runPathData.getRefSampleInsertSizeMetricsPath());
    }

    @Test(expected = MalformedRunDirException.class)
    public void exceptionOnRandomRunDir() throws MalformedRunDirException, IOException {
        RunPathData.fromRunDirectory("RandomRunDir");
    }

    @Test(expected = MalformedRunDirException.class)
    public void exceptionOnRunDirWithTooShortPatientName() throws MalformedRunDirException, IOException {
        RunPathData.fromRunDirectory("RunDir_CPCT123456");
    }
}