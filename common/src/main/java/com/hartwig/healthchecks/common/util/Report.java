package com.hartwig.healthchecks.common.util;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Report {

    private static Logger LOGGER = LogManager.getLogger(Report.class);

    private static final String REPORT_NAME = "health-checks_%s.json";

    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .disableHtmlEscaping()
            .create();

    private static Report instance = new Report();

    private Map<CheckType, BaseConfig> healthChecks = new ConcurrentHashMap<>();

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

        PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();

        String reportLocation = propertiesUtil.getProperty("report.dir");
        String fileName = String.format("%s/%s", reportLocation, String.format(REPORT_NAME, System.currentTimeMillis()));

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
