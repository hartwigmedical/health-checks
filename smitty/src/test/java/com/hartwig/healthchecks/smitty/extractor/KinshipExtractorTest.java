package com.hartwig.healthchecks.smitty.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.MalformedFileException;
import com.hartwig.healthchecks.common.io.reader.FileFinderAndReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SingleValueReport;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;

public class KinshipExtractorTest {

    private static final String THIRD_LINE = "CPCT12345678T\tCPCT12345678T\tCPCT12345678TII\tCPCT12345678TII\t157\t0.146\t0.0573\t-0.0042";
    private static final String FIRST_LINE = "FID1\tID1\tFID2\tID2\tN_SNP\tHetHet\tIBS0\tKinship";
    private static final String TEST_DIR = "Test";
    private static final String WRONG_DATA = "Wrong Data";
    private static final String EXPECTED_VALUE_R = "-0.0042";
    private static final String SHOULD_NOT_BE_NULL = "Should Not be Null";

    private List<String> lines;
    private List<String> malformedLines;

    @Mocked
    private FileFinderAndReader kinshipReader;

    @Before
    public void setUp() {
        lines = new ArrayList<>();
        lines.add(FIRST_LINE);
        lines.add(THIRD_LINE);
        malformedLines = new ArrayList<>();
        malformedLines.addAll(lines);
        malformedLines.add(FIRST_LINE);
    }

    @Test
    public void extractDataFromKinship() throws IOException, HealthChecksException {
        final KinshipExtractor kinshipExtractor = new KinshipExtractor(kinshipReader);
        new Expectations() {
            {
                kinshipReader.readLines(anyString, anyString);
                returns(lines);
            }
        };
        final BaseReport kinshipReport = kinshipExtractor.extractFromRunDirectory(TEST_DIR);
        assertEquals("Report with wrong type", CheckType.KINSHIP, kinshipReport.getCheckType());

        assertNotNull(SHOULD_NOT_BE_NULL, kinshipReport);
        assertKinshipData((SingleValueReport) kinshipReport, EXPECTED_VALUE_R);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataFromEmptyKinship() throws IOException, HealthChecksException {
        final KinshipExtractor kinshipExtractor = new KinshipExtractor(kinshipReader);
        new Expectations() {
            {
                kinshipReader.readLines(anyString, anyString);
                result = new EmptyFileException("", "");
            }
        };
        kinshipExtractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = IOException.class)
    public void extractDataFromKinshipIoException() throws IOException, HealthChecksException {
        final KinshipExtractor kinshipExtractor = new KinshipExtractor(kinshipReader);
        new Expectations() {
            {
                kinshipReader.readLines(anyString, anyString);
                result = new IOException();
            }
        };
        kinshipExtractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = MalformedFileException.class)
    public void extractDataFromKinshipMalformedFileException() throws IOException, HealthChecksException {
        final KinshipExtractor kinshipExtractor = new KinshipExtractor(kinshipReader);
        new Expectations() {
            {
                kinshipReader.readLines(anyString, anyString);
                returns(malformedLines);
            }
        };
        kinshipExtractor.extractFromRunDirectory(TEST_DIR);
    }

    private static void assertKinshipData(@NotNull final SingleValueReport kinshipReport,
            @NotNull final String expectedValue) {
        final BaseDataReport baseDataReport = kinshipReport.getSampleData();
        assertEquals(WRONG_DATA, expectedValue, baseDataReport.getValue());
    }
}
