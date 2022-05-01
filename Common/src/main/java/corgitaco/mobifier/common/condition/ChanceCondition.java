package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ChanceCondition implements Condition {

    public static final Codec<ChanceCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Codec.DOUBLE.fieldOf("chance").forGetter(chanceCondition -> chanceCondition.chance)
        ).apply(builder, ChanceCondition::new);
    });

    private final double chance;

    public ChanceCondition(double chance) {
        this.chance = chance;
    }

    @Override
    public boolean passes(Level world, LivingEntity entity, boolean isDeadOrDying, int mobifiersPassed) {
        return isDeadOrDying && chance > world.getRandom().nextDouble();
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}