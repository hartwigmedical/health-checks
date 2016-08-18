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
import com.hartwig.healthchecks.common.io.reader.FileReader;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import mockit.Mocked;

public class InsertSizeMetricsExtractorTest {

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();

    private static final String REF_SAMPLE = "sample1";
    private static final String REF_MEDIAN_INSERT_SIZE = "409";
    private static final String REF_WIDTH_OF_70_PERCENT = "195";

    private static final String TUMOR_SAMPLE = "sample2";
    private static final String TUMOR_MEDIAN_INSERT_SIZE = "410";
    private static final String TUMOR_WIDTH_OF_70_PERCENT = "196";

    private static final String EMPTY_SAMPLE = "sample3";
    private static final String INCORRECT_SAMPLE = "sample4";
    private static final String NON_EXISTING_SAMPLE = "sample5";

    @Mocked
    private FileReader reader;

    @Test
    public void correctInputYieldsCorrectOutput() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);

        InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(runContext);
        final BaseReport report = extractor.extractFromRunDirectory("");
        assertReport(report);
    }

    @Test(expected = EmptyFileException.class)
    public void emptyFileYieldsEmptyFileException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, EMPTY_SAMPLE, EMPTY_SAMPLE);

        InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(runContext);
        extractor.extractFromRunDirectory("");
    }

    @Test(expected = IOException.class)
    public void nonExistingFileYieldsIOException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, NON_EXISTING_SAMPLE, NON_EXISTING_SAMPLE);

        InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(runContext);
        extractor.extractFromRunDirectory("");
    }

    @Test(expected = LineNotFoundException.class)
    public void incorrectRefFileYieldsLineNotFoundException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, INCORRECT_SAMPLE, TUMOR_SAMPLE);

        InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(runContext);
        extractor.extractFromRunDirectory("");
    }

    @Test(expected = LineNotFoundException.class)
    public void incorrectTumorFileYieldsLineNotFoundException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, INCORRECT_SAMPLE);

        InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(runContext);
        extractor.extractFromRunDirectory("");
    }

    @Test(expected = LineNotFoundException.class)
    public void incorrectFilesYieldsLineNotFoundException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, INCORRECT_SAMPLE, INCORRECT_SAMPLE);

        InsertSizeMetricsExtractor extractor = new InsertSizeMetricsExtractor(runContext);
        extractor.extractFromRunDirectory("");
    }

    private static void assertReport(@NotNull final BaseReport report) {
        assertEquals("Report with wrong type", CheckType.INSERT_SIZE, report.getCheckType());
        assertNotNull(report);
        assertField(report, InsertSizeMetricsCheck.MAPPING_MEDIAN_INSERT_SIZE.toString(), REF_MEDIAN_INSERT_SIZE,
                TUMOR_MEDIAN_INSERT_SIZE);
        assertField(report, InsertSizeMetricsCheck.MAPPING_WIDTH_OF_70_PERCENT.toString(), REF_WIDTH_OF_70_PERCENT,
                TUMOR_WIDTH_OF_70_PERCENT);
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
