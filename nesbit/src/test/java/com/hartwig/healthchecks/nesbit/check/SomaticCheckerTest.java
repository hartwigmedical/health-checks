package com.hartwig.healthchecks.nesbit.check;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.dir.CPCTRunContextFactory;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.dir.TestRunContextFactory;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.MultiValueResult;
import com.hartwig.healthchecks.nesbit.model.VCFType;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class SomaticCheckerTest {

    private static final double EPSILON = 1.0e-4;
    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();
    private static final String REF_SAMPLE = "CPCT11111111R";
    private static final String TUMOR_SAMPLE = "CPCT11111111T";

    private static final String INDELS = VCFType.INDELS.toString();
    private static final String SNP = VCFType.SNP.toString();
    private static final String MUTECT = SomaticChecker.MUTECT.toUpperCase();
    private static final String FREEBAYES = SomaticChecker.FREEBAYES.toUpperCase();
    private static final String STRELKA = SomaticChecker.STRELKA.toUpperCase();
    private static final String VARSCAN = SomaticChecker.VARSCAN.toUpperCase();

    @Test
    public void canAnalyseTypicalMeltedVCF() throws IOException, HealthChecksException {
        RunContext runContext = TestRunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);
        final HealthChecker checker = new SomaticChecker();

        final BaseResult result = checker.run(runContext);
        final List<HealthCheck> checks = ((MultiValueResult) result).getChecks();

        assertEquals(CheckType.SOMATIC, result.getCheckType());
        assertEquals(26, checks.size());

        assertCheck(checks, SomaticCheck.SOMATIC_COUNT.checkName(VCFType.INDELS.toString()), 67);
        assertCheck(checks, SomaticCheck.SOMATIC_COUNT.checkName(VCFType.SNP.toString()), 987);

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
