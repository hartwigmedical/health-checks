package com.hartwig.healthchecks.common.function;

@FunctionalInterface
public interface SingleValue extends Calculable {

    double calculate(double singleValue);

    static SingleValue apply() {
        return (singleValue) -> singleValue;
    }
}
