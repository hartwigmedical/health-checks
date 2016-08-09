package com.hartwig.healthchecks.flint.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.path.SamplePathData;
import com.hartwig.healthchecks.common.io.reader.SampleFinderAndReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;

public class InsertSizeMetricsExtractorTest {

    private static final String WRONG_PATIENT_ID = "Wrong Patient ID";
    private static final String DATA_LINE = "%s\t61\t19\t825\t408.556471\t99.364112\t8376\tFR\t23\t47\t69\t93\t123\t153\t195\t%s\t327\t571\t\t\t\t";

    private static final String HEADER_LINE = "MEDIAN_INSERT_SIZE\tMEDIAN_ABSOLUTE_DEVIATION\tMIN_INSERT_SIZE\t"
            + "MAX_INSERT_SIZE\tMEAN_INSERT_SIZE\tSTANDARD_DEVIATION\tREAD_PAIRS\tPAIR_ORIENTATION\t"
            + "WIDTH_OF_10_PERCENT\tWIDTH_OF_20_PERCENT\tWIDTH_OF_30_PERCENT\tWIDTH_OF_40_PERCENT\t"
            + "WIDTH_OF_50_PERCENT\tWIDTH_OF_60_PERCENT\tWIDTH_OF_70_PERCENT\tWIDTH_OF_80_PERCENT\t"
            + "WIDTH_OF_90_PERCENT\tWIDTH_OF_99_PERCENT\tSAMPLE\tLIBRARY\tREAD_GROUP";

    private static final String INPUT_LINE =
            "# picard.analysis.CollectMultipleMetrics " + "INPUT=/sample/output/cancerPanel/%s/mapping/%s_dedup.bam "
                    + "ASSUME_SORTED=true " + "OUTPUT=/sample/output/cancerPanel/QCStats//%s_dedup/"
                    + "%s_dedup_MultipleMetrics.txt "
                    + "PROGRAM=[CollectAlignmentSummaryMetrics, CollectBaseDistributionByCycle,"
                    + " CollectInsertSizeMetrics, MeanQualityByCycle, QualityScoreDistribution, "
                    + "CollectAlignmentSummaryMetrics, CollectInsertSizeMetrics, "
                    + "QualityScoreDistribution, QualityScoreDistribution, CollectGcBiasMetrics] "
                    + "TMP_DIR=[/sample/output/cancerPanel/QCStats/tmp] "
                    + "REFERENCE_SEQUENCE=/data/GENOMES/Homo_sapiens.GRCh37.GATK.illumina/"
                    + "Homo_sapiens.GRCh37.GATK.illumina.fasta    "
                    + "STOP_AFTER=0 METRIC_ACCUMULATION_LEVEL=[ALL_READS] "
                    + "VERBOSITY=INFO QUIET=false VALIDATION_STRINGENCY=STRICT "
                    + "COMPRESSION_LEVEL=5 MAX_RECORDS_IN_RAM=500000 "
                    + "CREATE_INDEX=false CREATE_MD5_FILE=false GA4GH_CLIENT_SECRETS=client_secrets.json";

    private static final String FILLING_LINE = "bla\tbla\tbla\tbla\tbla\tbla\tbla\tbla\tbla\tbla";

    private static final String TEST_DIR = "Test";
    private static final String WRONG_DATA = "Wrong Data";
    private static final String PATIENT_ID_R = "CPCT12345678R";
    private static final String PATIENT_ID_T = "CPCT12345678T";
    private static final String MEDIAN_INS_SZ_R = "409";
    private static final String MEDIAN_INS_SZ_T = "309";
    private static final String WIDTH_OF_70_PER_R = "247";
    private static final String WIDTH_OF_70_PER_T = "147";
    private static final String SHOULD_NOT_BE_NULL = "Should Not be Null";

    private List<String> refLines;
    private List<String> tumLines;
    private List<String> missingLines;

    @Mocked
    private SampleFinderAndReader reader;

    @Before
    public void setUp() {
        String inputLine = String.format(INPUT_LINE, PATIENT_ID_R, PATIENT_ID_R, PATIENT_ID_R, PATIENT_ID_R);
        String dataLine = String.format(DATA_LINE, MEDIAN_INS_SZ_R, WIDTH_OF_70_PER_R);
        refLines = Arrays.asList(FILLING_LINE, inputLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, HEADER_LINE,
                dataLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);

        inputLine = String.format(INPUT_LINE, PATIENT_ID_T, PATIENT_ID_T, PATIENT_ID_T, PATIENT_ID_T);
        dataLine = String.format(DATA_LINE, MEDIAN_INS_SZ_T, WIDTH_OF_70_PER_T);
        tumLines = Arrays.asList(FILLING_LINE, inputLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, HEADER_LINE,
                dataLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);

        missingLines = Arrays.asList(FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, HEADER_LINE,
                dataLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);
    }

    @Test
    public void extractDataFromFile() throws IOException, HealthChecksException {
        final InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(reader);
        new Expectations() {
            {
                reader.readLines((SamplePathData) any);
                returns(refLines, tumLines);
            }
        };
        final BaseReport report = extractor.extractFromRunDirectory(TEST_DIR);
        assertReport(report);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataFromEmptyFile() throws IOException, HealthChecksException {
        final InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(reader);
        new Expectations() {
            {
                reader.readLines((SamplePathData) any);
                result = new EmptyFileException("", "");
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = IOException.class)
    public void extractDataFromFileIoException() throws IOException, HealthChecksException {
        final InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(reader);
        new Expectations() {
            {
                reader.readLines((SamplePathData) any);
                result = new IOException();
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundRefExceptionFirstFile() throws IOException, HealthChecksException {
        final InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(reader);
        new Expectations() {
            {
                reader.readLines((SamplePathData) any);
                returns(missingLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundRefException() throws IOException, HealthChecksException {
        final InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(reader);
        new Expectations() {
            {
                reader.readLines((SamplePathData) any);
                returns(refLines, missingLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundTumException() throws IOException, HealthChecksException {
        final InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(reader);
        new Expectations() {
            {
                reader.readLines((SamplePathData) any);
                returns(refLines);
                reader.readLines((SamplePathData) any);
                result = new LineNotFoundException("", "");
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    private void assertReport(@NotNull final BaseReport report) {
        assertEquals("Report with wrong type", CheckType.INSERT_SIZE, report.getCheckType());
        assertNotNull(SHOULD_NOT_BE_NULL, report);
        assertField(report, InsertSizeMetricsCheck.MAPPING_MEDIAN_INSERT_SIZE.toString(), MEDIAN_INS_SZ_R,
                MEDIAN_INS_SZ_T);
        assertField(report, InsertSizeMetricsCheck.MAPPING_WIDTH_OF_70_PERCENT.toString(), WIDTH_OF_70_PER_R,
                WIDTH_OF_70_PER_T);
    }

    private void assertField(@NotNull final BaseReport report, @NotNull final String field,
            @NotNull final String refValue, @NotNull final String tumValue) {
        assertBaseData(((SampleReport) report).getReferenceSample(), PATIENT_ID_R, field, refValue);
        assertBaseData(((SampleReport) report).getTumorSample(), PATIENT_ID_T, field, tumValue);
    }

    private void assertBaseData(@NotNull final List<BaseDataReport> reports, @NotNull final String patientId,
            @NotNull final String check, @NotNull final String expectedValue) {
        final Optional<BaseDataReport> value = reports.stream().filter(
                p -> p.getCheckName().equals(check)).findFirst();
        assert value.isPresent();

        assertEquals(WRONG_DATA, expectedValue, value.get().getValue());
        assertEquals(WRONG_PATIENT_ID, patientId, value.get().getSampleId());
    }
}
