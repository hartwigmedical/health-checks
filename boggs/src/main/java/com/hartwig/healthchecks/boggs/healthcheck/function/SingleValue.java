package com.hartwig.healthchecks.boggs.healthcheck.function;

@FunctionalInterface
public interface SingleValue extends Calculable {

    double calculate(double singelValue);

    static SingleValue apply() {
        return (singelValue) -> singelValue;
    }
}