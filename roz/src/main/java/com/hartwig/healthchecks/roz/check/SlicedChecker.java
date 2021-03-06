package com.hartwig.healthchecks.roz.check;

import java.io.IOException;
import java.nio.file.Path;

import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.checks.ErrorHandlingChecker;
import com.hartwig.healthchecks.common.checks.HealthCheck;
import com.hartwig.healthchecks.common.checks.HealthCheckConstants;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.io.path.PathExtensionFinder;
import com.hartwig.healthchecks.common.io.reader.LineReader;
import com.hartwig.healthchecks.common.predicate.VCFDataLinePredicate;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.result.BaseResult;
import com.hartwig.healthchecks.common.result.SingleValueResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("WeakerAccess")
@ResourceWrapper(type = CheckType.SLICED)
public class SlicedChecker extends ErrorHandlingChecker implements HealthChecker {

    private static final Logger LOGGER = LogManager.getLogger(SlicedChecker.class);

    private static final String SLICED_VCF_EXTENSION = "_GoNLv5_sliced.vcf";

    public SlicedChecker() {
    }

    @NotNull
    @Override
    public CheckType checkType() {
        return CheckType.SLICED;
    }

    @NotNull
    @Override
    public BaseResult tryRun(@NotNull final RunContext runContext) throws IOException, HealthChecksException {
        final Path vcfPath = PathExtensionFinder.build().findPath(runContext.runDirectory(), SLICED_VCF_EXTENSION);
        final long value = LineReader.build().readLines(vcfPath, new VCFDataLinePredicate()).stream().count();

        return toSingleValueResult(
                new HealthCheck(runContext.tumorSample(), SlicedCheck.SLICED_NUMBER_OF_VARIANTS.toString(),
                        String.valueOf(value)));
    }

    @NotNull
    @Override
    public BaseResult errorRun(@NotNull final RunContext runContext) {
        return toSingleValueResult(
                new HealthCheck(runContext.tumorSample(), SlicedCheck.SLICED_NUMBER_OF_VARIANTS.toString(),
                        HealthCheckConstants.ERROR_VALUE));
    }

    @NotNull
    private BaseResult toSingleValueResult(@NotNull final HealthCheck check) {
        check.log(LOGGER);
        return new SingleValueResult(checkType(), check);
    }
}
