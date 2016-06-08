package com.hartwig.healthchecks.common.report;

import com.google.gson.*;
import com.hartwig.healthchecks.common.util.BaseConfig;
import com.hartwig.healthchecks.common.util.CheckType;
import com.hartwig.healthchecks.common.util.PropertiesUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class JsonReport extends Report {

    private static final String REPORT_NAME = "health-checks_%s.json";

    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .disableHtmlEscaping()
            .create();

    private static JsonReport instance = new JsonReport();

    private Map<CheckType, BaseConfig> healthChecks = new ConcurrentHashMap<>();

    private JsonReport() {
    }

    public static JsonReport getInstance() {
        return instance;
    }

    @Override
    public void addReportData(BaseConfig reportData) {
        healthChecks.putIfAbsent(reportData.getCheckType(), reportData);
    }

    @Override
    public Optional<String> generateReport() {
        JsonArray reportArray = new JsonArray();

        healthChecks.forEach((k, v) -> {
            JsonElement configJson = gson.toJsonTree(v);

            JsonObject element = new JsonObject();
            element.add(k.toString(), configJson);

            reportArray.add(element);
        });

        PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();

        String reportDir = propertiesUtil.getProperty("report.dir");
        String fileName = String.format("%s/%s", reportDir, String.format(REPORT_NAME, System.currentTimeMillis()));

        try (FileWriter fileWriter = new FileWriter(new File(fileName))) {
            JsonObject reportJson = new JsonObject();
            reportJson.add("health_checks", reportArray);
            fileWriter.write(gson.toJson(reportJson));
            fileWriter.flush();
        } catch (IOException e) {
            LOGGER.error(String.format("Error occurred whilst generating reports. Error -> %s", e.getMessage()));

            return Optional.empty();
        }

        return Optional.of(fileName);
    }
}
