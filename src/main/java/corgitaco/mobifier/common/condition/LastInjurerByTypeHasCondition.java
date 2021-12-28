package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.common.util.CodecUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class LastInjurerByTypeHasCondition implements Condition {
    public static Codec<LastInjurerByTypeHasCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Codec.unboundedMap(CodecUtil.ENTITY_TYPE_CODEC, Condition.CODEC.listOf()).fieldOf("conditionsRequiredToPass").forGetter(lastInjurerByTypeHasCondition -> lastInjurerByTypeHasCondition.injurerConditions)
        ).apply(builder, LastInjurerByTypeHasCondition::new);
    });
    private final Map<EntityType<?>, List<Condition>> injurerConditions;

    public LastInjurerByTypeHasCondition(Map<EntityType<?>, List<Condition>> injurerConditions) {
        this.injurerConditions = injurerConditions;
    }

    @Override
    public boolean passes(World world,LivingEntity entity, boolean isDeadOrDying) {
        @Nullable
        LivingEntity lastHurtByMob = entity.getLastHurtByMob();

        if (lastHurtByMob == null) {
            return false;
        }
        EntityType<?> lastHurtByMobType = lastHurtByMob.getType();
        if (injurerConditions.containsKey(lastHurtByMobType)) {
            List<Condition> conditions = injurerConditions.get(lastHurtByMobType);

            for (Condition condition : conditions) {
                if (!condition.passes(world, lastHurtByMob, isDeadOrDying)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}
