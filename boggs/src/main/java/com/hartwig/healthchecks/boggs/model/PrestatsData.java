package com.hartwig.healthchecks.boggs.model;

import com.hartwig.healthchecks.common.util.BaseConfig;
import com.hartwig.healthchecks.common.util.CheckType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrestatsData extends BaseConfig {

    @NotNull
    private Map<String, List<String>> summary;

    public PrestatsData(CheckType checkType) {
        super(checkType);
        this.summary = new HashMap<>();
    }

    public void addData(String filename, String check) {
        List<String> checks = summary.getOrDefault(filename, new ArrayList<>());
        checks.add(check);

        summary.put(filename, checks);
    }

    @NotNull
    public Map<String, List<String>> getSummary() {
        return summary;
    }

    @Override
    public String toString() {
        return "PrestatsData{" +
                "summary=" + summary +
                '}';
    }
}
