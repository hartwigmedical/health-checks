package com.hartwig.healthchecks.smitty.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.smitty.reader.InsertSizeMetricsReader;
import com.hartwig.healthchecks.smitty.report.InsertSizeMetricsReport;

import mockit.Expectations;
import mockit.Mocked;

public class InsertSizeMetricsExtractorTest {

    private static final String WRONG_PATIENT_ID = "Wrong Patient ID";

    private static final String MEDIAN_INSERT_SIZE = "MAPPING_INSERT_SIZE_MEDIAN";

    private static final String DATA_LINE = "%s\t61\t19\t825\t408.556471\t99.364112\t8376\tFR\t23\t47\t69\t93\t123\t153\t195\t247\t327\t571";

    private static final String HEADER_LINE = "MEDIAN_INSERT_SIZE\tMEDIAN_ABSOLUTE_DEVIATION\tMIN_INSERT_SIZE\t"
                    + "MAX_INSERT_SIZE\tMEAN_INSERT_SIZE\tSTANDARD_DEVIATION\tREAD_PAIRS\tPAIR_ORIENTATION\t"
                    + "WIDTH_OF_10_PERCENT\tWIDTH_OF_20_PERCENT\tWIDTH_OF_30_PERCENT\tWIDTH_OF_40_PERCENT\t"
                    + "WIDTH_OF_50_PERCENT\tWIDTH_OF_60_PERCENT\tWIDTH_OF_70_PERCENT\tWIDTH_OF_80_PERCENT\t"
                    + "WIDTH_OF_90_PERCENT\tWIDTH_OF_99_PERCENT\tSAMPLE\tLIBRARY\tREAD_GROUP";

    private static final String INPUT_LINE = "# picard.analysis.CollectMultipleMetrics "
                    + "INPUT=/sample/output/cancerPanel/%s/mapping/%s_dedup.bam " + "ASSUME_SORTED=true "
                    + "OUTPUT=/sample/output/cancerPanel/QCStats//%s_dedup/" + "%s_dedup_MultipleMetrics.txt "
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

    private static final String EXPECTED_VALUE_R = "409";

    private static final String EXPECTED_VALUE_T = "309";

    private static final String SHOULD_NOT_BE_NULL = "Should Not be Null";

    private List<String> refLines;

    private List<String> tumLines;

    private List<String> emptyLines;

    @Mocked
    private InsertSizeMetricsReader reader;

    @Before
    public void setUp() {

        String inputLine = String.format(INPUT_LINE, PATIENT_ID_R, PATIENT_ID_R, PATIENT_ID_R, PATIENT_ID_R);
        String dataLine = String.format(DATA_LINE, EXPECTED_VALUE_R);
        refLines = Arrays.asList(FILLING_LINE, inputLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, HEADER_LINE,
                        dataLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);

        inputLine = String.format(INPUT_LINE, PATIENT_ID_T, PATIENT_ID_T, PATIENT_ID_T, PATIENT_ID_T);
        dataLine = String.format(DATA_LINE, EXPECTED_VALUE_T);
        tumLines = Arrays.asList(FILLING_LINE, inputLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, HEADER_LINE,
                        dataLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);

        emptyLines = new ArrayList<>();
    }

    @Test
    public void extractDataFromFile() throws IOException, HealthChecksException {

        final InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString, anyString);
                returns(refLines, tumLines);
            }
        };
        final BaseReport report = extractor.extractFromRunDirectory(TEST_DIR);
        assertEquals("Report with wrong type", CheckType.INSERT_SIZE, report.getCheckType());
        assertNotNull(SHOULD_NOT_BE_NULL, report);
        assertBaseData(((InsertSizeMetricsReport) report).getReferenceSample(), PATIENT_ID_R, MEDIAN_INSERT_SIZE,
                        EXPECTED_VALUE_R);
        assertBaseData(((InsertSizeMetricsReport) report).getTumorSample(), PATIENT_ID_T, MEDIAN_INSERT_SIZE,
                        EXPECTED_VALUE_T);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataFromEmptyFile() throws IOException, HealthChecksException {
        final InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString, anyString);
                returns(emptyLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = IOException.class)
    public void extractDataFromFileIoException() throws IOException, HealthChecksException {
        final InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString, anyString);
                result = new IOException();
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundRefException() throws IOException, HealthChecksException {
        final InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString, anyString);
                result = new LineNotFoundException("");
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundTumException() throws IOException, HealthChecksException {
        final InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(reader);
        new Expectations() {

            {
                reader.readLines(anyString, anyString, anyString);
                returns(refLines);
                reader.readLines(anyString, anyString, anyString);
                result = new LineNotFoundException("");
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    private void assertBaseData(final List<BaseDataReport> reports, final String patientId, final String check,
                    final String expectedValue) {
        final BaseDataReport value = reports.stream().filter(p -> p.getCheckName().equals(check)).findFirst().get();
        assertEquals(WRONG_DATA, expectedValue, value.getValue());
        assertEquals(WRONG_PATIENT_ID, patientId, value.getPatientId());

    }
}
