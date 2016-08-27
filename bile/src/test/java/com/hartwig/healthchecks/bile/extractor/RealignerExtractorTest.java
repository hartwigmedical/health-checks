package com.hartwig.healthchecks.bile.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.data.BaseResult;
import com.hartwig.healthchecks.common.data.PatientResult;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.LineNotFoundException;
import com.hartwig.healthchecks.common.exception.MalformedFileException;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.path.RunContextFactory;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class RealignerExtractorTest {

    private static final String REF_CHANGED_READS_PROPORTION = "0.04000";
    private static final String TUMOR_CHANGED_READS_PROPORTION = "0.04444";

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();

    private static final String REF_SAMPLE = "sample1";
    private static final String TUMOR_SAMPLE = "sample2";

    private static final String EMPTY_SAMPLE = "sample3";
    private static final String INCORRECT_SAMPLE = "sample4";
    private static final String NON_EXISTING_SAMPLE = "sample5";
    private static final String MALFORMED_SAMPLE = "sample6";

    @Test
    public void correctInputYieldsCorrectOutput() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);

        RealignerExtractor extractor = new RealignerExtractor(runContext);
        final BaseResult report = extractor.extract();
        assertReport(report);
    }

    @Test(expected = EmptyFileException.class)
    public void emptyFileYieldsEmptyFileException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, EMPTY_SAMPLE, EMPTY_SAMPLE);

        RealignerExtractor extractor = new RealignerExtractor(runContext);
        extractor.extract();
    }

    @Test(expected = IOException.class)
    public void nonExistingFileYieldsIOException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, NON_EXISTING_SAMPLE, NON_EXISTING_SAMPLE);

        RealignerExtractor extractor = new RealignerExtractor(runContext);
        extractor.extract();
    }

    @Test(expected = LineNotFoundException.class)
    public void incorrectRefFileYieldsLineNotFoundException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, INCORRECT_SAMPLE, TUMOR_SAMPLE);

        RealignerExtractor extractor = new RealignerExtractor(runContext);
        extractor.extract();
    }

    @Test(expected = LineNotFoundException.class)
    public void incorrectTumorFileYieldsLineNotFoundException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, INCORRECT_SAMPLE);

        RealignerExtractor extractor = new RealignerExtractor(runContext);
        extractor.extract();
    }

    @Test(expected = LineNotFoundException.class)
    public void incorrectFilesYieldsLineNotFoundException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, INCORRECT_SAMPLE, INCORRECT_SAMPLE);

        RealignerExtractor extractor = new RealignerExtractor(runContext);
        extractor.extract();
    }

    @Test(expected = MalformedFileException.class)
    public void malformedLineYieldsMalformedFileException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, MALFORMED_SAMPLE, MALFORMED_SAMPLE);

        RealignerExtractor extractor = new RealignerExtractor(runContext);
        extractor.extract();
    }

    private static void assertReport(@NotNull final BaseResult report) {
        assertEquals(CheckType.REALIGNER, report.getCheckType());
        assertNotNull(report);
        assertField(report, RealignerExtractor.REALIGNER_CHECK_NAME, REF_CHANGED_READS_PROPORTION,
                TUMOR_CHANGED_READS_PROPORTION);
    }

    private static void assertField(@NotNull final BaseResult report, @NotNull final String field,
            @NotNull final String refValue, @NotNull final String tumValue) {
        assertBaseData(((PatientResult) report).getRefSampleChecks(), REF_SAMPLE, field, refValue);
        assertBaseData(((PatientResult) report).getTumorSampleChecks(), TUMOR_SAMPLE, field, tumValue);
    }

    private static void assertBaseData(@NotNull final List<HealthCheck> reports, @NotNull final String sampleId,
            @NotNull final String check, @NotNull final String expectedValue) {
        final Optional<HealthCheck> value = reports.stream().filter(
                p -> p.getCheckName().equals(check)).findFirst();
        assert value.isPresent();

        assertEquals(expectedValue, value.get().getValue());
        assertEquals(sampleId, value.get().getSampleId());
    }
}
