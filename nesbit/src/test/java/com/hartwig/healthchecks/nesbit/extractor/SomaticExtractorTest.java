package com.hartwig.healthchecks.nesbit.extractor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Resources;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.extractor.DataExtractor;
import com.hartwig.healthchecks.common.io.path.RunContext;
import com.hartwig.healthchecks.common.io.path.RunContextFactory;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.MultiValueResult;
import com.hartwig.healthchecks.nesbit.model.VCFType;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class SomaticExtractorTest {

    private static final double EPSILON = 1.0e-4;
    private static final String RUN_DIRECTORY = Resources.getResource("run").getPath();
    private static final String REF_SAMPLE = "CPCT11111111R";
    private static final String TUMOR_SAMPLE = "CPCT11111111T";

    private static final String INDELS = VCFType.INDELS.toString();
    private static final String SNP = VCFType.SNP.toString();
    private static final String MUTECT = SomaticExtractor.MUTECT.toUpperCase();
    private static final String FREEBAYES = SomaticExtractor.FREEBAYES.toUpperCase();
    private static final String STRELKA = SomaticExtractor.STRELKA.toUpperCase();
    private static final String VARSCAN = SomaticExtractor.VARSCAN.toUpperCase();

    @Test
    public void canAnalyseTypicalMeltedVCF() throws IOException, HealthChecksException {
        RunContext runContext = RunContextFactory.testContext(RUN_DIRECTORY, REF_SAMPLE, TUMOR_SAMPLE);
        final DataExtractor extractor = new SomaticExtractor(runContext);

        final BaseResult result = extractor.extract();
        final List<HealthCheck> checks = ((MultiValueResult) result).getChecks();

        assertEquals(CheckType.SOMATIC, result.getCheckType());
        assertEquals(26, checks.size());

        assertCheck(checks, SomaticCheck.SOMATIC_COUNT.checkName(VCFType.INDELS.toString()), 68);
        assertCheck(checks, SomaticCheck.SOMATIC_COUNT.checkName(VCFType.SNP.toString()), 986);

        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(SNP, MUTECT), 0.7702);
        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(INDELS, MUTECT), 0.0);
        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(SNP, FREEBAYES), 0.1518);
        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(INDELS, FREEBAYES), 0.1818);
        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(SNP, VARSCAN), 0.7646);
        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(INDELS, VARSCAN), 0.1818);
        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(SNP, STRELKA), 0.7827);
        assertCheck(checks, SomaticCheck.SENSITIVITY_CHECK.checkName(INDELS, STRELKA), 0.1818);

        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(SNP, FREEBAYES), 0.5317);
        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(INDELS, FREEBAYES), 0.3636);
        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(SNP, MUTECT), 0.7503);
        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(INDELS, MUTECT), 0.0);
        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(SNP, VARSCAN), 0.8394);
        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(INDELS, VARSCAN), 0.0677);
        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(SNP, STRELKA), 0.7424);
        assertCheck(checks, SomaticCheck.PRECISION_CHECK.checkName(INDELS, STRELKA), 0.1666);

        assertCheck(checks, SomaticCheck.PROPORTION_CHECK.checkName(SNP, "1"), 0.2718);
        assertCheck(checks, SomaticCheck.PROPORTION_CHECK.checkName(INDELS, "1"), 0.6764);
        assertCheck(checks, SomaticCheck.PROPORTION_CHECK.checkName(SNP, "2"), 0.1582);
        assertCheck(checks, SomaticCheck.PROPORTION_CHECK.checkName(INDELS, "2"), 0.2647);
        assertCheck(checks, SomaticCheck.PROPORTION_CHECK.checkName(SNP, "3"), 0.4817);
        assertCheck(checks, SomaticCheck.PROPORTION_CHECK.checkName(INDELS, "3"), 0.0588);
        assertCheck(checks, SomaticCheck.PROPORTION_CHECK.checkName(SNP, "4"), 0.0);
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
