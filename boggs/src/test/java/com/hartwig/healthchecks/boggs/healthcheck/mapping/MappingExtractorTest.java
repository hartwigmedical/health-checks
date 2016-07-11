package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatData;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStatParser;
import com.hartwig.healthchecks.boggs.flagstatreader.FlagStats;
import com.hartwig.healthchecks.boggs.healthcheck.reader.TestZipFileFactory;
import com.hartwig.healthchecks.boggs.reader.ZipFileReader;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.SampleReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import mockit.Expectations;
import mockit.Mocked;

public class MappingExtractorTest {

    private static final String FASTQC_DATA_TXT = "fastqc_data.txt";

    private static final String RUNDIR = "rundir";

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    private static final String TEST_REF_ID = "CPCT12345678R";

    private static final String TEST_TUM_ID = "CPCT12345678T";

    private List<String> fastqLines;

    private List<String> emptyList;

    @Mocked
    private ZipFileReader zipFileReader;

    @Before
    public void setUp() {
        fastqLines = TestZipFileFactory.getFastqLines();
        emptyList = new ArrayList<>();
    }

    @Mocked
    private FlagStatParser flagstatParser;

    @Test
    public void extractData() throws IOException, HealthChecksException {
        new Expectations() {

            {
                zipFileReader.getZipFilesPath(anyString, anyString, anyString);
                returns(new File(TEST_REF_ID).toPath());
                zipFileReader.readFieldFromZipFiles((Path) any, FASTQC_DATA_TXT, anyString);
                returns(fastqLines);

                flagstatParser.parse(anyString, anyString);
                returns(dummyData());

                zipFileReader.getZipFilesPath(anyString, anyString, anyString);
                returns(new File(TEST_TUM_ID).toPath());
                zipFileReader.readFieldFromZipFiles((Path) any, FASTQC_DATA_TXT, anyString);
                returns(fastqLines);

                flagstatParser.parse(anyString, anyString);
                returns(dummyData());
            }
        };

        final MappingExtractor extractor = new MappingExtractor(flagstatParser, zipFileReader);

        final BaseReport mappingReport = extractor.extractFromRunDirectory(RUNDIR);
        assertNotNull("We should have data", mappingReport);
        assertEquals("Report with wrong type", CheckType.MAPPING, mappingReport.getCheckType());

        final List<BaseDataReport> referenceSample = ((SampleReport) mappingReport).getReferenceSample();
        assetMappingData(referenceSample);

        final List<BaseDataReport> tumorSample = ((SampleReport) mappingReport).getTumorSample();
        assetMappingData(tumorSample);

    }

    @Test(expected = NoSuchFileException.class)
    public void extractDataNoneExistingDir() throws IOException, HealthChecksException {
        new Expectations() {

            {
                zipFileReader.getZipFilesPath(DUMMY_RUN_DIR, anyString, anyString);
                result = new NoSuchFileException(anyString);

            }
        };
        final MappingExtractor extractor = new MappingExtractor(flagstatParser, zipFileReader);
        extractor.extractFromRunDirectory(DUMMY_RUN_DIR);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataEmptyFastQFile() throws IOException, HealthChecksException {
        new Expectations() {

            {
                zipFileReader.getZipFilesPath(anyString, anyString, anyString);
                returns(new File(TEST_REF_ID).toPath());
                zipFileReader.readFieldFromZipFiles((Path) any, FASTQC_DATA_TXT, anyString);
                returns(emptyList);
            }
        };
        final MappingExtractor extractor = new MappingExtractor(flagstatParser, zipFileReader);
        extractor.extractFromRunDirectory(RUNDIR);
    }

    @Test(expected = EmptyFileException.class)
    public void extractDataEmptyFlagStatsFile() throws IOException, HealthChecksException {

        new Expectations() {

            {
                zipFileReader.getZipFilesPath(anyString, anyString, anyString);
                returns(new File(TEST_REF_ID).toPath());
                zipFileReader.readFieldFromZipFiles((Path) any, FASTQC_DATA_TXT, anyString);
                returns(fastqLines);

                flagstatParser.parse(anyString, anyString);
                returns(null);
            }
        };
        final MappingExtractor extractor = new MappingExtractor(flagstatParser, zipFileReader);
        extractor.extractFromRunDirectory(RUNDIR);
    }

    @Test(expected = NoSuchFileException.class)
    public void extractDataNoFastQFile() throws IOException, HealthChecksException {
        new Expectations() {

            {
                zipFileReader.getZipFilesPath(anyString, anyString, anyString);
                result = new NoSuchFileException("");
            }
        };
        final MappingExtractor extractor = new MappingExtractor(flagstatParser, zipFileReader);
        extractor.extractFromRunDirectory(RUNDIR);
    }

    @Test(expected = NoSuchFileException.class)
    public void extractDataNoFlagStatsFile() throws IOException, HealthChecksException {

        new Expectations() {

            {
                zipFileReader.getZipFilesPath(anyString, anyString, anyString);
                returns(new File(TEST_REF_ID).toPath());
                zipFileReader.readFieldFromZipFiles((Path) any, FASTQC_DATA_TXT, anyString);
                returns(fastqLines);

                flagstatParser.parse(anyString, anyString);
                result = new NoSuchFileException("");
            }
        };
        final MappingExtractor extractor = new MappingExtractor(flagstatParser, zipFileReader);
        extractor.extractFromRunDirectory(RUNDIR);
    }

    private BaseDataReport extractReportData(@NotNull final List<BaseDataReport> mapping,
                    @NotNull final MappingCheck check) {

        return mapping.stream().filter(baseDataReport -> {
            return baseDataReport.getCheckName().equals(check.getDescription());
        }).findFirst().get();
    }

    private void assetMappingData(final List<BaseDataReport> mapping) {
        final BaseDataReport mappedData = extractReportData(mapping, MappingCheck.MAPPING_MAPPED);
        assertEquals("99.69", mappedData.getValue());

        final BaseDataReport mateData = extractReportData(mapping, MappingCheck.MAPPING_MATE_MAPPED_DIFFERENT_CHR);
        assertEquals("0.0", mateData.getValue());

        final BaseDataReport properData = extractReportData(mapping, MappingCheck.MAPPING_PROPERLY_PAIRED);
        assertEquals("99.57", properData.getValue());

        final BaseDataReport singletonData = extractReportData(mapping, MappingCheck.MAPPING_SINGLETON);
        assertEquals("55.0", singletonData.getValue());

        final BaseDataReport duplicateData = extractReportData(mapping, MappingCheck.MAPPING_DUPLIVATES);
        assertEquals("5.95", duplicateData.getValue());

        final BaseDataReport isAllRead = extractReportData(mapping, MappingCheck.MAPPING_IS_ALL_READ);
        assertEquals("false", isAllRead.getValue());
    }

    private FlagStatData dummyData() throws IOException, EmptyFileException {

        final FlagStats total = new FlagStats(FlagStatsType.TOTAL_INDEX, "dummy", 17940d);
        final FlagStats secondary = new FlagStats(FlagStatsType.SECONDARY_INDEX, "dummy", 20d);
        final FlagStats supplementary = new FlagStats(FlagStatsType.SUPPLEMENTARY_INDEX, "dummy", 0d);
        final FlagStats duplicate = new FlagStats(FlagStatsType.DUPLICATES_INDEX, "dummy", 1068d);
        final FlagStats mapped = new FlagStats(FlagStatsType.MAPPED_INDEX, "dummy", 17885d);
        final FlagStats paired = new FlagStats(FlagStatsType.PAIRED_IN_SEQ_INDEX, "dummy", 17920d);
        final FlagStats read1 = new FlagStats(FlagStatsType.READ_1_INDEX, "dummy", 8960d);
        final FlagStats read2 = new FlagStats(FlagStatsType.READ_2_INDEX, "dummy", 8960d);
        final FlagStats proper = new FlagStats(FlagStatsType.PROPERLY_PAIRED_INDEX, "dummy", 17808d);
        final FlagStats itself = new FlagStats(FlagStatsType.ITSELF_AND_MATE_INDEX, "dummy", 17808d);
        final FlagStats singleton = new FlagStats(FlagStatsType.SINGELTONS_INDEX, "dummy", 55d);
        final FlagStats mateMapped = new FlagStats(FlagStatsType.MATE_MAP_DIF_CHR_INDEX, "dummy", 0d);
        final FlagStats q5Index = new FlagStats(FlagStatsType.MATE_MAP_DIF_CHR_Q5_INDEX, "dummy", 0d);

        final List<FlagStats> passedStats = Arrays.asList(total, secondary, supplementary, duplicate, mapped, paired,
                        read1, read2, proper, itself, singleton, mateMapped, q5Index);
        final List<FlagStats> failedStats = Arrays.asList(total, secondary, supplementary, duplicate, mapped, paired,
                        read1, read2, proper, itself, singleton, mateMapped, q5Index);

        final FlagStatData flagStatData = new FlagStatData(passedStats, failedStats);
        return flagStatData;
    }
}
