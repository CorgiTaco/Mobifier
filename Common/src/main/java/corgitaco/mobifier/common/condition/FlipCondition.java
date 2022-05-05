package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class FlipCondition implements Condition {

    public static final Codec<FlipCondition> CODEC = RecordCodecBuilder.create(builder -> builder.group(Condition.CODEC.fieldOf("condition_to_flip").forGetter(flipCondition -> flipCondition.condition)).apply(builder, FlipCondition::new));

    private final Condition condition;

    public FlipCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public boolean passes(Level world, LivingEntity entity, boolean isDeadOrDying, int mobifiersPassed) {
        return !this.condition.passes(world, entity, isDeadOrDying, mobifiersPassed);
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}