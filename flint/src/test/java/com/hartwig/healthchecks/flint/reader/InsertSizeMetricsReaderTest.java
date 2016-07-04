package com.hartwig.healthchecks.flint.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.List;

import org.junit.Test;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.flint.reader.InsertSizeMetricsReader;

public class InsertSizeMetricsReaderTest {

    private static final String DUMMY_DIR = "bla";

    private static final String UNDER_SCORE = "_";

    private static final String QC_STATS = "QCStats";

    private static final String WRONG_NUM_LINES = "Wrong # of Lines";

    private static final String NOT_NULL = "Should Not Be null";

    private static final String TEST_DIR = "rundir";

    private static final String EMPTY_DIR = "emptyFiles";

    private static final String NO_FILE_DIR = "empty";

    private static final int EXPECTED_NUM_LINES = 620;

    private static final String SAMPLE_PREFIX = "CPCT";

    private static final String REF_DED_SAMPLE_SUFFIX = "R";

    private static final String TUM_DED_SAMPLE_SUFFIX = "T";

    private static final String DEDUP_SAMPLE_SUFFIX = "dedup";

    @Test
    public void readLines() throws IOException {
        final URL testPath = Resources.getResource(TEST_DIR + File.separator + QC_STATS);
        final InsertSizeMetricsReader reader = new InsertSizeMetricsReader();
        final String suffix = REF_DED_SAMPLE_SUFFIX + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final List<String> readLines = reader.readLines(testPath.getPath(), SAMPLE_PREFIX, suffix);
        assertNotNull(NOT_NULL, readLines);
        assertEquals(WRONG_NUM_LINES, EXPECTED_NUM_LINES, readLines.size());
    }

    @Test
    public void readEmptyFile() throws IOException {
        final URL testPath = Resources.getResource(EMPTY_DIR + File.separator + QC_STATS);
        final InsertSizeMetricsReader reader = new InsertSizeMetricsReader();
        final String suffix = REF_DED_SAMPLE_SUFFIX + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final List<String> readLines = reader.readLines(testPath.getPath(), SAMPLE_PREFIX, suffix);
        assertNotNull(NOT_NULL, readLines);
        assertEquals(NOT_NULL, 0, readLines.size());
    }

    @Test(expected = FileNotFoundException.class)
    public void readNoFileDir() throws IOException {
        final URL testPath = Resources.getResource(NO_FILE_DIR);
        final String suffix = REF_DED_SAMPLE_SUFFIX + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final InsertSizeMetricsReader reader = new InsertSizeMetricsReader();
        reader.readLines(testPath.getPath(), SAMPLE_PREFIX, suffix);
    }

    @Test(expected = FileNotFoundException.class)
    public void readNoFile() throws IOException {
        final URL testPath = Resources.getResource(NO_FILE_DIR + File.separator + QC_STATS);
        final String suffix = TUM_DED_SAMPLE_SUFFIX + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final InsertSizeMetricsReader reader = new InsertSizeMetricsReader();
        reader.readLines(testPath.getPath(), SAMPLE_PREFIX, suffix);
    }

    @Test(expected = NoSuchFileException.class)
    public void readNoneExistingFolder() throws IOException {
        final String suffix = REF_DED_SAMPLE_SUFFIX + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final InsertSizeMetricsReader reader = new InsertSizeMetricsReader();
        reader.readLines(DUMMY_DIR, SAMPLE_PREFIX, suffix);
    }
}
