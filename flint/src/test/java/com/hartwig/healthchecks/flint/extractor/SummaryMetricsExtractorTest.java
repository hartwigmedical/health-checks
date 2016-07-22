package com.hartwig.healthchecks.flint.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import com.hartwig.healthchecks.common.io.path.SamplePath;
import com.hartwig.healthchecks.common.io.reader.SampleReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import mockit.Expectations;
import mockit.Mocked;

public class SummaryMetricsExtractorTest {

    private static final String WRONG_PATIENT_ID = "Wrong Patient ID";

    private static final String DATA_LINE = "PAIR\t17920\t17920\t1\t0\t17865\t0.996931\t"
                    + "2678694\t17852\t2677263\t2587040\t0\t%s\t0.005017\t"
                    + "%s\t151\t17810\t0.996921\t0\t%s\t%s\t%s\t\t\t\t";

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

    private static final String MISMATCH_VALUE_R = "0.005024";

    private static final String MISMATCH_VALUE_T = "0.004024";

    private static final String INDEL_VALUE_R = "0.000161";

    private static final String INDEL_VALUE_T = "0.000162";

    private static final String STRAND_VALUE_R = "0.499972";

    private static final String STRAND_VALUE_T = "0.399972";

    private static final String CHIMERA_VALUE_R = "0.000112";

    private static final String CHIMERA_VALUE_T = "0.000212";

    private static final String ADAPTER_VALUE_R = "0.000056";

    private static final String ADAPTER_VALUE_T = "0.000036";

    private static final String SHOULD_NOT_BE_NULL = "Should Not be Null";

    private List<String> refLines;

    private List<String> tumLines;

    private List<String> emptyLines;

    private List<String> missingLines;

    @Mocked
    private SampleReader reader;

    @Before
    public void setUp() {
        String inputLine = String.format(INPUT_LINE, PATIENT_ID_R, PATIENT_ID_R, PATIENT_ID_R, PATIENT_ID_R);
        String dataLine = String.format(DATA_LINE, MISMATCH_VALUE_R, INDEL_VALUE_R, STRAND_VALUE_R, CHIMERA_VALUE_R,
                        ADAPTER_VALUE_R);
        refLines = Arrays.asList(FILLING_LINE, inputLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE,
                        dataLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);

        inputLine = String.format(INPUT_LINE, PATIENT_ID_T, PATIENT_ID_T, PATIENT_ID_T, PATIENT_ID_T);
        dataLine = String.format(DATA_LINE, MISMATCH_VALUE_T, INDEL_VALUE_T, STRAND_VALUE_T, CHIMERA_VALUE_T,
                        ADAPTER_VALUE_T);
        tumLines = Arrays.asList(FILLING_LINE, inputLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, dataLine,
                        FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);

        emptyLines = new ArrayList<>();
        missingLines = Arrays.asList(FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE,
                        FILLING_LINE, FILLING_LINE, FILLING_LINE);
    }

    @Test
    public void extractDataFromFile() throws IOException, HealthChecksException {

        final SummaryMetricsExtractor extractor = new SummaryMetricsExtractor(reader);
        new Expectations() {

            {
                reader.readLines((SamplePath) any);
                returns(refLines, tumLines);
            }
        };
        final BaseReport report = extractor.extractFromRunDirectory(TEST_DIR);
        assertReport(report);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataFromEmptyFile() throws IOException, HealthChecksException {
        final SummaryMetricsExtractor extractor = new SummaryMetricsExtractor(reader);
        new Expectations() {

            {
                reader.readLines((SamplePath) any);
                returns(emptyLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = IOException.class)
    public void extractDataFromFileIoException() throws IOException, HealthChecksException {
        final SummaryMetricsExtractor extractor = new SummaryMetricsExtractor(reader);
        new Expectations() {

            {
                reader.readLines((SamplePath) any);
                result = new IOException();
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundRefExceptionFirstFile() throws IOException, HealthChecksException {
        final SummaryMetricsExtractor extractor = new SummaryMetricsExtractor(reader);
        new Expectations() {

            {
                reader.readLines((SamplePath) any);
                returns(missingLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundRefException() throws IOException, HealthChecksException {
        final SummaryMetricsExtractor extractor = new SummaryMetricsExtractor(reader);
        new Expectations() {

            {
                reader.readLines((SamplePath) any);
                returns(refLines, missingLines);
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundTumException() throws IOException, HealthChecksException {
        final SummaryMetricsExtractor extractor = new SummaryMetricsExtractor(reader);
        new Expectations() {

            {
                reader.readLines((SamplePath) any);
                returns(refLines);
                reader.readLines((SamplePath) any);
                result = new LineNotFoundException("", "");
            }
        };
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    private void assertReport(final BaseReport report) {
        assertEquals("Report with wrong type", CheckType.SUMMARY_METRICS, report.getCheckType());
        assertNotNull(SHOULD_NOT_BE_NULL, report);
        assertField(report, SummaryMetricsCheck.MAPPING_PF_MISMATCH_RATE.toString(), MISMATCH_VALUE_R,
                        MISMATCH_VALUE_T);
        assertField(report, SummaryMetricsCheck.MAPPING_PF_INDEL_RATE.toString(), INDEL_VALUE_R, INDEL_VALUE_T);
        assertField(report, SummaryMetricsCheck.MAPPING_STRAND_BALANCE.toString(), STRAND_VALUE_R, STRAND_VALUE_T);
        assertField(report, SummaryMetricsCheck.MAPPING_PCT_CHIMERA.toString(), CHIMERA_VALUE_R, CHIMERA_VALUE_T);
        assertField(report, SummaryMetricsCheck.MAPPING_PCT_ADAPTER.toString(), ADAPTER_VALUE_R, ADAPTER_VALUE_T);
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
