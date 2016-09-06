package com.hartwig.healthchecks.common.io.path;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import com.google.common.io.Resources;

import org.junit.Test;

public class PathsExtensionFinderTest {

    private static final String RUN_DIRECTORY = "run";
    private static final String LOG_DIRECTORY = "logs";

    private static final String EXISTING_EXTENSION = ".log";
    private static final String NON_EXISTING_EXTENSION = ".bla";

    @Test
    public void findPaths() throws IOException {
        final URL testPath = Resources.getResource(RUN_DIRECTORY + File.separator + LOG_DIRECTORY + File.separator);
        final List<Path> paths = PathsExtensionFinder.build().findPaths(testPath.getPath(), EXISTING_EXTENSION);
        assertTrue(!paths.isEmpty());
    }

    @Test(expected = FileNotFoundException.class)
    public void findPathsFilesNotFound() throws IOException {
        final URL testPath = Resources.getResource(RUN_DIRECTORY + File.separator + LOG_DIRECTORY + File.separator);
        PathsExtensionFinder.build().findPaths(testPath.getPath(), NON_EXISTING_EXTENSION);
    }
}
