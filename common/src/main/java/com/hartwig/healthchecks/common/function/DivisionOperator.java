package com.hartwig.healthchecks.common.function;

@FunctionalInterface
public interface DivisionOperator extends Calculable {

    double calculate(double left, double right);

    static DivisionOperator apply() {
        return (left, right) -> left / right;
    }

}
