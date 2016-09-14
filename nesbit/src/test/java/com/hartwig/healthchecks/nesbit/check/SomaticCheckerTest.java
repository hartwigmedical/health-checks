package com.hartwig.healthchecks.nesbit.check;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.dir.TestRunContextFactory;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.MultiValueResult;
import com.hartwig.healthchecks.nesbit.model.VCFConstants;
import com.hartwig.healthchecks.nesbit.model.VCFType;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class SomaticCheckerTest {

    private static final double EPSILON = 1.0e-4;
    private static final int EXPECTED_NUM_CHECKS = 38;

    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();
    private static final String REF_SAMPLE = "sample1";
    private static final String TUMOR_SAMPLE = "sample2";

    private static final String MINIMAL_RUN_DIRECTORY = Resources.getResource("run2").getPath();
    private static final String MINIMAL_REF_SAMPLE = "sample3";
    private static final String MINIMAL_TUMOR_SAMPLE = "sample4";

    private static final String INDELS = VCFType.INDELS.toString();
    private static final String SNP = VCFType.SNP.toString();

    private static final String MUTECT = VCFConstants.MUTECT.toUpperCase();
    private static final String FREEBAYES = VCFConstants.FREEBAYES.toUpperCase();
    private static final String STRELKA = VCFConstants.STRELKA.toUpperCase();
    private static final String VARSCAN = VCFConstants.VARSCAN.toUpperCase();

    private final SomaticChecker checker = new SomaticChecker();

    @Test
    public void canAnalyseTypicalMeltedVCF() throws IOException, HealthChecksException {
        final RunContext runContext = TestRunContextFactory.forTest(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);

        final BaseResult result = checker.tryRun(runContext);
        final List<HealthCheck> checks = ((MultiValueResult) result).getChecks();

        assertEquals(CheckType.SOMATIC, result.getCheckType());
        assertEquals(EXPECTED_NUM_CHECKS, checks.size());

        assertCheck(checks, SomaticCheck.COUNT.checkName(VCFType.INDELS.toString()), 67);
        assertCheck(checks, SomaticCheck.COUNT.checkName(VCFType.SNP.toString()), 987);

        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(SNP, MUTECT), 0.9137);
        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(INDELS, MUTECT), 0.0);
        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(SNP, FREEBAYES), 0.1655);
        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(INDELS, FREEBAYES), 0.1904);
        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(SNP, VARSCAN), 0.8539);
        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(INDELS, VARSCAN), 1.0);
        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(SNP, STRELKA), 0.9694);
        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(INDELS, STRELKA), 1.0);

        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(SNP, MUTECT), 0.8914);
        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(INDELS, MUTECT), 0.0);
        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(SNP, FREEBAYES), 0.5804);
        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(INDELS, FREEBAYES), 0.3636);
        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(SNP, VARSCAN), 0.9374);
        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(INDELS, VARSCAN), 0.3620);
        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(SNP, STRELKA), 0.9195);
        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(INDELS, STRELKA), 0.9130);

        assertCheck(checks, SomaticCheck.PROPORTION_CHECK.checkName(SNP, "1"), 0.2715);
        assertCheck(checks, SomaticCheck.PROPORTION_CHECK.checkName(INDELS, "1"), 0.6865);
        assertCheck(checks, SomaticCheck.PROPORTION_CHECK.checkName(SNP, "2"), 0.1590);
        assertCheck(checks, SomaticCheck.PROPORTION_CHECK.checkName(INDELS, "2"), 0.2537);
        assertCheck(checks, SomaticCheck.PROPORTION_CHECK.checkName(SNP, "3"), 0.4812);
        assertCheck(checks, SomaticCheck.PROPORTION_CHECK.checkName(INDELS, "3"), 0.0597);
        assertCheck(checks, SomaticCheck.PROPORTION_CHECK.checkName(SNP, "4"), 0.08814);
        assertCheck(checks, SomaticCheck.PROPORTION_CHECK.checkName(INDELS, "4"), 0.0);

        assertCheck(checks, SomaticCheck.AF_LOWER_SD.checkName(MUTECT), 0.1075);
        assertCheck(checks, SomaticCheck.AF_MEDIAN.checkName(MUTECT), 0.1578);
        assertCheck(checks, SomaticCheck.AF_UPPER_SD.checkName(MUTECT), 0.2253);

        assertCheck(checks, SomaticCheck.AF_LOWER_SD.checkName(FREEBAYES), 0.2143);
        assertCheck(checks, SomaticCheck.AF_MEDIAN.checkName(FREEBAYES), 0.2571);
        assertCheck(checks, SomaticCheck.AF_UPPER_SD.checkName(FREEBAYES), 0.3333);

        assertCheck(checks, SomaticCheck.AF_LOWER_SD.checkName(VARSCAN), 0.128);
        assertCheck(checks, SomaticCheck.AF_MEDIAN.checkName(VARSCAN), 0.1651);
        assertCheck(checks, SomaticCheck.AF_UPPER_SD.checkName(VARSCAN), 0.243);

        assertCheck(checks, SomaticCheck.AF_LOWER_SD.checkName(STRELKA), 0.1136);
        assertCheck(checks, SomaticCheck.AF_MEDIAN.checkName(STRELKA), 0.1627);
        assertCheck(checks, SomaticCheck.AF_UPPER_SD.checkName(STRELKA), 0.2381);
    }

    @Test
    public void errorYieldsCorrectOutput() {
        final RunContext runContext = TestRunContextFactory.forTest(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);
        final MultiValueResult result = (MultiValueResult) checker.errorRun(runContext);
        assertEquals(EXPECTED_NUM_CHECKS, result.getChecks().size());
    }

    @Test
    public void canAnalyseMinimalVCF() throws IOException, HealthChecksException {
        final RunContext runContext = TestRunContextFactory.forTest(MINIMAL_RUN_DIRECTORY, MINIMAL_REF_SAMPLE,
                MINIMAL_TUMOR_SAMPLE);

        final BaseResult result = checker.run(runContext);
        final List<HealthCheck> checks = ((MultiValueResult) result).getChecks();
        assertEquals(38, checks.size());
    }

    private static void assertCheck(@NotNull final List<HealthCheck> checks, @NotNull final String checkName,
            final double expectedValue) {
        final Optional<HealthCheck> report = checks.stream().filter(
                data -> data.getCheckName().equals(checkName)).findFirst();

        assert report.isPresent();
        final String check = report.get().getValue();
        double checkValue = Double.valueOf(check);
        assertEquals(expectedValue, checkValue, EPSILON);
    }
}
