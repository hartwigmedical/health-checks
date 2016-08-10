package com.hartwig.healthchecks.common.io.path;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import com.google.common.io.Resources;

import org.junit.Test;

public class PathRegexFinderTest {

    private static final String DUMMY_DIR = "bla";

    private static final String TEST_DIR = "160101_HMFregCPCT_FR10002000_FR20003000_CPCT12345678";
    private static final String NO_FILE_DIR = "empty";

    private static final String REGEX = "(.*)(_)(CPCT)(\\d+)(\\.)(log)";
    private static final String WRONG_REGEX = "(.*)(_)(CPCT)(\\d+)(\\.)";
    private static final String PIPELINE_LOG_REGEX = "PipelineCheck.log";

    @Test
    public void findPathRunLog() throws IOException {
        final URL testPath = Resources.getResource(TEST_DIR);
        final Path path = PathRegexFinder.build().findPath(testPath.getPath(), TEST_DIR);
        assertNotNull(path);
    }

    @Test
    public void findPathPipelineLog() throws IOException {
        final URL testPath = Resources.getResource(TEST_DIR);
        final Path path = PathRegexFinder.build().findPath(testPath.getPath(), PIPELINE_LOG_REGEX);
        assertNotNull(path);
    }

    @Test(expected = FileNotFoundException.class)
    public void findPathWrongRegex() throws IOException {
        final URL testPath = Resources.getResource(TEST_DIR);
        PathRegexFinder.build().findPath(testPath.getPath(), WRONG_REGEX);
    }

    @Test(expected = FileNotFoundException.class)
    public void findPathEmpty() throws IOException {
        final URL testPath = Resources.getResource(NO_FILE_DIR);
        PathRegexFinder.build().findPath(testPath.getPath(), REGEX);
    }

    @Test(expected = NoSuchFileException.class)
    public void findPathDummyDir() throws IOException {
        PathRegexFinder.build().findPath(DUMMY_DIR, REGEX);
    }
}
