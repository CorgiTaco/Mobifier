package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class SeeSkyAtCondition implements Condition {

    public static Codec<SeeSkyAtCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(BlockPos.CODEC.optionalFieldOf("offset", BlockPos.ZERO).forGetter(precipitationAtCondition -> precipitationAtCondition.offset)).apply(builder, SeeSkyAtCondition::new);
    });

    private final BlockPos offset;

    public SeeSkyAtCondition(BlockPos offset) {
        this.offset = offset;
    }

    @Override
    public boolean passes(ServerWorld world, LivingEntity entity, boolean isDeadOrDying) {
        return world.canSeeSky(entity.blockPosition().offset(this.offset));
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}
