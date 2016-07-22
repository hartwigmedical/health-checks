package com.hartwig.healthchecks.common.io.path;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

import com.google.common.io.Resources;

public class PathsExtensionFinderTest {

    private static final String PATIENT = "CPCT12345678T";

    private static final String ZIP = ".zip";

    private static final String TEST_DIR = "rundir";

    protected static final String QC_STATS = "QCStats";

    @Test
    public void findPaths() throws IOException {
        final URL testPath = Resources
                        .getResource(TEST_DIR + File.separator + PATIENT + File.separator + QC_STATS + File.separator);
        final List<Path> paths = PathsExtensionFinder.build().findPaths(testPath.getPath(), ZIP);
        assertTrue("Files Not Found", !paths.isEmpty());
    }

    @Test(expected = FileNotFoundException.class)
    public void findPathsFilesNotFound() throws IOException {
        final URL testPath = Resources
                        .getResource(TEST_DIR + File.separator + PATIENT + File.separator + QC_STATS + File.separator);
        PathsExtensionFinder.build().findPaths(testPath.getPath(), ".bla");
    }
}
