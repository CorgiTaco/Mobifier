package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class IsDeadOrDyingCondition implements Condition {
    public static final IsDeadOrDyingCondition INSTANCE = new IsDeadOrDyingCondition();
    public static final Codec<IsDeadOrDyingCondition> CODEC = Codec.unit(() -> {
        return INSTANCE;
    });

    @Override
    public boolean passes(Level world, LivingEntity entity, boolean isDeadOrDying, int mobifiersPassed) {
        return isDeadOrDying;
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}
