package com.hartwig.healthchecks.boggs.adapter;

import com.hartwig.healthchecks.boggs.flagstatreader.SambambaFlagStatParser;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingExtractor;
import com.hartwig.healthchecks.boggs.healthcheck.mapping.MappingHealthChecker;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestatsExtractor;
import com.hartwig.healthchecks.boggs.healthcheck.prestast.PrestatsHealthChecker;
import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.checks.HealthChecker;
import com.hartwig.healthchecks.common.exception.EmptyFileException;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.common.resource.ResourceWrapper;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@ResourceWrapper(type = CheckCategory.BOGGS)
public class BoggsAdapter implements HealthCheckAdapter {
    private static final Logger LOGGER = LogManager.getLogger(BoggsAdapter.class);

    private static final String IO_ERROR_MSG = "Got IO Exception with message: %s";
    private static final String EMPTY_FILE_ERROR_MSG = "Got Empty File Exception with message: %s";
    private final Report report = JsonReport.getInstance();

    public void runCheck(@NotNull final String runDirectory) {
        try {
            final MappingExtractor mappingExtractor = new MappingExtractor(new SambambaFlagStatParser());
            final HealthChecker mappingHealthChecker = new MappingHealthChecker(runDirectory, mappingExtractor);
            final BaseReport mapping = mappingHealthChecker.runCheck();
            report.addReportData(mapping);

            final PrestatsExtractor prestatsExtractor = new PrestatsExtractor();
            final HealthChecker prestastHealthChecker = new PrestatsHealthChecker(runDirectory, prestatsExtractor);
            final BaseReport prestatsErrors = prestastHealthChecker.runCheck();
            report.addReportData(prestatsErrors);
        } catch (EmptyFileException e) {
            LOGGER.error(String.format(EMPTY_FILE_ERROR_MSG, e.getMessage()));
        } catch (IOException e) {
            LOGGER.error(String.format(IO_ERROR_MSG, e.getMessage()));
        }
    }
}
