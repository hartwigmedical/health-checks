package com.hartwig.healthchecks.nesbit.extractor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.path.RunContextFactory;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.PatientReport;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class GermlineExtractorTest {

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();
    private static final String REF_SAMPLE = "CPCT11111111R";
    private static final String TUMOR_SAMPLE = "CPCT11111111T";
    private static final String GERMLINE_INDELS = "VARIANTS_GERMLINE_INDELS";
    private static final String GERMLINE_SNP = "VARIANTS_GERMLINE_SNP";

    @Test
    public void canCountSNPAndIndels() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);

        final GermlineExtractor extractor = new GermlineExtractor(runContext);
        final BaseReport report = extractor.extractFromRunDirectory(RUN_DIRECTORY);

        assertEquals(CheckType.GERMLINE, report.getCheckType());
        final List<BaseDataReport> refData = ((PatientReport) report).getReferenceSample();
        final List<BaseDataReport> tumData = ((PatientReport) report).getTumorSample();

        assertSampleData(refData, "55", "4");
        assertSampleData(tumData, "74", "4");
    }

    private static void assertSampleData(@NotNull final List<BaseDataReport> sampleData,
            @NotNull final String expectedCountSNP, @NotNull final String expectedCountIndels) {
        assertEquals(2, sampleData.size());
        final Optional<BaseDataReport> indelReport = sampleData.stream().filter(
                data -> data.getCheckName().equals(GERMLINE_INDELS)).findFirst();
        assert indelReport.isPresent();

        assertEquals(expectedCountIndels, indelReport.get().getValue());
        final Optional<BaseDataReport> snpReport = sampleData.stream().filter(
                data -> data.getCheckName().equals(GERMLINE_SNP)).findFirst();
        assert snpReport.isPresent();

        assertEquals(expectedCountSNP, snpReport.get().getValue());
    }
}
