package corgitaco.mobifier.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.corgilib.entity.condition.ConditionContext;
import corgitaco.mobifier.common.condition.Condition;
import corgitaco.corgilib.serialization.codec.CodecUtil;
import corgitaco.mobifier.common.util.DoubleModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record MobMobifier(DoubleModifier xpMultiplier, Map<Attribute, DoubleModifier> attributesMultipliers,
                          boolean dropDefaultTable, List<ResourceLocation> droppedTables,
                          List<Condition> conditionsRequiredToPass) {

    public static final Codec<MobMobifier> CODEC = RecordCodecBuilder.create(builder -> builder.group(DoubleModifier.CODEC.optionalFieldOf("xp_modifier", new DoubleModifier("+0.0")).forGetter(mobMobifier -> mobMobifier.xpMultiplier),
            Codec.unboundedMap(CodecUtil.ATTRIBUTE_CODEC, DoubleModifier.CODEC).optionalFieldOf("attribute_modifier", new HashMap<>()).forGetter(mobMobifier -> mobMobifier.attributesMultipliers),
            Codec.BOOL.optionalFieldOf("drop_death_table", true).forGetter(mobMobifier -> mobMobifier.dropDefaultTable),
            ResourceLocation.CODEC.listOf().optionalFieldOf("drops_tables", new ArrayList<>()).forGetter(mobMobifier -> mobMobifier.droppedTables),
            Condition.CODEC.listOf().fieldOf("conditions_to_apply").forGetter(mobMobifier -> mobMobifier.conditionsRequiredToPass)
    ).apply(builder, MobMobifier::new));

    public boolean passes(ConditionContext conditionContext) {
        for (Condition requiredToPass : this.conditionsRequiredToPass) {
            if (!requiredToPass.passes(conditionContext)) {
                return false;
            }
        }
        return true;
    }
}
