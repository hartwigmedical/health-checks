package com.hartwig.healthchecks.boggs.extractor;

import java.util.Arrays;
import java.util.Optional;

import com.hartwig.healthchecks.common.function.Calculable;
import com.hartwig.healthchecks.common.function.DivisionOperator;
import com.hartwig.healthchecks.common.function.SingleValue;

import org.jetbrains.annotations.NotNull;

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
    SINGLETONS_INDEX(10, DivisionOperator.apply(), DivisionOperator.class),
    MATE_MAP_DIF_CHR_INDEX(11, DivisionOperator.apply(), DivisionOperator.class),
    MATE_MAP_DIF_CHR_Q5_INDEX(12, SingleValue.apply(), SingleValue.class);

    private final int index;
    @NotNull
    private final Calculable calculable;
    @NotNull
    private final Class calculableClass;

    FlagStatsType(final int index, @NotNull final Calculable calculable, @NotNull final Class calculableClass) {
        this.index = index;
        this.calculable = calculable;
        this.calculableClass = calculableClass;
    }

    @NotNull
    public static Optional<FlagStatsType> getByIndex(final int index) {
        return Arrays.stream(FlagStatsType.values()).filter(type -> type.getIndex() == index).findFirst();
    }

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
