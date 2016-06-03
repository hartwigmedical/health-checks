package com.hartwig.healthchecks.boggs.io;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.boggs.PatientData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatParser;
import mockit.Mocked;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

public class PatientExtractorTest {

    @Mocked
    FlagStatData flagStatData;

    @Test
    public void canProcessRunDirectoryStructure() throws IOException {
        URL runDirURL = Resources.getResource("rundir");
        PatientExtractor extractor = new PatientExtractor(new DummyFlagstatParser());
        PatientData patient = extractor.extractFromRunDirectory(runDirURL.getPath());

        assertNotNull(patient);
    }

    class DummyFlagstatParser implements FlagStatParser {

        @NotNull
        public FlagStatData parse(@NotNull File file) throws IOException {
            return flagStatData;
        }
    }
}
