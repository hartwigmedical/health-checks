package com.hartwig.healthchecks.boggs.extractor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.path.RunContextFactory;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.HealthCheck;
import com.hartwig.healthchecks.common.report.PatientReport;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class MappingExtractorTest {

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();

    private static final String REF_SAMPLE = "sample1";
    private static final String TUMOR_SAMPLE = "sample2";
    private static final String EMPTY_FLAGSTAT_SAMPLE = "sample3";
    private static final String EMPTY_FASTQC_SAMPLE = "sample4";
    private static final String NON_EXISTING_SAMPLE = "sample5";

    @Test
    public void correctInputYieldsCorrectOutput() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);

        MappingExtractor extractor = new MappingExtractor(runContext);
        final BaseReport report = extractor.extractFromRunDirectory("");
        assertReport(report);
    }

    @Test(expected = EmptyFileException.class)
    public void emptyFlagStatYieldsEmptyFileException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, EMPTY_FLAGSTAT_SAMPLE,
                EMPTY_FLAGSTAT_SAMPLE);

        MappingExtractor extractor = new MappingExtractor(runContext);
        extractor.extractFromRunDirectory("");
    }

    @Test(expected = EmptyFileException.class)
    public void emptyTotalSequenceFileYieldsEmptyFileException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, EMPTY_FASTQC_SAMPLE, EMPTY_FASTQC_SAMPLE);

        MappingExtractor extractor = new MappingExtractor(runContext);
        extractor.extractFromRunDirectory("");
    }

    @Test(expected = IOException.class)
    public void nonExistingFileYieldsIOException() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, NON_EXISTING_SAMPLE, NON_EXISTING_SAMPLE);

        MappingExtractor extractor = new MappingExtractor(runContext);
        extractor.extractFromRunDirectory("");
    }

    private static void assertReport(@NotNull final BaseReport report) {
        assertEquals(CheckType.MAPPING, report.getCheckType());
        assertRefSampleData(((PatientReport) report).getRefSampleChecks());
        assertTumorSampleData(((PatientReport) report).getTumorSampleChecks());
    }

    private static void assertRefSampleData(@NotNull final List<HealthCheck> mapping) {
        final HealthCheck mappedData = extractReportData(mapping, MappingCheck.MAPPING_PERCENTAGE_MAPPED);
        assertEquals("0.9693877551020408", mappedData.getValue());
        assertEquals(REF_SAMPLE, mappedData.getSampleId());

        final HealthCheck mateData = extractReportData(mapping,
                MappingCheck.MAPPING_PROPORTION_MAPPED_DIFFERENT_CHR);
        assertEquals("0.010526315789473684", mateData.getValue());

        final HealthCheck properData = extractReportData(mapping,
                MappingCheck.MAPPING_PROPERLY_PAIRED_PROPORTION_OF_MAPPED);
        assertEquals("0.9473684210526315", properData.getValue());

        final HealthCheck singletonData = extractReportData(mapping, MappingCheck.MAPPING_PROPORTION_SINGLETON);
        assertEquals("0.010526315789473684", singletonData.getValue());

        final HealthCheck duplicateData = extractReportData(mapping,
                MappingCheck.MAPPING_MARKDUP_PROPORTION_DUPLICATES);
        assertEquals("0.10204081632653061", duplicateData.getValue());

        final HealthCheck proportionRead = extractReportData(mapping,
                MappingCheck.MAPPING_PROPORTION_READ_VS_TOTAL_SEQUENCES);
        assertEquals("0.97", proportionRead.getValue());
    }

    private static void assertTumorSampleData(@NotNull final List<HealthCheck> mapping) {
        final HealthCheck mappedData = extractReportData(mapping, MappingCheck.MAPPING_PERCENTAGE_MAPPED);
        assertEquals("0.875", mappedData.getValue());
        assertEquals(TUMOR_SAMPLE, mappedData.getSampleId());

        final HealthCheck mateData = extractReportData(mapping,
                MappingCheck.MAPPING_PROPORTION_MAPPED_DIFFERENT_CHR);
        assertEquals("0.02857142857142857", mateData.getValue());

        final HealthCheck properData = extractReportData(mapping,
                MappingCheck.MAPPING_PROPERLY_PAIRED_PROPORTION_OF_MAPPED);
        assertEquals("0.7142857142857143", properData.getValue());

        final HealthCheck singletonData = extractReportData(mapping, MappingCheck.MAPPING_PROPORTION_SINGLETON);
        assertEquals("0.07142857142857142", singletonData.getValue());

        final HealthCheck duplicateData = extractReportData(mapping,
                MappingCheck.MAPPING_MARKDUP_PROPORTION_DUPLICATES);
        assertEquals("0.125", duplicateData.getValue());

        final HealthCheck proportionRead = extractReportData(mapping,
                MappingCheck.MAPPING_PROPORTION_READ_VS_TOTAL_SEQUENCES);
        assertEquals("0.7", proportionRead.getValue());
    }

    @NotNull
    private static HealthCheck extractReportData(@NotNull final List<HealthCheck> mapping,
            @NotNull final MappingCheck check) {
        Optional<HealthCheck> report = mapping.stream().filter(
                baseDataReport -> baseDataReport.getCheckName().equals(check.toString())).findFirst();

        assert report.isPresent();
        return report.get();
    }
}
