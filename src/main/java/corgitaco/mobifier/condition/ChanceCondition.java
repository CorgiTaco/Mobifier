package corgitaco.mobifier.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public class ChanceCondition implements Condition {

    public static final Codec<ChanceCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Codec.DOUBLE.fieldOf("chanceCondition").forGetter(chanceCondition -> chanceCondition.chance)
        ).apply(builder, ChanceCondition::new);
    });

    private final double chance;

    public ChanceCondition(double chance) {
        this.chance = chance;
    }

    @Override
    public boolean passes(ServerWorld world, LivingEntity entity, boolean isDeath) {
        return chance > world.getRandom().nextDouble();
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}