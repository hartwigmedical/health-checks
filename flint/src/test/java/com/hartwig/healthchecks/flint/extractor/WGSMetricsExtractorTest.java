package com.hartwig.healthchecks.flint.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.path.RunContextFactory;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class WGSMetricsExtractorTest {

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();

    private static final String REF_SAMPLE = "sample1";
    private static final String REF_COVERAGE_MEAN = "0.000856";
    private static final String REF_COVERAGE_SD = "0.257469";
    private static final String REF_COVERAGE_MEDIAN = "0";
    private static final String REF_PCT_EXC_MAPQ = "0.000585";
    private static final String REF_PCT_EXC_DUPE = "0.059484";
    private static final String REF_PCT_EXC_UNPAIRED = "0.002331";
    private static final String REF_PCT_EXC_BASEQ = "0.002378";
    private static final String REF_PCT_EXC_OVERLAP = "0.020675";
    private static final String REF_PCT_EXC_TOTAL = "0.086479";

    private static final String TUMOR_SAMPLE = "sample2";
    private static final String TUMOR_COVERAGE_MEAN = "0.000756";
    private static final String TUMOR_COVERAGE_SD = "0.157469";
    private static final String TUMOR_COVERAGE_MEDIAN = "1";
    private static final String TUMOR_PCT_EXC_MAPQ = "0.000385";
    private static final String TUMOR_PCT_EXC_DUPE = "0.069484";
    private static final String TUMOR_PCT_EXC_UNPAIRED = "0.003331";
    private static final String TUMOR_PCT_EXC_BASEQ = "0.003378";
    private static final String TUMOR_PCT_EXC_OVERLAP = "0.030675";
    private static final String TUMOR_PCT_EXC_TOTAL = "0.096479";

    private static final String EMPTY_SAMPLE = "sample3";
    private static final String INCORRECT_SAMPLE = "sample4";
    private static final String NON_EXISTING_SAMPLE = "sample5";

    @Test
    public void correctInputYieldsCorrectOutput() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);

        WGSMetricsExtractor extractor = new WGSMetricsExtractor(runContext);
        final BaseReport report = extractor.extractFromRunDirectory("");
        assertReport(report);
    }

    @Test(expected = EmptyFileException.class)
    public void emptyFileYieldsEmptyFileException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, EMPTY_SAMPLE, EMPTY_SAMPLE);

        WGSMetricsExtractor extractor = new WGSMetricsExtractor(runContext);
        extractor.extractFromRunDirectory("");
    }

    @Test(expected = IOException.class)
    public void nonExistingFileYieldsIOException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, NON_EXISTING_SAMPLE, NON_EXISTING_SAMPLE);

        WGSMetricsExtractor extractor = new WGSMetricsExtractor(runContext);
        extractor.extractFromRunDirectory("");
    }

    @Test(expected = LineNotFoundException.class)
    public void incorrectRefFileYieldsLineNotFoundException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, INCORRECT_SAMPLE, TUMOR_SAMPLE);

        WGSMetricsExtractor extractor = new WGSMetricsExtractor(runContext);
        extractor.extractFromRunDirectory("");
    }

    @Test(expected = LineNotFoundException.class)
    public void incorrectTumorFileYieldsLineNotFoundException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, INCORRECT_SAMPLE);

        WGSMetricsExtractor extractor = new WGSMetricsExtractor(runContext);
        extractor.extractFromRunDirectory("");
    }

    @Test(expected = LineNotFoundException.class)
    public void incorrectFilesYieldsLineNotFoundException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, INCORRECT_SAMPLE, INCORRECT_SAMPLE);

        WGSMetricsExtractor extractor = new WGSMetricsExtractor(runContext);
        extractor.extractFromRunDirectory("");
    }

    private static void assertReport(@NotNull final BaseReport report) {
        assertEquals(CheckType.COVERAGE, report.getCheckType());
        assertNotNull(report);
        assertField(report, WGSMetricsCheck.COVERAGE_MEAN.name(), REF_COVERAGE_MEAN, TUMOR_COVERAGE_MEAN);
        assertField(report, WGSMetricsCheck.COVERAGE_PCT_EXC_BASEQ.name(), REF_PCT_EXC_BASEQ, TUMOR_PCT_EXC_BASEQ);
        assertField(report, WGSMetricsCheck.COVERAGE_PCT_EXC_DUPE.name(), REF_PCT_EXC_DUPE, TUMOR_PCT_EXC_DUPE);
        assertField(report, WGSMetricsCheck.COVERAGE_PCT_EXC_MAPQ.name(), REF_PCT_EXC_MAPQ, TUMOR_PCT_EXC_MAPQ);
        assertField(report, WGSMetricsCheck.COVERAGE_MEDIAN.name(), REF_COVERAGE_MEDIAN, TUMOR_COVERAGE_MEDIAN);
        assertField(report, WGSMetricsCheck.COVERAGE_PCT_EXC_OVERLAP.name(), REF_PCT_EXC_OVERLAP,
                TUMOR_PCT_EXC_OVERLAP);
        assertField(report, WGSMetricsCheck.COVERAGE_SD.name(), REF_COVERAGE_SD, TUMOR_COVERAGE_SD);
        assertField(report, WGSMetricsCheck.COVERAGE_PCT_EXC_UNPAIRED.name(), REF_PCT_EXC_UNPAIRED,
                TUMOR_PCT_EXC_UNPAIRED);
        assertField(report, WGSMetricsCheck.COVERAGE_PCT_EXC_TOTAL.name(), REF_PCT_EXC_TOTAL, TUMOR_PCT_EXC_TOTAL);
    }

    private static void assertField(@NotNull final BaseReport report, @NotNull final String field,
            @NotNull final String refValue, @NotNull final String tumValue) {
        assertBaseData(((SampleReport) report).getReferenceSample(), REF_SAMPLE, field, refValue);
        assertBaseData(((SampleReport) report).getTumorSample(), TUMOR_SAMPLE, field, tumValue);
    }

    private static void assertBaseData(@NotNull final List<BaseDataReport> reports, @NotNull final String sampleId,
            @NotNull final String check, @NotNull final String expectedValue) {
        final Optional<BaseDataReport> value = reports.stream().filter(
                p -> p.getCheckName().equals(check)).findFirst();
        assert value.isPresent();

        assertEquals(expectedValue, value.get().getValue());
        assertEquals(sampleId, value.get().getSampleId());
    }
}
