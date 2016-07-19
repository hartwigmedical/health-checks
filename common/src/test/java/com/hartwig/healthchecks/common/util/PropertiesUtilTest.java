package com.hartwig.healthchecks.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertiesUtilTest {

    @Test
    public void getProperty() throws Exception {
        final PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();
        final String dir = propertiesUtil.getProperty("report.dir");
        final String parseLogs = propertiesUtil.getProperty("parse.logs");

        assertEquals("Directory is not the one expected", "/tmp", dir);
        assertEquals("Wrong value", "1", parseLogs);
    }

}
