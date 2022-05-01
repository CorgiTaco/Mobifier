package corgitaco.mobifier.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public class DoubleModifier {
    public static final Codec<DoubleModifier> CODEC = Codec.STRING.comapFlatMap(s -> DataResult.success(new DoubleModifier(s)), doubleComparator -> doubleComparator.original);

    private final double numericalModifier;
    private final NumericalModifierType modifierType;
    private final boolean flip;
    private final String original;

    public DoubleModifier(String s) {
        this.original = s;
        if (s.startsWith("*") || s.endsWith("*")) {
            s = s.replaceAll("\\*", "");
            modifierType = NumericalModifierType.MULTIPLY;
            this.flip = false;
        } else if (s.startsWith("/")) {
            s = s.replaceAll("/", "");
            modifierType = NumericalModifierType.DIVIDE;
            this.flip = false;
        } else if (s.endsWith("/")) {
            s = s.replaceAll("/", "");
            modifierType = NumericalModifierType.DIVIDE;
            this.flip = true;
        } else if (s.startsWith("-")) {
            s = s.replaceAll("-", "");
            modifierType = NumericalModifierType.SUBTRACT;
            this.flip = false;
        } else if (s.endsWith("-")) {
            s = s.replaceAll("-", "");
            modifierType = NumericalModifierType.SUBTRACT;
            this.flip = true;
        } else if (s.startsWith("+") || s.endsWith("+")) {
            s = s.replaceAll("\\+", "");
            modifierType = NumericalModifierType.ADD;
            this.flip = false;
        } else {
            throw new IllegalArgumentException("Illegal Modifier specified");
        }
        this.numericalModifier = Double.parseDouble(s);
    }

    public double apply(double number) {
        return this.flip ? modifierType.apply(number, numericalModifier) : modifierType.apply(numericalModifier, number);
    }
}
