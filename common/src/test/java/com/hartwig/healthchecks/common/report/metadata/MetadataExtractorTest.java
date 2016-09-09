package com.hartwig.healthchecks.common.report.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.Collections;
import java.util.function.Predicate;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.path.PathRegexFinder;
import com.hartwig.healthchecks.common.io.reader.LineReader;

import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;

public class MetadataExtractorTest {

    private static final String EXPECTED_VERSION = "v1.7";
    private static final String EXPECTED_DATE = "2016-Jul-09T15.41.42";

    private static final String PIPELINE_VERSION_V1_7 = "Pipeline version: v1.7";
    private static final String DATA =
            "End Kinship\tSat Jul 9 15:41:42 CEST 2016\t" + "run.filtered_variants.vcf\t" + "machine-foxtrot1-15";

    private static final String NON_EXISTING_DIRECTORY = "bla";
    private static final String RUN_DIRECTORY = "run";

    @Mocked
    private PathRegexFinder pathFinder;

    @Mocked
    private LineReader lineReader;

    @Test
    public void extractMetaData() throws IOException, HealthChecksException {
        final MetadataExtractor extractor = new MetadataExtractor(pathFinder, lineReader);
        new Expectations() {
            {
                pathFinder.findPath(anyString, anyString);
                returns(new File(RUN_DIRECTORY).toPath());
                lineReader.readLines(new File(RUN_DIRECTORY).toPath(), (Predicate<String>) any);
                returns(Collections.singletonList(DATA));

                pathFinder.findPath(anyString, anyString);
                returns(new File(RUN_DIRECTORY).toPath());
                lineReader.readLines(new File(RUN_DIRECTORY).toPath(), (Predicate<String>) any);
                returns(Collections.singletonList(PIPELINE_VERSION_V1_7));
            }
        };
        final ReportMetadata reportMetadata = extractor.extractMetadata(RUN_DIRECTORY);
        assertNotNull(reportMetadata);

        assertEquals(EXPECTED_DATE, reportMetadata.getDate());
        assertEquals(EXPECTED_VERSION, reportMetadata.getPipelineVersion());
    }

    @Test
    public void extractMetaDataFullPath() throws IOException, HealthChecksException {
        final MetadataExtractor extractor = new MetadataExtractor(pathFinder, lineReader);
        new Expectations() {
            {
                pathFinder.findPath(anyString, anyString);
                returns(new File(RUN_DIRECTORY).toPath());
                lineReader.readLines(new File(RUN_DIRECTORY).toPath(), (Predicate<String>) any);
                returns(Collections.singletonList(DATA));

                pathFinder.findPath(anyString, anyString);
                returns(new File(RUN_DIRECTORY).toPath());
                lineReader.readLines(new File(RUN_DIRECTORY).toPath(), (Predicate<String>) any);
                returns(Collections.singletonList(PIPELINE_VERSION_V1_7));
            }
        };
        final URL testPath = Resources.getResource(RUN_DIRECTORY);
        final ReportMetadata reportMetadata = extractor.extractMetadata(testPath.getPath());

        assertNotNull(reportMetadata);
        assertEquals(EXPECTED_DATE, reportMetadata.getDate());
        assertEquals(EXPECTED_VERSION, reportMetadata.getPipelineVersion());
    }

    @Test(expected = FileNotFoundException.class)
    public void extractMetaDataEmptyFolder() throws IOException, HealthChecksException {
        final MetadataExtractor extractor = new MetadataExtractor(pathFinder, lineReader);
        new Expectations() {
            {
                pathFinder.findPath(anyString, anyString);
                result = new FileNotFoundException();
            }
        };
        extractor.extractMetadata(RUN_DIRECTORY);
    }

    @Test(expected = NoSuchFileException.class)
    public void extractMetaDataDummyFolder() throws IOException, HealthChecksException {
        final MetadataExtractor extractor = new MetadataExtractor(pathFinder, lineReader);
        new Expectations() {
            {
                pathFinder.findPath(anyString, anyString);
                result = new NoSuchFileException("");
            }
        };
        extractor.extractMetadata(NON_EXISTING_DIRECTORY);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractMetaDataEmptyFiles() throws IOException, HealthChecksException {
        final MetadataExtractor extractor = new MetadataExtractor(pathFinder, lineReader);
        new Expectations() {
            {
                pathFinder.findPath(anyString, anyString);
                returns(new File(NON_EXISTING_DIRECTORY).toPath());
                lineReader.readLines(new File(NON_EXISTING_DIRECTORY).toPath(), (Predicate<String>) any);
                result = new LineNotFoundException("", "");

            }
        };
        extractor.extractMetadata(RUN_DIRECTORY);
    }
}
