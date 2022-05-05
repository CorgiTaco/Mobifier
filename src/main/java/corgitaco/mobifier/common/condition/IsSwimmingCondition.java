package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class IsSwimmingCondition implements Condition {

    public static final Codec<IsSwimmingCondition> CODEC = Codec.unit(IsSwimmingCondition::new);

    @Override
    public boolean passes(World world, LivingEntity entity, boolean isDeadOrDying, int mobifiersPassed) {
        return entity.isSwimming();
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}