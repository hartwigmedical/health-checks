package com.hartwig.healthchecks.common.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hartwig.healthchecks.common.exception.GenerateReportException;
import com.hartwig.healthchecks.common.util.PropertiesUtil;

public final class JsonReport extends AbstractJsonBaseReport {

    private static final JsonReport INSTANCE = new JsonReport();

    private static final Logger LOGGER = LogManager.getLogger(JsonReport.class);
    private static final String REPORT_DIR = "report.dir";
    private static final String REPORT_NAME = "health-checks_%s.json";

    private static final String ERROR_GENERATING_REPORT = "Error occurred whilst generating reports. Error -> %s";


    private JsonReport() {
    }

    public static JsonReport getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public Optional<String> generateReport(@NotNull final String runDirectory) throws GenerateReportException {
        final PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();
        final JsonArray reportArray = computeElements(runDirectory);

        final String reportDir = propertiesUtil.getProperty(REPORT_DIR);
        final String fileName = String.format("%s/%s", reportDir,
                        String.format(REPORT_NAME, System.currentTimeMillis()));

        try (FileWriter fileWriter = new FileWriter(new File(fileName))) {
            final JsonObject reportJson = new JsonObject();
            reportJson.add("health_checks", reportArray);
            fileWriter.write(GSON.toJson(reportJson));
            fileWriter.flush();
        } catch (final IOException e) {
            LOGGER.error(String.format(ERROR_GENERATING_REPORT, e.getMessage()));
            throw (GenerateReportException) new GenerateReportException(e.getMessage()).initCause(e);
        }
        return Optional.of(fileName);
    }
}
