package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import java.util.Arrays;
import java.util.Optional;

import com.hartwig.healthchecks.boggs.healthcheck.function.Calculable;
import com.hartwig.healthchecks.boggs.healthcheck.function.DivisionOperator;
import com.hartwig.healthchecks.boggs.healthcheck.function.SingleValue;

import org.jetbrains.annotations.NotNull;

public enum FlagStatsType {

    TOTAL_INDEX(0, DivisionOperator.apply(), DivisionOperator.class, 0d),
    SECONDARY_INDEX(1, SingleValue.apply(), SingleValue.class, 1d),
    SUPPLEMENTARY_INDEX(2, SingleValue.apply(), SingleValue.class, 0d),
    DUPLICATES_INDEX(3, DivisionOperator.apply(), DivisionOperator.class, 0d),
    MAPPED_INDEX(4, DivisionOperator.apply(), DivisionOperator.class, 99.2d),
    PAIRED_IN_SEQ_INDEX(5, SingleValue.apply(), SingleValue.class, 0d),
    READ_1_INDEX(6, SingleValue.apply(), SingleValue.class, 0d),
    READ_2_INDEX(7, SingleValue.apply(), SingleValue.class, 0d),
    PROPERLY_PAIRED_INDEX(8, DivisionOperator.apply(), DivisionOperator.class, 99.0d),
    ITSELF_AND_MATE_INDEX(9, SingleValue.apply(), SingleValue.class, 0d),
    SINGELTONS_INDEX(10, SingleValue.apply(), SingleValue.class, 0.5d),
    MATE_MAP_DIF_CHR_INDEX(11, SingleValue.apply(), SingleValue.class, 0.01d),
    MATE_MAP_DIF_CHR_Q5_INDEX(12, SingleValue.apply(), SingleValue.class, 0d);

    private final int index;
    private final Calculable calculable;
    private final Class calculableClass;
    private final double threshold;

    FlagStatsType(@NotNull final int index, @NotNull final Calculable calculable, @NotNull Class calculableClass,
            @NotNull final double threshold) {
        this.index = index;
        this.calculable = calculable;
        this.calculableClass = calculableClass;
        this.threshold = threshold;
    }

    public static Optional<FlagStatsType> getByIndex(@NotNull final int index) {
        return Arrays.stream(FlagStatsType.values())
                .filter(type -> type.getIndex() == index)
                .findFirst();
    }

    @NotNull
    public int getIndex() {
        return index;
    }

    @NotNull
    public Calculable getCalculable() {
        return calculable;
    }

    public double getThreshold() {
        return threshold;
    }

    @NotNull
    public <T>T getCalculableInstance() {
        calculableClass.cast(getCalculable());
        return (T)calculableClass;
    }
}