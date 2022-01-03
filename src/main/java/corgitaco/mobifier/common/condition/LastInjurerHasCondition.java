package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class LastInjurerHasCondition implements Condition {
    public static Codec<LastInjurerHasCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Condition.CODEC.listOf().fieldOf("conditions_to_apply").forGetter(lastInjurerByTypeHasCondition -> lastInjurerByTypeHasCondition.injurerConditions)
        ).apply(builder, LastInjurerHasCondition::new);
    });
    private final List<Condition> injurerConditions;

    public LastInjurerHasCondition(List<Condition> injurerConditions) {
        this.injurerConditions = injurerConditions;
    }

    @Override
    public boolean passes(World world, LivingEntity entity, boolean isDeadOrDying, int mobifiersPassed) {
        @Nullable
        LivingEntity lastHurtByMob = entity.getLastHurtByMob();

        if (lastHurtByMob == null) {
            return false;
        }

        for (Condition condition : injurerConditions) {
            if (!condition.passes(world, lastHurtByMob, isDeadOrDying, mobifiersPassed)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}
