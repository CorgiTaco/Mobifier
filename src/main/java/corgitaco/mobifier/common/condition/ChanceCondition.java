package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

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
    public boolean passes(World world, LivingEntity entity, boolean isDeadOrDying) {
        return chance > world.getRandom().nextDouble();
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}