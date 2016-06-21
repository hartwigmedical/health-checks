package com.hartwig.healthchecks.common.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.common.util.PropertiesUtil;

public final class JsonReport implements Report {

    private static final String REPORT_NAME = "health-checks_%s.json";
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).disableHtmlEscaping().create();
    private static Logger LOGGER = LogManager.getLogger(Report.class);
    private static JsonReport instance = new JsonReport();

    private final Map<CheckType, BaseReport> healthChecks = new ConcurrentHashMap<>();

    private JsonReport() {
    }

    public static JsonReport getInstance() {
        return instance;
    }

    @Override
    public void addReportData(@NotNull final BaseReport reportData) {
        healthChecks.putIfAbsent(reportData.getCheckType(), reportData);
    }

    @NotNull
    @Override
    public Optional<String> generateReport() {
        final JsonArray reportArray = new JsonArray();

        healthChecks.forEach((k, v) -> {
            JsonElement configJson = GSON.toJsonTree(v);

            JsonObject element = new JsonObject();
            element.add(k.toString(), configJson);

            reportArray.add(element);
        });

        PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();

        final String reportDir = propertiesUtil.getProperty("report.dir");
        final String fileName = String.format("%s/%s", reportDir,
                String.format(REPORT_NAME, System.currentTimeMillis()));

        try (FileWriter fileWriter = new FileWriter(new File(fileName))) {
            JsonObject reportJson = new JsonObject();
            reportJson.add("health_checks", reportArray);
            fileWriter.write(GSON.toJson(reportJson));
            fileWriter.flush();
        } catch (IOException e) {
            LOGGER.error(String.format("Error occurred whilst generating reports. Error -> %s", e.getMessage()));

            return Optional.empty();
        }

        return Optional.of(fileName);
    }
}
