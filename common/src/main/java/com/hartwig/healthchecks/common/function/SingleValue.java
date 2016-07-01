package com.hartwig.healthchecks.common.function;

@FunctionalInterface
public interface SingleValue extends Calculable {

    double calculate(double singelValue);

    static SingleValue apply() {
        return (singelValue) -> singelValue;
    }
}
