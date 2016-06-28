package com.hartwig.healthchecks;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.exception.GenerateReportException;
import com.hartwig.healthchecks.common.exception.NotFoundException;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.util.adapter.HealthChecksFlyweight;

import rx.Observable;
import rx.observables.BlockingObservable;
import rx.schedulers.Schedulers;

public class HealthChecksApplication {

    private static final String REPORT_GENERATED_MSG = "Report generated with following name -> %s";

    private static final Logger LOGGER = LogManager.getLogger(HealthChecksApplication.class);

    private static final String RUN_DIRECTORY = "rundir";

    private static final String CHECK_TYPE = "checktype";

    private static final String ALL_CHECKS = "all";

    private final String runDirectory;

    private final String checkType;

    public HealthChecksApplication(@NotNull final String runDirectory, @NotNull final String checkType) {
        this.runDirectory = runDirectory;
        this.checkType = checkType;
    }

    public static void main(final String... args) throws ParseException, IOException {
        final Options options = createOptions();
        final CommandLine cmd = createCommandLine(options, args);

        final String runDirectory = cmd.getOptionValue(RUN_DIRECTORY);
        final String checkType = cmd.getOptionValue(CHECK_TYPE);

        if (runDirectory == null || checkType == null) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Health-Checks", options);
        }

        final HealthChecksApplication healthChecksApplication = new HealthChecksApplication(runDirectory, checkType);
        healthChecksApplication.processHealthChecks();
    }

    @NotNull
    private static Options createOptions() {
        final Options options = new Options();
        options.addOption(RUN_DIRECTORY, true, "The path containing the data for a single run");
        options.addOption(CHECK_TYPE, true, "The type of check to b executed for a single run");
        return options;
    }

    @NotNull
    private static CommandLine createCommandLine(@NotNull final Options options, @NotNull final String... args)
                    throws ParseException {
        final CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    public void processHealthChecks() {
        LOGGER.info(String.format("Check yype sent via command line -> %s", ALL_CHECKS));

        if (checkType.equalsIgnoreCase(ALL_CHECKS)) {
            executeAllChecks(runDirectory);
        } else {
            final HealthChecksFlyweight flyweight = HealthChecksFlyweight.getInstance();
            try {
                final HealthCheckAdapter healthCheckAdapter = flyweight.getAdapter(checkType);
                healthCheckAdapter.runCheck(runDirectory);
            } catch (final NotFoundException e) {
                LOGGER.error(e.getMessage());
            }
            generateReport();
        }
    }

    protected void executeAllChecks(@NotNull final String runDirectory) {
        final HealthChecksFlyweight flyweight = HealthChecksFlyweight.getInstance();
        final Collection<HealthCheckAdapter> adapters = flyweight.getAllAdapters();

        final Observable<HealthCheckAdapter> adapterObservable =  Observable.from(adapters)
                .subscribeOn(Schedulers.io());

        BlockingObservable.from(adapterObservable)
                .subscribe(
                        adapter -> adapter.runCheck(runDirectory),
                        (error) -> LOGGER.error(error.getMessage()),
                        () -> generateReport()
                );
    }

    private void generateReport() {
        try {
            final Optional<String> fileName = JsonReport.getInstance().generateReport();
            LOGGER.info(String.format(REPORT_GENERATED_MSG, fileName.get()));
        } catch (GenerateReportException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
    }
}
