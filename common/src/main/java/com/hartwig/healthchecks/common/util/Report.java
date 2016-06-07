package com.hartwig.healthchecks.common.util;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Report {

    private static Logger LOGGER = LogManager.getLogger(Report.class);

    private static final String DEFAULT_LOCATION = "/Users/wrodrigues/health-checks_%s.json";

    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .disableHtmlEscaping()
            .create();

    private static Report instance = new Report();
    JsonParser jsonParser = new JsonParser();
    private Map<CheckType, BaseConfig> healthChecks = new HashMap<>();

    private Report() {
    }

    public static Report getInstance() {
        return instance;
    }

    public void addReportData(BaseConfig reportData) {
        healthChecks.putIfAbsent(reportData.getCheckType(), reportData);
    }

    public void generateReport() {
        JsonArray reportArray = new JsonArray();

        healthChecks.forEach((k, v) -> {
            JsonElement configJson = gson.toJsonTree(v);

            JsonObject element = new JsonObject();
            element.add(k.toString(), configJson);

            reportArray.add(element);
        });

        String fileName = String.format(DEFAULT_LOCATION, System.currentTimeMillis());

        try (FileWriter fileWriter = new FileWriter(new File(fileName))) {
            JsonObject reportJson = new JsonObject();
            reportJson.add("health_checks", reportArray);
            fileWriter.write(gson.toJson(reportJson));
            fileWriter.flush();
        } catch (IOException e) {
            LOGGER.error(String.format("Error occurred whilst generating reports. Error -> %s", e.getMessage()));
        }
    }
}
