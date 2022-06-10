package corgitaco.mobifier.common.util;

import net.minecraft.world.entity.MobCategory;

import java.util.Arrays;

public class MobifierUtil {

    public static MobCategory tryParseMobCategory(String categoryString) {
        final MobCategory mobCategory = MobCategory.valueOf(categoryString.toUpperCase());
        if (mobCategory == null) {
            throw new IllegalArgumentException(String.format("\"%s\" is not a valid monster category. Valid monster categories: %s", categoryString, Arrays.toString(Arrays.stream(MobCategory.values()).map(MobCategory::getName).toArray())));
        } else {
            return mobCategory;
        }
    }
}
