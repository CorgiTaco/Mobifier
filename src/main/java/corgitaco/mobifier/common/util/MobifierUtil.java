package corgitaco.mobifier.common.util;

import net.minecraft.entity.EntityClassification;

import java.util.Arrays;

public class MobifierUtil {

    public static EntityClassification tryParseMobCategory(String categoryString) {
        final EntityClassification mobCategory = EntityClassification.byName(categoryString.toLowerCase());
        if (mobCategory == null) {
            throw new IllegalArgumentException(String.format("\"%s\" is not a valid monster category. Valid monster categories: %s", categoryString, Arrays.toString(Arrays.stream(EntityClassification.values()).map(EntityClassification::getName).toArray())));
        } else {
            return mobCategory;
        }
    }
}
