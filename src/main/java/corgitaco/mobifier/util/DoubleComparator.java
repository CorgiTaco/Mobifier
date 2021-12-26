package corgitaco.mobifier.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public class DoubleComparator {
    public static final Codec<DoubleComparator> CODEC = Codec.STRING.comapFlatMap(s -> DataResult.success(new DoubleComparator(s)), doubleComparator -> doubleComparator.original);

    private final double checkAgainst;
    private final NumericalCheck check;
    private final String original;

    public DoubleComparator(String s) {
        this.original = s;
        if (s.startsWith("<=")) {
            s = s.replaceAll("<=", "");
            check = NumericalCheck.LESSER_THAN_OR_EQUAL;
        } else if (s.startsWith(">=")) {
            s = s.replaceAll(">=", "");
            check = NumericalCheck.GREATER_THAN_OR_EQUAL;
        } else if (s.startsWith(">")) {
            s = s.replaceAll(">", "");
            check = NumericalCheck.GREATER_THAN;
        } else if (s.startsWith("<")) {
            s = s.replaceAll("<", "");
            check = NumericalCheck.LESSER_THAN;
        } else if (s.startsWith("=") || s.startsWith("==")) {
            s = s.replaceAll("=", "");
            check = NumericalCheck.EQUAL;
        } else {
            throw new IllegalArgumentException("Illegal Comparison specified");
        }
        this.checkAgainst = Double.parseDouble(s);
    }

    public boolean check(double number) {
        return check.test(checkAgainst, number);
    }
}
