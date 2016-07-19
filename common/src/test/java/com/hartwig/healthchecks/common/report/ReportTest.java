package com.hartwig.healthchecks.common.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.GenerateReportException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.path.PathRegexFinder;
import com.hartwig.healthchecks.common.io.reader.LineReader;
import com.hartwig.healthchecks.common.report.metadata.MetadataExtractor;
import com.hartwig.healthchecks.common.report.metadata.ReportMetadata;
import com.hartwig.healthchecks.common.util.PropertiesUtil;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

public class ReportTest {

    private static final String ZERO = "0";

    private static final String SOME_VERSION = "v1.7";

    private static final String SOME_DATE = "Sat Jul 9 15:41:42 CEST 2016";

    private static final String TMP_DIR = "/tmp";

    private static final String ONE = "1";

    private static final String RUN_DIR = "runDir";

    private static final String REPORT_DIR = "report.dir";

    private static final String PARSE_LOGS = "parse.logs";

    @Test
    public void generateReport(@Mocked final MetadataExtractor metadataExtractor,
                    @Mocked final PropertiesUtil propertiesUtil, @Mocked final FileWriter fileWriter)
                    throws IOException, HealthChecksException {
        new NonStrictExpectations() {

            {

                PropertiesUtil.getInstance();
                result = propertiesUtil;
                times = 1;

                new MetadataExtractor((PathRegexFinder) any, (LineReader) any);
                result = metadataExtractor;
                times = 1;

                propertiesUtil.getProperty(PARSE_LOGS);
                returns(ONE);

                propertiesUtil.getProperty(REPORT_DIR);
                returns(TMP_DIR);

                metadataExtractor.extractMetadata(RUN_DIR);
                returns(new ReportMetadata(SOME_DATE, SOME_VERSION));

                new FileWriter(new File(anyString));
                result = fileWriter;
                times = 1;
            }
        };

        final Report report = JsonReport.getInstance();

        final BaseReport baseConfig1 = new BaseReport(CheckType.MAPPING);
        report.addReportData(baseConfig1);

        final BaseReport baseConfig2 = new BaseReport(CheckType.PRESTATS);
        report.addReportData(baseConfig2);

        try {
            final Optional<String> location = report.generateReport(RUN_DIR);
            Assert.assertNotNull(location);
            Assert.assertTrue(location.isPresent());
        } catch (final GenerateReportException e) {
            Assert.fail("Failed to generate report.");
        }
    }

    @Test
    public void generateReportNoMeta(@Mocked final MetadataExtractor metadataExtractor,
                    @Mocked final PropertiesUtil propertiesUtil, @Mocked final FileWriter fileWriter)
                    throws IOException, HealthChecksException {
        new NonStrictExpectations() {

            {

                PropertiesUtil.getInstance();
                result = propertiesUtil;
                times = 1;

                propertiesUtil.getProperty(PARSE_LOGS);
                returns(ZERO);

                propertiesUtil.getProperty(REPORT_DIR);
                returns(TMP_DIR);

                new FileWriter(new File(anyString));
                result = fileWriter;
                times = 1;
            }
        };

        final Report report = JsonReport.getInstance();

        final BaseReport baseConfig1 = new BaseReport(CheckType.MAPPING);
        report.addReportData(baseConfig1);

        final BaseReport baseConfig2 = new BaseReport(CheckType.PRESTATS);
        report.addReportData(baseConfig2);

        try {
            final Optional<String> location = report.generateReport(RUN_DIR);
            Assert.assertNotNull(location);
            Assert.assertTrue(location.isPresent());
        } catch (final GenerateReportException e) {
            Assert.fail("Failed to generate report.");
        }

        new Verifications() {

            {
                metadataExtractor.extractMetadata(RUN_DIR);
                times = 0;
            }
        };
    }

    @Test(expected = GenerateReportException.class)
    public void generateReportException(@Mocked final MetadataExtractor metadataExtractor,
                    @Mocked final PropertiesUtil propertiesUtil, @Mocked final FileWriter fileWriter)
                    throws IOException, HealthChecksException {
        new NonStrictExpectations() {

            {

                PropertiesUtil.getInstance();
                result = propertiesUtil;
                times = 1;

                new MetadataExtractor((PathRegexFinder) any, (LineReader) any);
                result = metadataExtractor;
                times = 1;

                propertiesUtil.getProperty(PARSE_LOGS);
                returns(ONE);

                propertiesUtil.getProperty(REPORT_DIR);
                returns(TMP_DIR);

                metadataExtractor.extractMetadata(RUN_DIR);
                returns(new ReportMetadata(SOME_DATE, SOME_VERSION));

                new FileWriter(new File(anyString));
                result = fileWriter;
                times = 1;

                fileWriter.write(anyString);
                result = new IOException("");

            }
        };
        final Report report = JsonReport.getInstance();

        final BaseReport baseConfig1 = new BaseReport(CheckType.MAPPING);
        report.addReportData(baseConfig1);

        final Optional<String> location = report.generateReport(RUN_DIR);

        Assert.assertNotNull(location);
        Assert.assertFalse(location.isPresent());
    }

}
