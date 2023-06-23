package corgitaco.mobifier.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import corgitaco.corgilib.entity.condition.Condition;
import corgitaco.mobifier.Mobifier;
import corgitaco.mobifier.mixin.access.RegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class MobifierRegistry {

    public static final ResourceKey<Registry<Codec<? extends Condition>>> CONDITION_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Mobifier.MOD_ID, "condition"));

    public static final Registry<Codec<? extends Condition>> CONDITION = RegistryAccess.mobifier_invokeRegisterSimple(CONDITION_KEY, Lifecycle.stable(), registry -> Condition.CODEC);

}
