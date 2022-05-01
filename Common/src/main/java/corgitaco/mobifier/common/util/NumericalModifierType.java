package corgitaco.mobifier.common.util;

import java.util.function.BiFunction;

@SuppressWarnings("UnnecessaryUnboxing")
public enum NumericalModifierType {
    MULTIPLY(((number, number2) -> number.doubleValue() * number2.doubleValue())),
    DIVIDE(((number, number2) -> number.doubleValue() / number2.doubleValue())),
    SUBTRACT(((number, number2) -> number.doubleValue() - number2.doubleValue())),
    ADD(((number, number2) -> number.doubleValue() + number2.doubleValue()));

    private final BiFunction<Double, Double, Double> numberFunction;

    NumericalModifierType(BiFunction<Double, Double, Double> numberFunction) {
        this.numberFunction = numberFunction;
    }

    public double apply(Double first, Double two) {
        return numberFunction.apply(first, two);
    }
}
