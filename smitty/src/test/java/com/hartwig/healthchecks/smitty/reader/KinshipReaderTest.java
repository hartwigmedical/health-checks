package com.hartwig.healthchecks.smitty.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.List;

import org.junit.Test;

import com.google.common.io.Resources;

public class KinshipReaderTest {

    private static final String WRONG_NUM_LINES = "Wrong # of Lines";

    private static final String NOT_NULL = "Should Not Be null";

    private static final String TEST_DIR = "rundir";

    private static final String EMPTY_DIR = "emptyFiles";

    private static final String NO_FILE_DIR = "empty";

    private static final int EXPECTED_NUM_LINES = 3;

    @Test
    public void readKinship() throws IOException {
        final URL testPath = Resources.getResource(TEST_DIR);
        final KinshipReader kinshipReader = new KinshipReader();
        final List<String> readLines = kinshipReader.readLinesFromKinship(testPath.getPath());
        assertNotNull(NOT_NULL, readLines);
        assertEquals(WRONG_NUM_LINES, EXPECTED_NUM_LINES, readLines.size());
    }

    @Test
    public void readEmptyKinship() throws IOException {
        final URL testPath = Resources.getResource(EMPTY_DIR);
        final KinshipReader kinshipReader = new KinshipReader();
        final List<String> readLines = kinshipReader.readLinesFromKinship(testPath.getPath());
        assertNotNull(NOT_NULL, readLines);
        assertEquals(NOT_NULL, 0, readLines.size());
    }

    @Test(expected = FileNotFoundException.class)
    public void readNoKinship() throws IOException {
        final URL testPath = Resources.getResource(NO_FILE_DIR);
        final KinshipReader kinshipReader = new KinshipReader();
        kinshipReader.readLinesFromKinship(testPath.getPath());
    }

    @Test(expected = NoSuchFileException.class)
    public void readNoneExistingFolder() throws IOException {
        final KinshipReader kinshipReader = new KinshipReader();
        kinshipReader.readLinesFromKinship("bla");
    }
}
