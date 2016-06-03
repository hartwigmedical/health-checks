package com.hartwig.healthchecks;

import com.hartwig.healthchecks.common.adapter.HealthCheckAdapter;
import com.hartwig.healthchecks.common.exception.NotFoundException;
import com.hartwig.healthchecks.util.adapter.HealthChecksFlyweight;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HealthChecksApplication {

    private static final String RUN_DIRECTORY = "rundir";
    private static final String CHECK_TYPE = "checktype";
    private static Logger LOGGER = LoggerFactory.getLogger(HealthChecksApplication.class);

    public static void main(String[] args) throws ParseException, IOException {
        LOGGER.info("Testing");
        Options options = createOptions();
        CommandLine cmd = createCommandLine(args, options);

        String runDirectory = cmd.getOptionValue(RUN_DIRECTORY);
        String checkType = cmd.getOptionValue(CHECK_TYPE);

        if (runDirectory == null || checkType == null) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Health-Checks", options);
        } else {
            HealthChecksFlyweight flyweight = HealthChecksFlyweight.getInstance();
            try {
                HealthCheckAdapter healthCheckAdapter = flyweight.getAdapter(checkType);
                healthCheckAdapter.runCheck(runDirectory);
            } catch (NotFoundException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    @NotNull
    private static CommandLine createCommandLine(@NotNull String[] args, @NotNull Options options)
            throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    @NotNull
    private static Options createOptions() {
        Options options = new Options();
        options.addOption(RUN_DIRECTORY, true, "The path containing the data for a single run");
        options.addOption(CHECK_TYPE, true, "The type of check to b executed for a single run");
        return options;
    }
}