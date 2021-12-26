package corgitaco.mobifier.condition;

import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public class DieCondition implements Condition {
    public static final DieCondition INSTANCE = new DieCondition();
    public static final Codec<DieCondition> CODEC = Codec.unit(() -> {
        return INSTANCE;
    });

    @Override
    public boolean passes(ServerWorld world, LivingEntity entity, boolean isDeath) {
        return isDeath;
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}
