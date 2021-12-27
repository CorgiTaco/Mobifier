package corgitaco.mobifier.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.common.condition.Condition;
import corgitaco.mobifier.common.util.CodecUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Map;

public class MobMobifier {

    public static final Codec<MobMobifier> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Codec.DOUBLE.fieldOf("xpMultiplier").forGetter(mobMobifier -> mobMobifier.xpMultiplier),
                Codec.unboundedMap(CodecUtil.ATTRIBUTE_CODEC, Codec.DOUBLE).fieldOf("attributesMultipliers").forGetter(mobMobifier -> mobMobifier.attributesMultipliers),
                Codec.BOOL.fieldOf("dropDefaultTable").forGetter(mobMobifier -> mobMobifier.dropDefaultTable),
                ResourceLocation.CODEC.listOf().fieldOf("droppedTables").forGetter(mobMobifier -> mobMobifier.droppedTables),
                Condition.CODEC.listOf().fieldOf("conditionsRequiredToPass").forGetter(mobMobifier -> mobMobifier.conditionsRequiredToPass)
        ).apply(builder, MobMobifier::new);
    });

    private final double xpMultiplier;
    private final Map<Attribute, Double> attributesMultipliers;
    private final boolean dropDefaultTable;
    private final List<ResourceLocation> droppedTables;
    private final List<Condition> conditionsRequiredToPass;

    public MobMobifier(double xpMultiplier, Map<Attribute, Double> attributesMultipliers, boolean dropDefaultTable, List<ResourceLocation> droppedTables, List<Condition> conditionsRequiredToPass) {
        this.xpMultiplier = xpMultiplier;
        this.attributesMultipliers = attributesMultipliers;
        this.dropDefaultTable = dropDefaultTable;
        this.droppedTables = droppedTables;
        this.conditionsRequiredToPass = conditionsRequiredToPass;
    }

    public double getXpMultiplier() {
        return xpMultiplier;
    }

    public Map<Attribute, Double> getAttributesMultipliers() {
        return attributesMultipliers;
    }

    public boolean isDropDefaultTable() {
        return dropDefaultTable;
    }

    public List<ResourceLocation> getDroppedTables() {
        return droppedTables;
    }

    public List<Condition> getConditionsRequiredToPass() {
        return conditionsRequiredToPass;
    }

    public boolean passes(ServerWorld world, LivingEntity entity, boolean isDeadOrDying) {
        for (Condition requiredToPass : this.conditionsRequiredToPass) {
            if (!requiredToPass.passes(world, entity, isDeadOrDying)) {
                return false;
            }
        }
        return true;
    }
}
