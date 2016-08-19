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
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.SampleReport;

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
        assertRefSampleData(((SampleReport) report).getReferenceSample());
        assertTumorSampleData(((SampleReport) report).getTumorSample());
    }

    private static void assertRefSampleData(@NotNull final List<BaseDataReport> mapping) {
        final BaseDataReport mappedData = extractReportData(mapping, MappingCheck.MAPPING_PERCENTAGE_MAPPED);
        assertEquals("96.94", mappedData.getValue());
        assertEquals(REF_SAMPLE, mappedData.getSampleId());

        final BaseDataReport mateData = extractReportData(mapping,
                MappingCheck.MAPPING_PROPORTION_MAPPED_DIFFERENT_CHR);
        assertEquals("1.05", mateData.getValue());

        final BaseDataReport properData = extractReportData(mapping,
                MappingCheck.MAPPING_PROPERLY_PAIRED_PROPORTION_OF_MAPPED);
        assertEquals("94.74", properData.getValue());

        final BaseDataReport singletonData = extractReportData(mapping, MappingCheck.MAPPING_PROPORTION_SINGLETON);
        assertEquals("1.05", singletonData.getValue());

        final BaseDataReport duplicateData = extractReportData(mapping,
                MappingCheck.MAPPING_MARKDUP_PROPORTION_DUPLICATES);
        assertEquals("10.2", duplicateData.getValue());

        final BaseDataReport proportionRead = extractReportData(mapping,
                MappingCheck.MAPPING_PROPORTION_READ_VS_TOTAL_SEQUENCES);
        assertEquals("99.0", proportionRead.getValue());
    }

    private static void assertTumorSampleData(@NotNull final List<BaseDataReport> mapping) {
        final BaseDataReport mappedData = extractReportData(mapping, MappingCheck.MAPPING_PERCENTAGE_MAPPED);
        assertEquals("87.5", mappedData.getValue());
        assertEquals(TUMOR_SAMPLE, mappedData.getSampleId());

        final BaseDataReport mateData = extractReportData(mapping,
                MappingCheck.MAPPING_PROPORTION_MAPPED_DIFFERENT_CHR);
        assertEquals("2.86", mateData.getValue());

        final BaseDataReport properData = extractReportData(mapping,
                MappingCheck.MAPPING_PROPERLY_PAIRED_PROPORTION_OF_MAPPED);
        assertEquals("71.43", properData.getValue());

        final BaseDataReport singletonData = extractReportData(mapping, MappingCheck.MAPPING_PROPORTION_SINGLETON);
        assertEquals("7.14", singletonData.getValue());

        final BaseDataReport duplicateData = extractReportData(mapping,
                MappingCheck.MAPPING_MARKDUP_PROPORTION_DUPLICATES);
        assertEquals("12.5", duplicateData.getValue());

        final BaseDataReport proportionRead = extractReportData(mapping,
                MappingCheck.MAPPING_PROPORTION_READ_VS_TOTAL_SEQUENCES);
        assertEquals("90.0", proportionRead.getValue());
    }

    @NotNull
    private static BaseDataReport extractReportData(@NotNull final List<BaseDataReport> mapping,
            @NotNull final MappingCheck check) {
        Optional<BaseDataReport> report = mapping.stream().filter(
                baseDataReport -> baseDataReport.getCheckName().equals(check.toString())).findFirst();

        assert report.isPresent();
        return report.get();
    }
}
