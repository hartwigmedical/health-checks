package com.hartwig.healthchecks.common.util;

import java.io.Serializable;

public class BaseConfig implements Serializable {

    private CheckType checkType;

    public BaseConfig(CheckType checkType) {
        this.checkType = checkType;
    }

    public CheckType getCheckType() {
        return checkType;
    }

    @Override
    public String toString() {
        return "BaseConfig{" +
                "checkType=" + checkType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseConfig that = (BaseConfig) o;

        return checkType == that.checkType;

    }

    @Override
    public int hashCode() {
        return checkType.hashCode();
    }
}
