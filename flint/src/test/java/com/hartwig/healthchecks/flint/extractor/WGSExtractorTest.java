package com.hartwig.healthchecks.flint.extractor;

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
import com.hartwig.healthchecks.common.io.reader.SamplePath;
import com.hartwig.healthchecks.common.io.reader.SampleReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.flint.report.CoverageReport;

import mockit.Expectations;
import mockit.Mocked;

public class WGSExtractorTest {

    private static final String WRONG_PATIENT_ID = "Wrong Patient ID";

    private static final String DATA_LINE = "2858674662\t%s\t0.157469\t0\t0\t0.000585\t0.059484\t0.002331\t"
                    + "0.002378\t0.020675\t0.001026\t0.086479\t0.000041\t0.000037\t0.00003\t0.00002\t0.00001\t0.000005\t"
                    + "0.000002\t0\t0\t0\t0\t0\t0";

    private static final String HEADER_LINE = "GENOME_TERRITORY\tMEAN_COVERAGE\tSD_COVERAGE\tMEDIAN_COVERAGE\t"
                    + "MAD_COVERAGE\tPCT_EXC_MAPQ\tPCT_EXC_DUPE\tPCT_EXC_UNPAIRED\tPCT_EXC_BASEQ\tPCT_EXC_OVERLAP\t"
                    + "PCT_EXC_CAPPED\tPCT_EXC_TOTAL\tPCT_5X\tPCT_10X\tPCT_15X\tPCT_20X\tPCT_25X\tPCT_30X\tPCT_40X\t"
                    + "PCT_50X\tPCT_60X\tPCT_70X\tPCT_80X\tPCT_90X\tPCT_100X";

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

    private static final String COV_MEDIAN_R = "0.000856";

    private static final String COV_MEDIAN_T = "0.000756";

    private static final String WIDTH_OF_70_PER_R = "247";

    private static final String WIDTH_OF_70_PER_T = "147";

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
        String dataLine = String.format(DATA_LINE, COV_MEDIAN_R, WIDTH_OF_70_PER_R);
        refLines = Arrays.asList(FILLING_LINE, inputLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, HEADER_LINE,
                        dataLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);

        inputLine = String.format(INPUT_LINE, PATIENT_ID_T, PATIENT_ID_T, PATIENT_ID_T, PATIENT_ID_T);
        dataLine = String.format(DATA_LINE, COV_MEDIAN_T, WIDTH_OF_70_PER_T);
        tumLines = Arrays.asList(FILLING_LINE, inputLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, HEADER_LINE,
                        dataLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);

        emptyLines = new ArrayList<>();
        missingLines = Arrays.asList(FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, HEADER_LINE,
                        dataLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);
    }

    @Test
    public void extractDataFromFile() throws IOException, HealthChecksException {
        new Expectations() {

            {
                reader.readLines((SamplePath) any);
                returns(refLines, tumLines);
            }
        };
        final WGSExtractor extractor = new WGSExtractor(reader);
        final BaseReport report = extractor.extractFromRunDirectory(TEST_DIR);
        assertReport(report);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataFromEmptyFile() throws IOException, HealthChecksException {
        new Expectations() {

            {
                reader.readLines((SamplePath) any);
                returns(emptyLines);
            }
        };
        final WGSExtractor extractor = new WGSExtractor(reader);
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = IOException.class)
    public void extractDataFromFileIoException() throws IOException, HealthChecksException {
        new Expectations() {

            {
                reader.readLines((SamplePath) any);
                result = new IOException();
            }
        };
        final WGSExtractor extractor = new WGSExtractor(reader);
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundRefExceptionFirstFile() throws IOException, HealthChecksException {
        new Expectations() {

            {
                reader.readLines((SamplePath) any);
                returns(missingLines);
            }
        };
        final WGSExtractor extractor = new WGSExtractor(reader);
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundRefException() throws IOException, HealthChecksException {
        new Expectations() {

            {
                reader.readLines((SamplePath) any);
                returns(refLines, missingLines);
            }
        };
        final WGSExtractor extractor = new WGSExtractor(reader);
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundTumException() throws IOException, HealthChecksException {
        new Expectations() {

            {
                reader.readLines((SamplePath) any);
                returns(refLines);
                reader.readLines((SamplePath) any);
                result = new LineNotFoundException("");
            }
        };
        final WGSExtractor extractor = new WGSExtractor(reader);
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    private void assertReport(final BaseReport report) {
        assertEquals("Report with wrong type", CheckType.COVERAGE, report.getCheckType());
        assertNotNull(SHOULD_NOT_BE_NULL, report);
        assertField(report, CoverageCheck.COVERAGE_MEAN.name(), COV_MEDIAN_R, COV_MEDIAN_T);

    }

    private void assertField(final BaseReport report, final String field, final String refValue,
                    final String tumValue) {
        assertBaseData(((CoverageReport) report).getReferenceSample(), PATIENT_ID_R, field, refValue);
        assertBaseData(((CoverageReport) report).getTumorSample(), PATIENT_ID_T, field, tumValue);
    }

    private void assertBaseData(final List<BaseDataReport> reports, final String patientId, final String check,
                    final String expectedValue) {
        final BaseDataReport value = reports.stream().filter(p -> p.getCheckName().equals(check)).findFirst().get();
        assertEquals(WRONG_DATA, expectedValue, value.getValue());
        assertEquals(WRONG_PATIENT_ID, patientId, value.getPatientId());

    }
}
