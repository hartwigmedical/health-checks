package com.hartwig.healthchecks.bile.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.exception.MalformedFileException;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.path.SamplePathFinder;
import com.hartwig.healthchecks.common.io.reader.SamplePath;
import com.hartwig.healthchecks.common.io.reader.SampleReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;

public class RealignerExtractorTest {

    private static final String T_DEDUP = "T_dedup";

    private static final String R_DEDUP = "R_dedup";

    private static final String REF_PATH = "Test/QCStats/CPCT12345678R_dedup";

    private static final String TUM_PATH = "Test/QCStats/CPCT12345678T_dedup";

    private static final String WRONG_PATIENT_ID = "Wrong Patient ID";

    private static final String DIFF_HEADER_LINE = "ST-E00288:19:H5G7WCCXX:7:1223:6481:46262";

    private static final String MAP_REALI_CHAN_ALIGN = "MAPPING_REALIGNER_CHANGED_ALIGNMENTS";

    private static final String M_DIFF_LINE = "<   53  1M1I149M";

    private static final String R_DIFF_LINE = ">   53  151M";

    private static final String SLICED_TOTAL = "10 + 0 in total (QC-passed reads + QC-failed reads)";

    private static final String SLICED_MAPPED = "300 + 0 mapped (99.49%:N/A)";

    private static final String MAPPED_NO_PLUS = "300  0 mapped (99.49%:N/A)";

    private static final String SLICED_FILLER = "10 + 0 properly paired (93.18%:N/A)";

    private static final String TEST_DIR = "Test";

    private static final String WRONG_DATA = "Wrong Data";

    private static final String PATIENT_ID_R = "CPCT12345678R";

    private static final String PATIENT_ID_T = "CPCT12345678T";

    private static final String EXPT_VALUE_R = "0.03333";

    private static final String EXPT_VALUE_T = "0.03000";

    private static final String SHOULD_NOT_BE_NULL = "Should Not be Null";

    private List<String> refDiffLines;

    private List<String> tumDiffLines;

    private List<String> emptyLines;

    private List<String> refSlicLines;

    private List<String> tumSlicLines;

    private List<String> missingLines;

    private List<String> mallformatedLines;

    @Mocked
    private SampleReader reader;

    @Mocked
    private SamplePathFinder samplePathFinder;

    @Before
    public void setUp() {
        refDiffLines = Arrays.asList(DIFF_HEADER_LINE, M_DIFF_LINE, R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE,
                        R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE, R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE,
                        R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE, R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE,
                        R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE, R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE,
                        R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE, R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE,
                        R_DIFF_LINE);

        tumDiffLines = Arrays.asList(DIFF_HEADER_LINE, M_DIFF_LINE, R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE,
                        R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE, R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE,
                        R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE, R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE,
                        R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE, R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE,
                        R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE, R_DIFF_LINE, DIFF_HEADER_LINE, M_DIFF_LINE,
                        R_DIFF_LINE);

        refSlicLines = Arrays.asList(SLICED_TOTAL, SLICED_FILLER, SLICED_FILLER, SLICED_FILLER, SLICED_MAPPED,
                        SLICED_FILLER, SLICED_FILLER, SLICED_FILLER, SLICED_FILLER);
        tumSlicLines = Arrays.asList(SLICED_TOTAL, SLICED_FILLER, SLICED_FILLER, SLICED_FILLER, SLICED_MAPPED,
                        SLICED_FILLER, SLICED_FILLER, SLICED_FILLER, SLICED_FILLER);

        mallformatedLines = Arrays.asList(SLICED_TOTAL, SLICED_FILLER, SLICED_FILLER, SLICED_FILLER, MAPPED_NO_PLUS,
                        SLICED_FILLER, SLICED_FILLER, SLICED_FILLER, SLICED_FILLER);
        emptyLines = new ArrayList<>();
        missingLines = Arrays.asList(SLICED_TOTAL, SLICED_FILLER, SLICED_FILLER, SLICED_FILLER, SLICED_FILLER,
                        SLICED_FILLER, SLICED_FILLER, SLICED_FILLER);
    }

    @Test
    public void extractDataFromFile() throws IOException, HealthChecksException {

        new NonStrictExpectations() {

            {
                samplePathFinder.findPath(anyString, anyString, R_DEDUP);
                returns(new File(REF_PATH).toPath());

                reader.readLines((SamplePath) any);
                returns(refDiffLines, refSlicLines);

                samplePathFinder.findPath(anyString, anyString, T_DEDUP);
                returns(new File(TUM_PATH).toPath());

                reader.readLines((SamplePath) any);
                returns(tumDiffLines, tumSlicLines);
            }
        };

        final DataExtractor extractor = new RealignerExtractor(reader, samplePathFinder);
        final BaseReport report = extractor.extractFromRunDirectory(TEST_DIR);
        assertReport(report);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataFromEmptyRefDiffFile(@Mocked final SamplePathFinder samplePathFinder,
                    @Mocked final SampleReader reader) throws IOException, HealthChecksException {
        final DataExtractor extractor = new RealignerExtractor(reader, samplePathFinder);
        new Expectations() {

            {
                samplePathFinder.findPath(anyString, anyString, R_DEDUP);
                returns(new File(REF_PATH).toPath());

                reader.readLines((SamplePath) any);
                returns(emptyLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataFromEmptyRefSlicFile(@Mocked final SamplePathFinder samplePathFinder,
                    @Mocked final SampleReader reader) throws IOException, HealthChecksException {
        final DataExtractor extractor = new RealignerExtractor(reader, samplePathFinder);
        new Expectations() {

            {
                samplePathFinder.findPath(anyString, anyString, R_DEDUP);
                returns(new File(REF_PATH).toPath());

                reader.readLines((SamplePath) any);
                returns(refDiffLines, emptyLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataFromEmptyTumDiffFile(@Mocked final SamplePathFinder samplePathFinder,
                    @Mocked final SampleReader reader) throws IOException, HealthChecksException {
        final DataExtractor extractor = new RealignerExtractor(reader, samplePathFinder);
        new Expectations() {

            {
                samplePathFinder.findPath(anyString, anyString, R_DEDUP);
                returns(new File(REF_PATH).toPath());

                reader.readLines((SamplePath) any);
                returns(refDiffLines, refSlicLines);

                samplePathFinder.findPath(anyString, anyString, T_DEDUP);
                returns(new File(TUM_PATH).toPath());

                reader.readLines((SamplePath) any);
                returns(emptyLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataFromEmptyTumSliceFile(@Mocked final SamplePathFinder samplePathFinder,
                    @Mocked final SampleReader reader) throws IOException, HealthChecksException {
        final DataExtractor extractor = new RealignerExtractor(reader, samplePathFinder);
        new Expectations() {

            {
                samplePathFinder.findPath(anyString, anyString, R_DEDUP);
                returns(new File(REF_PATH).toPath());

                reader.readLines((SamplePath) any);
                returns(refDiffLines, refSlicLines);

                samplePathFinder.findPath(anyString, anyString, T_DEDUP);
                returns(new File(TUM_PATH).toPath());

                reader.readLines((SamplePath) any);
                returns(tumDiffLines, emptyLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = IOException.class)
    public void extractDataFromFileIoException(@Mocked final SamplePathFinder samplePathFinder,
                    @Mocked final SampleReader reader) throws IOException, HealthChecksException {
        final DataExtractor extractor = new RealignerExtractor(reader, samplePathFinder);
        new Expectations() {

            {
                samplePathFinder.findPath(anyString, anyString, R_DEDUP);
                returns(new File(REF_PATH).toPath());

                reader.readLines((SamplePath) any);
                result = new IOException();
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundRefExceptionFirstFile(@Mocked final SamplePathFinder samplePathFinder,
                    @Mocked final SampleReader reader) throws IOException, HealthChecksException {
        final DataExtractor extractor = new RealignerExtractor(reader, samplePathFinder);
        new Expectations() {

            {
                samplePathFinder.findPath(anyString, anyString, R_DEDUP);
                returns(new File(REF_PATH).toPath());

                reader.readLines((SamplePath) any);
                returns(refDiffLines, missingLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundRefException(@Mocked final SamplePathFinder samplePathFinder,
                    @Mocked final SampleReader reader) throws IOException, HealthChecksException {
        final DataExtractor extractor = new RealignerExtractor(reader, samplePathFinder);
        new Expectations() {

            {
                samplePathFinder.findPath(anyString, anyString, R_DEDUP);
                returns(new File(REF_PATH).toPath());

                reader.readLines((SamplePath) any);
                returns(refDiffLines, missingLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundTumException(@Mocked final SamplePathFinder samplePathFinder,
                    @Mocked final SampleReader reader) throws IOException, HealthChecksException {
        final DataExtractor extractor = new RealignerExtractor(reader, samplePathFinder);
        new Expectations() {

            {
                samplePathFinder.findPath(anyString, anyString, R_DEDUP);
                returns(new File(REF_PATH).toPath());
                reader.readLines((SamplePath) any);
                returns(refDiffLines, refSlicLines);

                samplePathFinder.findPath(anyString, anyString, T_DEDUP);
                returns(new File(TUM_PATH).toPath());
                reader.readLines((SamplePath) any);
                result = new LineNotFoundException("");
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = FileNotFoundException.class)
    public void extractDataLineSamplePathRefFileNotFound(@Mocked final SamplePathFinder samplePathFinder,
                    @Mocked final SampleReader reader) throws IOException, HealthChecksException {
        final DataExtractor extractor = new RealignerExtractor(reader, samplePathFinder);
        new Expectations() {

            {
                samplePathFinder.findPath(anyString, anyString, R_DEDUP);
                result = new FileNotFoundException("");
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = FileNotFoundException.class)
    public void extractDataLineSamplePathTumFileNotFound(@Mocked final SamplePathFinder samplePathFinder,
                    @Mocked final SampleReader reader) throws IOException, HealthChecksException {
        final DataExtractor extractor = new RealignerExtractor(reader, samplePathFinder);
        new Expectations() {

            {
                samplePathFinder.findPath(anyString, anyString, R_DEDUP);
                returns(new File(REF_PATH).toPath());
                reader.readLines((SamplePath) any);
                returns(refDiffLines, refSlicLines);

                samplePathFinder.findPath(anyString, anyString, T_DEDUP);
                result = new FileNotFoundException("");
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = MalformedFileException.class)
    public void extractDataLineMalformated(@Mocked final SamplePathFinder samplePathFinder,
                    @Mocked final SampleReader reader) throws IOException, HealthChecksException {
        final DataExtractor extractor = new RealignerExtractor(reader, samplePathFinder);
        new Expectations() {

            {
                samplePathFinder.findPath(anyString, anyString, R_DEDUP);
                returns(new File(REF_PATH).toPath());
                reader.readLines((SamplePath) any);
                returns(refDiffLines, mallformatedLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    private void assertReport(final BaseReport report) {
        assertEquals("Report with wrong type", CheckType.REALIGNER, report.getCheckType());
        assertNotNull(SHOULD_NOT_BE_NULL, report);
        assertField(report, MAP_REALI_CHAN_ALIGN, EXPT_VALUE_R, EXPT_VALUE_T);
    }

    private void assertField(final BaseReport report, final String field, final String refValue,
                    final String tumValue) {
        assertBaseData(((SampleReport) report).getReferenceSample(), PATIENT_ID_R, field, refValue);
        assertBaseData(((SampleReport) report).getTumorSample(), PATIENT_ID_T, field, tumValue);
    }

    private void assertBaseData(final List<BaseDataReport> reports, final String patientId, final String check,
                    final String expectedValue) {
        final BaseDataReport value = reports.stream().filter(p -> p.getCheckName().equals(check)).findFirst().get();
        assertEquals(WRONG_DATA, expectedValue, value.getValue());
        assertEquals(WRONG_PATIENT_ID, patientId, value.getPatientId());
    }

}
