package com.hartwig.healthchecks.common.io.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;

public class VCFFilteredReaderTest {

    private static final String CHROM = "#CHROM";

    private static final String DUMMY_VALUE = "bla";

    private static final String WRONG_NUM_LINES = "Wrong # of Lines";

    private static final String NOT_NULL = "Should Not Be null";

    private static final String TEST_DIR = "rundir";

    private static final String EMPTY_FILES = "emptyFiles";

    private static final String EMPTY_DIR = "empty";

    private static final String EXT = "_Cosmicv76_GoNLv5_sliced.vcf";

    @Test
    public void readHeaderLine() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(TEST_DIR);
        final ExtensionFinderAndLineReader reader = ExtensionFinderAndLineReader.build();
        final Predicate<String> headerPredicate = createHeaderPredicate(CHROM);
        final List<String> readLines = reader.readLines(testPath.getPath(), EXT, headerPredicate);
        assertNotNull(NOT_NULL, readLines);
        assertEquals(WRONG_NUM_LINES, 1, readLines.size());
    }

    @Test(expected = FileNotFoundException.class)
    public void readFileNotFound() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(EMPTY_DIR);
        final ExtensionFinderAndLineReader reader = ExtensionFinderAndLineReader.build();
        final Predicate<String> headerPredicate = createHeaderPredicate(CHROM);
        reader.readLines(testPath.getPath(), EXT, headerPredicate);
    }

    @Test(expected = LineNotFoundException.class)
    public void readLineNotFound() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(TEST_DIR);
        final ExtensionFinderAndLineReader reader = ExtensionFinderAndLineReader.build();
        final Predicate<String> headerPredicate = createHeaderPredicate(DUMMY_VALUE);
        reader.readLines(testPath.getPath(), EXT, headerPredicate);
    }

    private Predicate<String> createHeaderPredicate(final String filter) {
        final Predicate<String> headerPredicate = new Predicate<String>() {

            @Override
            public boolean test(final String line) {
                return line.startsWith(filter);
            }
        };
        return headerPredicate;
    }

}
