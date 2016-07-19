package com.hartwig.healthchecks.common.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class PropertiesUtil {

    private static final String CONFIG_FILE = "config.properties";

    private static final String ERROR_LOADING_PROPERTIES = "Error loading properties. Error -> %s";

    private static final Logger LOGGER = LogManager.getLogger(PropertiesUtil.class);

    private static PropertiesUtil instance = new PropertiesUtil();

    private static Properties properties = new Properties();

    static {
        try {
            properties.load(ClassLoader.getSystemResourceAsStream(CONFIG_FILE));
        } catch (IOException e) {
            LOGGER.error(String.format(ERROR_LOADING_PROPERTIES, e.getMessage()));
        }
    }

    private PropertiesUtil() {
    }

    public static PropertiesUtil getInstance() {
        return instance;
    }

    @NotNull
    public String getProperty(@NotNull final String key) {
        return properties.getProperty(key);
    }
}
