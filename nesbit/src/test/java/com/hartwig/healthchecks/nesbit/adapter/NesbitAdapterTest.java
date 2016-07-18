package com.hartwig.healthchecks.nesbit.adapter;

import java.util.Arrays;

import org.junit.Test;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.PatientMultiChecksReport;
import com.hartwig.healthchecks.nesbit.extractor.SomaticExtractor;
import com.hartwig.healthchecks.nesbit.extractor.GermlineExtractor;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

public class NesbitAdapterTest {

    private static final String DUMMY_CHECK = "DUMMY_CHECK";

    private static final String DUMMY_ID = "DUMMY_ID";

    private static final String DUMMY_RUN_DIR = "DummyRunDir";

    private static final String REF_VALUE = "409";

    @Test
    public void verifyAdapterRunning(@Mocked final HealthCheckerImpl variant, @Mocked final HealthCheckerImpl somatic,
                    @Mocked final JsonReport report) {

        new NonStrictExpectations() {

            {
                JsonReport.getInstance();
                result = report;
                times = 1;

                new HealthCheckerImpl(CheckType.GERMLINE, anyString, (GermlineExtractor) any);
                result = variant;
                times = 1;
                variant.runCheck();
                returns(getVariantDummyReport());
                times = 1;

                new HealthCheckerImpl(CheckType.SOMATIC, anyString, (SomaticExtractor) any);
                result = somatic;
                times = 1;
                variant.runCheck();
                returns(getSomticDummyReport());
                times = 1;

            }
        };
        final HealthCheckAdapter adapter = new NesbitAdapter();
        adapter.runCheck(DUMMY_RUN_DIR);

        new Verifications() {

            {
                report.addReportData((BaseReport) any);
                times = 2;
            }
        };
    }

    private BaseReport getVariantDummyReport() {
        final BaseDataReport testDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, REF_VALUE);
        return new PatientMultiChecksReport(CheckType.GERMLINE, Arrays.asList(testDataReport));
    }

    private BaseReport getSomticDummyReport() {
        final BaseDataReport testDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, REF_VALUE);
        return new PatientMultiChecksReport(CheckType.SOMATIC, Arrays.asList(testDataReport));
    }
}
