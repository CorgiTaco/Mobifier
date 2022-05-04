package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SeeSkyAtCondition implements Condition {

    public static Codec<SeeSkyAtCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(BlockPos.CODEC.optionalFieldOf("offset", BlockPos.ZERO).forGetter(precipitationAtCondition -> precipitationAtCondition.offset)).apply(builder, SeeSkyAtCondition::new);
    });

    private final BlockPos offset;

    public SeeSkyAtCondition(BlockPos offset) {
        this.offset = offset;
    }

    @Override
    public boolean passes(Level world, LivingEntity entity, boolean isDeadOrDying, int mobifiersPassed) {
        return world.canSeeSky(entity.blockPosition().offset(this.offset));
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}