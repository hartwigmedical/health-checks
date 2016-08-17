package com.hartwig.healthchecks.common.io.path;

import java.io.IOException;
import java.net.URL;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.exception.MalformedRunDirException;

import org.junit.Test;

public class RunContextFactoryTest {

    @Test
    public void resolveCorrectPaths() throws MalformedRunDirException, IOException {
        final String dirPath = "160101_HMFregCPCT_FR10002000_FR20003000_CPCT12345678";
        final URL testPath = Resources.getResource(dirPath);
        final String runDirectory = testPath.getPath();

        RunContext runContext = RunContextFactory.fromRunDirectory(runDirectory);
        //        assertEquals("CPCT12345678R", runContext.getRefSample());
        //        assertEquals("CPCT12345678T", runContext.getTumorSample());

        //        System.out.println(runContext());
    }

    @Test(expected = MalformedRunDirException.class)
    public void exceptionOnRandomRunDir() throws MalformedRunDirException, IOException {
        RunContextFactory.fromRunDirectory("RandomRunDir");
    }

    @Test(expected = MalformedRunDirException.class)
    public void exceptionOnRunDirWithTooShortPatientName() throws MalformedRunDirException, IOException {
        RunContextFactory.fromRunDirectory("RunDir_CPCT123456");
    }
}