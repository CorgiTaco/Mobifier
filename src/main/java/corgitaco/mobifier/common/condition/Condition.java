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
        register("biome", BiomeCondition.CODEC);
        register("biome_category", BiomeCategoryCondition.CODEC);
        register("difficulty", DifficultyCondition.CODEC);
        register("wearing", WearingCondition.CODEC);
        register("has_in_hand", HasInHandCondition.CODEC);
        register("attribute_comparator", AttributeCondition.CODEC);
        register("dimension", InDimensionCondition.CODEC);
        register("is_dead_or_dying", IsDeadOrDyingCondition.CODEC);
        register("last_injurer_has", LastInjurerHasCondition.CODEC);
        register("last_injurer_by_type_has", LastInjurerByTypeHasCondition.CODEC);
        register("y_range", YRangeCondition.CODEC);
        register("inside_structure", InsideStructureCondition.CODEC);
        register("player_inventory_has", PlayerInventoryCondition.CODEC);
        register("blockstates_are", BlockStatesAreCondition.CODEC);
        register("blocks_are", BlocksAreCondition.CODEC);
        register("precipitation_at", PrecipitationAtCondition.CODEC);
        register("see_sky_at", SeeSkyAtCondition.CODEC);
        register("chance", ChanceCondition.CODEC);
        register("lunar_phase", LunarPhaseCondition.CODEC);
        register("time_of_day", TimeOfDayCondition.CODEC);
        register("every_amount_of_days", EveryAmountOfDaysCondition.CODEC);
    }

    static void register(String id, Codec<? extends Condition> codec) {
        Registry.register(MobifierRegistry.CONDITION, new ResourceLocation(Mobifier.MOD_ID, id), codec);
    }
}
