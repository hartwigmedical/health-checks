package com.hartwig.healthchecks;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import com.hartwig.healthchecks.common.adapter.AbstractHealthCheckAdapter;
import com.hartwig.healthchecks.common.adapter.HealthCheckReportFactory;
import com.hartwig.healthchecks.common.exception.GenerateReportException;
import com.hartwig.healthchecks.common.exception.HealthChecksException;
import com.hartwig.healthchecks.common.exception.NotFoundException;
import com.hartwig.healthchecks.common.io.dir.CPCTRunContextFactory;
import com.hartwig.healthchecks.common.io.dir.FolderChecker;
import com.hartwig.healthchecks.common.io.dir.RunContext;
import com.hartwig.healthchecks.common.report.Report;
import com.hartwig.healthchecks.util.adapter.HealthChecksFlyweight;

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

import rx.Observable;
import rx.observables.BlockingObservable;
import rx.schedulers.Schedulers;

public final class HealthChecksApplication {

    private static final String RUN_DIR_ARG_DESC = "The path containing the data for a single run";
    private static final String CHECK_TYPE_ARGS_DESC = "The type of check to be executed for a single run";
    private static final String REPORT_TYPE_ARGS_DESC = "The type of report to be generated: json or stdout.";

    private static final String REPORT_GENERATED_MSG = "Report generated -> \n%s";

    private static final Logger LOGGER = LogManager.getLogger(HealthChecksApplication.class);

    private static final String RUN_DIRECTORY = "rundir";
    private static final String CHECK_TYPE = "checktype";
    private static final String REPORT_TYPE = "reporttype";

    private static final String ALL_CHECKS = "all";

    @NotNull
    private final RunContext runContext;
    @NotNull
    private final String checkType;
    @NotNull
    private final String reportType;

    private HealthChecksApplication(@NotNull final RunContext runContext, @NotNull final String checkType,
            @NotNull final String reportType) {
        this.runContext = runContext;
        this.checkType = checkType;
        this.reportType = reportType;
    }

    /**
     * To Run Healthchecks over files in a dir
     *
     * @param args - Arguments on how to run the health-checks should contain:
     *             -rundir [run-directory] -checktype [boggs - all] -reporttype
     *             [json - stdout]
     * @throws ParseException - In case commandline's arguments could not be parsed.
     */
    public static void main(final String... args) throws ParseException {
        final Options options = createOptions();
        final CommandLine cmd = createCommandLine(options, args);

        String runDirectory = cmd.getOptionValue(RUN_DIRECTORY);
        final String checkType = cmd.getOptionValue(CHECK_TYPE);
        final String reportType = cmd.getOptionValue(REPORT_TYPE);

        if (runDirectory == null || checkType == null || reportType == null) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Health-Checks", options);
            System.exit(1);
        }

        RunContext runContext = null;
        try {
            runDirectory = FolderChecker.build().checkFolder(runDirectory);
            runContext = CPCTRunContextFactory.fromRunDirectory(runDirectory);
        } catch (IOException | HealthChecksException e) {
            LOGGER.info(e.getMessage());
            System.exit(1);
        }

        final HealthChecksApplication healthChecksApplication = new HealthChecksApplication(runContext, checkType,
                reportType);
        healthChecksApplication.processHealthChecks();
    }

    @NotNull
    private static Options createOptions() {
        final Options options = new Options();

        options.addOption(RUN_DIRECTORY, true, RUN_DIR_ARG_DESC);
        options.addOption(CHECK_TYPE, true, CHECK_TYPE_ARGS_DESC);
        options.addOption(REPORT_TYPE, true, REPORT_TYPE_ARGS_DESC);

        return options;
    }

    @NotNull
    private static CommandLine createCommandLine(@NotNull final Options options, @NotNull final String... args)
            throws ParseException {
        final CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    private void processHealthChecks() {
        if (checkType.equalsIgnoreCase(ALL_CHECKS)) {
            executeAllChecks();
        } else {
            final HealthChecksFlyweight flyweight = HealthChecksFlyweight.getInstance();
            try {
                final AbstractHealthCheckAdapter healthCheckAdapter = flyweight.getAdapter(checkType);

                healthCheckAdapter.runCheck(runContext, reportType);
            } catch (final NotFoundException e) {
                LOGGER.error(e.getMessage());
            }
            generateReport();
        }
    }

    private void executeAllChecks() {
        final HealthChecksFlyweight flyweight = HealthChecksFlyweight.getInstance();
        final Collection<AbstractHealthCheckAdapter> adapters = flyweight.getAllAdapters();

        final Observable<AbstractHealthCheckAdapter> adapterObservable = Observable.from(adapters).subscribeOn(
                Schedulers.io());

        BlockingObservable.from(adapterObservable).subscribe(adapter -> adapter.runCheck(runContext, reportType),
                (error) -> LOGGER.error(error.getMessage()), this::generateReport);
    }

    private void generateReport() {
        try {
            final HealthCheckReportFactory reportFactory = AbstractHealthCheckAdapter.attachReport(reportType);
            final Report report = reportFactory.create();

            final Optional<String> reportData = report.generateReport(runContext.runDirectory());
            if (reportData.isPresent()) {
                LOGGER.info(String.format(REPORT_GENERATED_MSG, reportData.get()));
            }
        } catch (final GenerateReportException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
    }
}
