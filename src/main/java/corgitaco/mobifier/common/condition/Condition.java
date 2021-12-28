package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import corgitaco.mobifier.Mobifier;
import corgitaco.mobifier.common.MobifierRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.function.Function;

public interface Condition {
    Codec<Condition> CODEC = MobifierRegistry.CONDITION.dispatchStable(Condition::codec, Function.identity());

    boolean passes(World world, LivingEntity entity, boolean isDeadOrDying);

    Codec<? extends Condition> codec();

    static void register() {
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "biome"), BiomeCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "biome_category"), BiomeCategoryCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "difficulty"), DifficultyCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "wearing"), WearingCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "has_in_hand"), HasInHandCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "attribute_comparator"), AttributeCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "dimension"), InDimensionCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "is_dead_or_dying"), IsDeadOrDyingCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "last_injurer_has"), LastInjurerHasCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "last_injurer_by_type_has"), LastInjurerByTypeHasCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "y_range"), YRangeCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "inside_structure"), InsideStructureCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "player_inventory_has"), PlayerInventoryCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "blockstates_are"), BlockStatesAreCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "blocks_are"), BlocksAreCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "precipitation_at"), PrecipitationAtCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "see_sky_at"), SeeSkyAtCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "chance"), ChanceCondition.CODEC);
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, "lunar_phase"), LunarPhaseCondition.CODEC);
    }
}
