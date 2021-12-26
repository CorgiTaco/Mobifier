package corgitaco.mobifier.condition;

import com.mojang.serialization.Codec;
import corgitaco.mobifier.Mobifier;
import corgitaco.mobifier.MobifierRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Function;

public interface Condition {
    Codec<Condition> CODEC = MobifierRegistry.DEATH_CONDITION.dispatchStable(Condition::codec, Function.identity());

    boolean passes(ServerWorld world, LivingEntity entity, boolean isDeath);

    Codec<? extends Condition> codec();

    static void register() {
        Registry.register(MobifierRegistry.DEATH_CONDITION, new ResourceLocation(Mobifier.MOD_ID, "biome"), BiomeCondition.CODEC);
        Registry.register(MobifierRegistry.DEATH_CONDITION, new ResourceLocation(Mobifier.MOD_ID, "biome_category"), BiomeCategoryCondition.CODEC);
        Registry.register(MobifierRegistry.DEATH_CONDITION, new ResourceLocation(Mobifier.MOD_ID, "difficulty"), DifficultyCondition.CODEC);
        Registry.register(MobifierRegistry.DEATH_CONDITION, new ResourceLocation(Mobifier.MOD_ID, "wearing"), WearingCondition.CODEC);
        Registry.register(MobifierRegistry.DEATH_CONDITION, new ResourceLocation(Mobifier.MOD_ID, "has_in_hand"), HasInHandCondition.CODEC);
        Registry.register(MobifierRegistry.DEATH_CONDITION, new ResourceLocation(Mobifier.MOD_ID, "attribute_comparator"), AttributeCondition.CODEC);
        Registry.register(MobifierRegistry.DEATH_CONDITION, new ResourceLocation(Mobifier.MOD_ID, "dimension"), InDimensionCondition.CODEC);
        Registry.register(MobifierRegistry.DEATH_CONDITION, new ResourceLocation(Mobifier.MOD_ID, "on_death"), DieCondition.CODEC);
        Registry.register(MobifierRegistry.DEATH_CONDITION, new ResourceLocation(Mobifier.MOD_ID, "last_injurer_has"), LastInjurerHasCondition.CODEC);
        Registry.register(MobifierRegistry.DEATH_CONDITION, new ResourceLocation(Mobifier.MOD_ID, "last_injurer_by_type_has"), LastInjurerByTypeHasCondition.CODEC);
        Registry.register(MobifierRegistry.DEATH_CONDITION, new ResourceLocation(Mobifier.MOD_ID, "y_range"), YRangeCondition.CODEC);
    }
}
