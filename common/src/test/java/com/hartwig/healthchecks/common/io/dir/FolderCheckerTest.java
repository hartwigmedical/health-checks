package com.hartwig.healthchecks.common.io.dir;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.exception.EmptyFolderException;
import com.hartwig.healthchecks.common.exception.FolderDoesNotExistException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.NotFolderException;

import org.junit.Test;

public class FolderCheckerTest {

    private static final String FILE = "rundir/test.kinship";
    private static final String WRONG_PATH = "Wrong Path";
    private static final String DUMMY_DIR = "bla";
    private static final String NOT_NULL = "Should Not Be null";
    private static final String TEST_DIR = "rundir";
    private static final String NO_FILE_DIR = "empty/QCStats/CPCT12345678T_dedup";

    @Test
    public void checkFolder() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(TEST_DIR);
        final String dirPath = testPath.getPath();
        final String path = FolderChecker.build().checkFolder(dirPath);
        assertNotNull(NOT_NULL, path);
        assertEquals(WRONG_PATH, dirPath, path);
    }

    @Test
    public void checkFolderWithExtraSeperator() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(TEST_DIR);
        final String dirPath = testPath.getPath() + File.separator;
        final String path = FolderChecker.build().checkFolder(dirPath);
        assertNotNull(NOT_NULL, path);
        assertEquals(WRONG_PATH, testPath.getPath(), path);
    }

    @Test(expected = FolderDoesNotExistException.class)
    public void checkFolderNotExist() throws IOException, HealthChecksException {
        FolderChecker.build().checkFolder(DUMMY_DIR);
    }

    @Test(expected = EmptyFolderException.class)
    public void checkEmptyFolder() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(NO_FILE_DIR);
        final String dirPath = testPath.getPath();
        FolderChecker.build().checkFolder(dirPath);
    }

    @Test(expected = NotFolderException.class)
    public void checkNotFolder() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(FILE);
        final String dirPath = testPath.getPath();
        FolderChecker.build().checkFolder(dirPath);
    }
}
