package corgitaco.mobifier.common.util;

import net.minecraft.entity.EntityClassification;

import java.util.Arrays;

public class MobifierUtil {

    public static EntityClassification tryParseMonsterCategory(String categoryString) {
        try {
            return EntityClassification.byName(categoryString);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("\"%s\" is not a valid monster category. Valid monster categories: %s\n %s", categoryString, Arrays.toString(Arrays.stream(EntityClassification.values()).map(EntityClassification::getName).toArray()), e));
        }
    }
}
