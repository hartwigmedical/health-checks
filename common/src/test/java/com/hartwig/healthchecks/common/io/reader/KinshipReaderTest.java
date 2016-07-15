package com.hartwig.healthchecks.common.io.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.List;

import org.junit.Test;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.io.reader.Reader;

public class KinshipReaderTest {

    private static final String WRONG_NUM_LINES = "Wrong # of Lines";

    private static final String NOT_NULL = "Should Not Be null";

    private static final String TEST_DIR = "rundir";

    private static final String EMPTY_DIR = "emptyFiles";

    private static final String NO_FILE_DIR = "empty";

    private static final String KINSHIP = ".kinship";

    private static final int EXPECTED_NUM_LINES = 2;

    @Test
    public void readKinship() throws IOException {
        final URL testPath = Resources.getResource(TEST_DIR);

        final Reader reader = Reader.build();
        final List<String> readLines = reader.readLines(testPath.getPath(), KINSHIP);
        assertNotNull(NOT_NULL, readLines);
        assertEquals(WRONG_NUM_LINES, EXPECTED_NUM_LINES, readLines.size());
    }

    @Test
    public void readEmptyKinship() throws IOException {
        final URL testPath = Resources.getResource(EMPTY_DIR);
        final Reader reader = Reader.build();
        final List<String> readLines = reader.readLines(testPath.getPath(), KINSHIP);
        assertNotNull(NOT_NULL, readLines);
        assertEquals(NOT_NULL, 0, readLines.size());
    }

    @Test(expected = FileNotFoundException.class)
    public void readNoKinship() throws IOException {
        final URL testPath = Resources.getResource(NO_FILE_DIR);

        final Reader reader = Reader.build();
        reader.readLines(testPath.getPath(), KINSHIP);
    }

    @Test(expected = NoSuchFileException.class)
    public void readNoneExistingFolder() throws IOException {
        final Reader reader = Reader.build();
        reader.readLines("bla", KINSHIP);
    }
}
