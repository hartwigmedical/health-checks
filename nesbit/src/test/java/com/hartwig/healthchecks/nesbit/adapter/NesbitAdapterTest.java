package com.hartwig.healthchecks.nesbit.adapter;

import java.util.Collections;

import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheckerImpl;
import com.hartwig.healthchecks.common.io.path.RunContextFactory;
import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.report.BaseReport;
import com.hartwig.healthchecks.common.report.PatientMultiChecksReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.nesbit.extractor.GermlineExtractor;
import com.hartwig.healthchecks.nesbit.extractor.SomaticExtractor;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

public class NesbitAdapterTest {

    private static final String DUMMY_CHECK = "DUMMY_CHECK";
    private static final String DUMMY_ID = "DUMMY_ID";
    private static final String DUMMY_RUN_DIR = "DummyRunDir";
    private static final String DUMMY_REPORT = "DummyReport";

    private static final String REF_VALUE = "409";

    @Test
    public void verifyAdapterRunning(@Mocked final HealthCheckerImpl variant, @Mocked final HealthCheckerImpl somatic,
            @Mocked final Report report, @Mocked HealthCheckReportFactory factory, @Mocked AbstractHealthCheckAdapter mock) {
        new NonStrictExpectations() {
            {
                AbstractHealthCheckAdapter.attachReport(DUMMY_REPORT);
                result = factory;
                times = 1;

                factory.create();
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
        final AbstractHealthCheckAdapter adapter = new NesbitAdapter();
        adapter.runCheck(RunContextFactory.backwardsCompatible(DUMMY_RUN_DIR), DUMMY_REPORT);

        new Verifications() {

            {
                report.addReportData((BaseReport) any);
                times = 2;
            }
        };
    }

    @NotNull
    private static BaseReport getVariantDummyReport() {
        final BaseDataReport testDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, REF_VALUE);
        return new PatientMultiChecksReport(CheckType.GERMLINE, Collections.singletonList(testDataReport));
    }

    @NotNull
    private static BaseReport getSomticDummyReport() {
        final BaseDataReport testDataReport = new BaseDataReport(DUMMY_ID, DUMMY_CHECK, REF_VALUE);
        return new PatientMultiChecksReport(CheckType.SOMATIC, Collections.singletonList(testDataReport));
    }
}
