package com.hartwig.healthchecks;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.exception.NotFoundException;
import com.hartwig.healthchecks.common.report.JsonReport;
import com.hartwig.healthchecks.util.adapter.HealthChecksFlyweight;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public class HealthChecksApplication {
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

    public static void main(String[] args) throws ParseException, IOException {
        final Options options = createOptions();
        final CommandLine cmd = createCommandLine(args, options);

        final String runDirectory = cmd.getOptionValue(RUN_DIRECTORY);
        final String checkType = cmd.getOptionValue(CHECK_TYPE);

        if (runDirectory == null || checkType == null) {
            HelpFormatter formatter = new HelpFormatter();
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
    private static CommandLine createCommandLine(@NotNull String[] args, @NotNull Options options)
            throws ParseException {
        final CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    public void processHealthChecks() {
        if (checkType.equals(ALL_CHECKS)) {
            executeAllcheck(runDirectory);
        } else {
            HealthChecksFlyweight flyweight = HealthChecksFlyweight.getInstance();
            try {
                HealthCheckAdapter healthCheckAdapter = flyweight.getAdapter(checkType);
                healthCheckAdapter.runCheck(runDirectory);
            } catch (NotFoundException e) {
                LOGGER.error(e.getMessage());
            }
        }
        Optional<String> fileName = JsonReport.getInstance().generateReport();
        LOGGER.info(String.format("Report generated with following name -> %s", fileName.get()));
    }

    protected void executeAllcheck(@NotNull final String runDirectory) {
        final HealthChecksFlyweight flyweight = HealthChecksFlyweight.getInstance();
        final Collection<HealthCheckAdapter> adapters = flyweight.getAllAdapters();

        Observable.from(adapters)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        (h) -> h.runCheck(runDirectory),
                        (t) -> t.printStackTrace(),
                        () -> {
                            Optional<String> fileName = JsonReport.getInstance().generateReport();
                            LOGGER.info(String.format("Report generated with following name -> %s", fileName.get()));
                        }
                );

    }
}