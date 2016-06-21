package com.hartwig.healthchecks.common.util;

import java.io.Serializable;

public class BaseReport implements Serializable {

    private final CheckType checkType;

    public BaseReport(final CheckType checkType) {
        this.checkType = checkType;
    }

    public CheckType getCheckType() {
        return checkType;
    }

    @Override
    public String toString() {
        return "BaseConfig{" + "checkType=" + checkType + '}';
    }
}
