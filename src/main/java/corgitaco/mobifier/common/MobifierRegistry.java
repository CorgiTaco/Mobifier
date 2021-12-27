package corgitaco.mobifier.common;

import com.mojang.serialization.Codec;
import corgitaco.mobifier.Mobifier;
import corgitaco.mobifier.common.condition.Condition;
import corgitaco.mobifier.mixin.access.RegistryAccess;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class MobifierRegistry {

    public static final RegistryKey<Registry<Codec<? extends Condition>>> CONDITION_KEY = RegistryKey.createRegistryKey(new ResourceLocation(Mobifier.MOD_ID, "condition"));

    public static final Registry<Codec<? extends Condition>> CONDITION = RegistryAccess.invokeRegisterSimple(CONDITION_KEY, () -> Condition.CODEC);

}
