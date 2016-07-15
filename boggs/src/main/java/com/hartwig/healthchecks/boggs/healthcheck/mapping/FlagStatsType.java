package com.hartwig.healthchecks.boggs.healthcheck.mapping;

import java.util.Arrays;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.function.Calculable;
import com.hartwig.healthchecks.common.function.DivisionOperator;
import com.hartwig.healthchecks.common.function.SingleValue;

public enum FlagStatsType {

    TOTAL_INDEX(0, DivisionOperator.apply(), DivisionOperator.class),
    SECONDARY_INDEX(1, SingleValue.apply(), SingleValue.class),
    SUPPLEMENTARY_INDEX(2, SingleValue.apply(), SingleValue.class),
    DUPLICATES_INDEX(3, DivisionOperator.apply(), DivisionOperator.class),
    MAPPED_INDEX(4, DivisionOperator.apply(), DivisionOperator.class),
    PAIRED_IN_SEQ_INDEX(5, SingleValue.apply(), SingleValue.class),
    READ_1_INDEX(6, SingleValue.apply(), SingleValue.class),
    READ_2_INDEX(7, SingleValue.apply(), SingleValue.class),
    PROPERLY_PAIRED_INDEX(8, DivisionOperator.apply(), DivisionOperator.class),
    ITSELF_AND_MATE_INDEX(9, SingleValue.apply(), SingleValue.class),
    SINGELTONS_INDEX(10, SingleValue.apply(), SingleValue.class),
    MATE_MAP_DIF_CHR_INDEX(11, SingleValue.apply(), SingleValue.class),
    MATE_MAP_DIF_CHR_Q5_INDEX(12, SingleValue.apply(), SingleValue.class);

    private final int index;

    private final Calculable calculable;

    private final Class calculableClass;

    FlagStatsType(@NotNull final int index, @NotNull final Calculable calculable,
                    @NotNull final Class calculableClass) {
        this.index = index;
        this.calculable = calculable;
        this.calculableClass = calculableClass;
    }

    public static Optional<FlagStatsType> getByIndex(@NotNull final int index) {
        return Arrays.stream(FlagStatsType.values()).filter(type -> type.getIndex() == index).findFirst();
    }

    @NotNull
    public int getIndex() {
        return index;
    }

    @NotNull
    public Calculable getCalculable() {
        return calculable;
    }

    @NotNull
    public <T> T getCalculableInstance() {
        final Object calculable = calculableClass.cast(getCalculable());
        return (T) calculable;
    }
}
