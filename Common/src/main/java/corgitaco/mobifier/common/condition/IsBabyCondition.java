package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class IsBabyCondition implements Condition {

    public static final Codec<IsBabyCondition> CODEC = Codec.unit(IsBabyCondition::new);

    @Override
    public boolean passes(Level world, LivingEntity entity, boolean isDeadOrDying, int mobifiersPassed) {
        return entity.isBaby();
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}