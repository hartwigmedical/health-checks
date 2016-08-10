package com.hartwig.healthchecks.common.io.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.List;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.SamplePathData;

import org.junit.Test;

public class SummaryMetricsReaderTest {

    private static final String DUMMY_DIR = "bla";
    private static final String UNDER_SCORE = "_";
    private static final String QC_STATS = "QCStats";
    private static final String WRONG_NUM_LINES = "Wrong # of Lines";
    private static final String NOT_NULL = "Should Not Be null";
    private static final String TEST_DIR = "160101_HMFregCPCT_FR10002000_FR20003000_CPCT12345678";
    private static final String EMPTY_DIR = "emptyFiles";
    private static final String NO_FILE_DIR = "empty";
    private static final int EXPECTED_NUM_LINES = 12;
    private static final String SAMPLE_PREFIX = "CPCT";
    private static final String REF_DED_SAMPLE_SUFFIX = "R";
    private static final String TUM_DED_SAMPLE_SUFFIX = "T";
    private static final String DEDUP_SAMPLE_SUFFIX = "dedup";
    private static final String AL_SUM_METRICS = ".alignment_summary_metrics";

    @Test
    public void readLines() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(TEST_DIR + File.separator + QC_STATS);
        final String suffix = REF_DED_SAMPLE_SUFFIX + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final SamplePathData samplePath = new SamplePathData(testPath.getPath(), SAMPLE_PREFIX, suffix, AL_SUM_METRICS);
        final SampleFinderAndReader reader = SampleFinderAndReader.build();
        final List<String> readLines = reader.readLines(samplePath);
        assertNotNull(NOT_NULL, readLines);
        assertEquals(WRONG_NUM_LINES, EXPECTED_NUM_LINES, readLines.size());
    }

    @Test(expected = EmptyFileException.class)
    public void readEmptyFile() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(EMPTY_DIR + File.separator + QC_STATS);
        final String suffix = REF_DED_SAMPLE_SUFFIX + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final SamplePathData samplePath = new SamplePathData(testPath.getPath(), SAMPLE_PREFIX, suffix, AL_SUM_METRICS);
        final SampleFinderAndReader reader = SampleFinderAndReader.build();
        reader.readLines(samplePath);
    }

    @Test(expected = FileNotFoundException.class)
    public void readNoFileDir() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(NO_FILE_DIR);
        final String suffix = REF_DED_SAMPLE_SUFFIX + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final SamplePathData samplePath = new SamplePathData(testPath.getPath(), SAMPLE_PREFIX, suffix, AL_SUM_METRICS);
        final SampleFinderAndReader reader = SampleFinderAndReader.build();
        reader.readLines(samplePath);
    }

    @Test(expected = FileNotFoundException.class)
    public void readNoFile() throws IOException, HealthChecksException {
        final URL testPath = Resources.getResource(NO_FILE_DIR + File.separator + QC_STATS);
        final String suffix = TUM_DED_SAMPLE_SUFFIX + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final SamplePathData samplePath = new SamplePathData(testPath.getPath(), SAMPLE_PREFIX, suffix, AL_SUM_METRICS);
        final SampleFinderAndReader reader = SampleFinderAndReader.build();
        reader.readLines(samplePath);
    }

    @Test(expected = NoSuchFileException.class)
    public void readNoneExistingFolder() throws IOException, HealthChecksException {
        final String suffix = REF_DED_SAMPLE_SUFFIX + UNDER_SCORE + DEDUP_SAMPLE_SUFFIX;
        final SamplePathData samplePath = new SamplePathData(DUMMY_DIR, SAMPLE_PREFIX, suffix, AL_SUM_METRICS);
        final SampleFinderAndReader reader = SampleFinderAndReader.build();
        reader.readLines(samplePath);
    }
}
