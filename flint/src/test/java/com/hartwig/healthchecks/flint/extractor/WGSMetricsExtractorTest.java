package com.hartwig.healthchecks.flint.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
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

public class WGSMetricsExtractorTest {

    private static final String WRONG_SAMPLE_ID = "Wrong Sample ID";

    private static final String DATA_LINE = "2858674662\t%s\t%s\t%s\t0\t%s\t%s\t%s\t"
            + "%s\t%s\t0.001026\t%s\t0.000041\t0.000037\t0.00003\t0.00002\t0.00001\t0.000005\t"
            + "0.000002\t0\t0\t0\t0\t0\t0";

    private static final String HEADER_LINE = "GENOME_TERRITORY\tMEAN_COVERAGE\tSD_COVERAGE\tMEDIAN_COVERAGE\t"
            + "MAD_COVERAGE\tPCT_EXC_MAPQ\tPCT_EXC_DUPE\tPCT_EXC_UNPAIRED\tPCT_EXC_BASEQ\tPCT_EXC_OVERLAP\t"
            + "PCT_EXC_CAPPED\tPCT_EXC_TOTAL\tPCT_5X\tPCT_10X\tPCT_15X\tPCT_20X\tPCT_25X\tPCT_30X\tPCT_40X\t"
            + "PCT_50X\tPCT_60X\tPCT_70X\tPCT_80X\tPCT_90X\tPCT_100X";

    private static final String START_LINE = "# picard.analysis.CollectMultipleMetrics "
            + "INPUT=/sample/output/cancerPanel/%s/mapping/%s_dedup.bam "
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
    private static final String SAMPLE_ID_R = "CPCT12345678R";
    private static final String SAMPLE_ID_T = "CPCT12345678T";
    private static final String COV_MEAN_R = "0.000856";
    private static final String COV_MEAN_T = "0.000756";
    private static final String COV_SD_R = "0.257469";
    private static final String COV_SD_T = "0.157469";
    private static final String COV_MED_R = "0";
    private static final String COV_MED_T = "1";
    private static final String COV_MAP_Q_R = "0.000585";
    private static final String COV_MAP_Q_T = "0.000385";
    private static final String COV_DUPE_R = "0.059484";
    private static final String COV_DUPE_T = "0.069484";
    private static final String COV_UNPAIR_R = "0.002331";
    private static final String COV_UNPAIR_T = "0.003331";
    private static final String COV_BASE_Q_R = "0.002378";
    private static final String COV_BASE_Q_T = "0.003378";
    private static final String COV_OVERLAP_R = "0.020675";
    private static final String COV_OVERLAP_T = "0.030675";
    private static final String COV_TOTAL_R = "0.086479";
    private static final String COV_TOTAL_T = "0.096479";
    private static final String SHOULD_NOT_BE_NULL = "Should not be Null";

    private List<String> refLines;
    private List<String> tumLines;
    private List<String> missingLines;
    private List<String> emptyLines;

    @Mocked
    private SampleFinderAndReader reader;

    @Before
    public void setUp() {
        String startLine = String.format(START_LINE, SAMPLE_ID_R, SAMPLE_ID_R, SAMPLE_ID_R, SAMPLE_ID_R);
        final String dataLine = String.format(DATA_LINE, COV_MEAN_R, COV_SD_R, COV_MED_R, COV_MAP_Q_R, COV_DUPE_R,
                COV_UNPAIR_R, COV_BASE_Q_R, COV_OVERLAP_R, COV_TOTAL_R);
        refLines = Arrays.asList(FILLING_LINE, startLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, HEADER_LINE,
                dataLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);

        startLine = String.format(START_LINE, SAMPLE_ID_T, SAMPLE_ID_T, SAMPLE_ID_T, SAMPLE_ID_T);
        final String tDataLine = String.format(DATA_LINE, COV_MEAN_T, COV_SD_T, COV_MED_T, COV_MAP_Q_T, COV_DUPE_T,
                COV_UNPAIR_T, COV_BASE_Q_T, COV_OVERLAP_T, COV_TOTAL_T);
        tumLines = Arrays.asList(FILLING_LINE, startLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, HEADER_LINE,
                tDataLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);

        missingLines = Arrays.asList(FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE, HEADER_LINE,
                tDataLine, FILLING_LINE, FILLING_LINE, FILLING_LINE, FILLING_LINE);
        emptyLines = new ArrayList<>();
    }

    @Test
    public void extractDataFromFile() throws IOException, HealthChecksException {
        new Expectations() {
            {
                reader.readLines((SamplePathData) any);
                returns(refLines, tumLines);
            }
        };
        final WGSMetricsExtractor extractor = new WGSMetricsExtractor(reader);
        final BaseReport report = extractor.extractFromRunDirectory(TEST_DIR);
        assertReport(report);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataFromEmptyFile() throws IOException, HealthChecksException {
        new Expectations() {
            {
                reader.readLines((SamplePathData) any);
                result = new EmptyFileException("", "");
            }
        };
        final WGSMetricsExtractor extractor = new WGSMetricsExtractor(reader);
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = IOException.class)
    public void extractDataFromFileIoException() throws IOException, HealthChecksException {
        new Expectations() {
            {
                reader.readLines((SamplePathData) any);
                result = new IOException();
            }
        };
        final WGSMetricsExtractor extractor = new WGSMetricsExtractor(reader);
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundRefExceptionFirstFile() throws IOException, HealthChecksException {
        new Expectations() {
            {
                reader.readLines((SamplePathData) any);
                returns(missingLines);
            }
        };
        final WGSMetricsExtractor extractor = new WGSMetricsExtractor(reader);
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundRefException() throws IOException, HealthChecksException {
        new Expectations() {
            {
                reader.readLines((SamplePathData) any);
                returns(refLines, missingLines);
            }
        };
        final WGSMetricsExtractor extractor = new WGSMetricsExtractor(reader);
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = LineNotFoundException.class)
    public void extractDataLineNotFoundTumException() throws IOException, HealthChecksException {
        new Expectations() {
            {
                reader.readLines((SamplePathData) any);
                returns(refLines);
                reader.readLines((SamplePathData) any);
                result = new LineNotFoundException("", "");
            }
        };
        final WGSMetricsExtractor extractor = new WGSMetricsExtractor(reader);
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataEmptyRef() throws IOException, HealthChecksException {
        new Expectations() {
            {
                reader.readLines((SamplePathData) any);
                returns(emptyLines);
            }
        };
        final WGSMetricsExtractor extractor = new WGSMetricsExtractor(reader);
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataEmptyTum() throws IOException, HealthChecksException {
        new Expectations() {
            {
                reader.readLines((SamplePathData) any);
                returns(refLines);
                reader.readLines((SamplePathData) any);
                returns(emptyLines);
            }
        };
        final WGSMetricsExtractor extractor = new WGSMetricsExtractor(reader);
        extractor.extractFromRunDirectory(TEST_DIR);
    }

    private static void assertReport(@NotNull final BaseReport report) {
        assertEquals("Report with wrong type", CheckType.COVERAGE, report.getCheckType());
        assertNotNull(SHOULD_NOT_BE_NULL, report);
        assertField(report, CoverageCheck.COVERAGE_MEAN.name(), COV_MEAN_R, COV_MEAN_T);
        assertField(report, CoverageCheck.COVERAGE_PCT_EXC_BASEQ.name(), COV_BASE_Q_R, COV_BASE_Q_T);
        assertField(report, CoverageCheck.COVERAGE_PCT_EXC_DUPE.name(), COV_DUPE_R, COV_DUPE_T);
        assertField(report, CoverageCheck.COVERAGE_PCT_EXC_MAPQ.name(), COV_MAP_Q_R, COV_MAP_Q_T);
        assertField(report, CoverageCheck.COVERAGE_MEDIAN.name(), COV_MED_R, COV_MED_T);
        assertField(report, CoverageCheck.COVERAGE_PCT_EXC_OVERLAP.name(), COV_OVERLAP_R, COV_OVERLAP_T);
        assertField(report, CoverageCheck.COVERAGE_SD.name(), COV_SD_R, COV_SD_T);
        assertField(report, CoverageCheck.COVERAGE_PCT_EXC_UNPAIRED.name(), COV_UNPAIR_R, COV_UNPAIR_T);
        assertField(report, CoverageCheck.COVERAGE_PCT_EXC_TOTAL.name(), COV_TOTAL_R, COV_TOTAL_T);
    }

    private static void assertField(@NotNull final BaseReport report, @NotNull final String field,
            @NotNull final String refValue, @NotNull final String tumValue) {
        assertBaseData(((SampleReport) report).getReferenceSample(), SAMPLE_ID_R, field, refValue);
        assertBaseData(((SampleReport) report).getTumorSample(), SAMPLE_ID_T, field, tumValue);
    }

    private static void assertBaseData(@NotNull final List<BaseDataReport> reports, @NotNull final String sampleId,
            @NotNull final String check, @NotNull final String expectedValue) {
        final Optional<BaseDataReport> value = reports.stream().filter(
                p -> p.getCheckName().equals(check)).findFirst();
        assert value.isPresent();

        assertEquals(WRONG_DATA, expectedValue, value.get().getValue());
        assertEquals(WRONG_SAMPLE_ID, sampleId, value.get().getSampleId());
    }
}
