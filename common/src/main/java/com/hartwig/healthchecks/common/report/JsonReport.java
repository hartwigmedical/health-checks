package com.hartwig.healthchecks.common.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hartwig.healthchecks.common.checks.CheckType;
import com.hartwig.healthchecks.common.exception.GenerateReportException;
import com.hartwig.healthchecks.common.util.PropertiesUtil;

import org.jetbrains.annotations.NotNull;

public final class JsonReport extends AbstractJsonBaseReport {

    private static final String REPORT_DIR = "report.dir";

    private static final String REPORT_NAME = "health-checks_%s.json";

    private static final JsonReport INSTANCE = new JsonReport();

    private static final Map<CheckType, BaseReport> HEALTH_CHECKS = new ConcurrentHashMap<>();

    private JsonReport() {
    }

    public static JsonReport getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public Optional<String> generateReport(final String runDirectory) throws GenerateReportException {
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
        return Optional.ofNullable(fileName);
    }
}
