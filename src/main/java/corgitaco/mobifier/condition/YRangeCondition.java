package corgitaco.mobifier.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

//TODO: Use vertical anchors in 1.18.
public class YRangeCondition implements Condition {
    public static final Codec<YRangeCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(YRange.CODEC.listOf().fieldOf("yRanges").forGetter(yRangeCondition -> yRangeCondition.yRanges)).apply(builder, YRangeCondition::new);
    });

    private final List<YRange> yRanges;

    public YRangeCondition(List<YRange> yRanges) {
        this.yRanges = yRanges;
        if (yRanges.isEmpty()) {
            throw new IllegalArgumentException("No yRanges were specified.");
        }
    }


    @Override
    public boolean passes(ServerWorld world, LivingEntity entity, boolean isDeath) {
        for (YRange yRange : yRanges) {
            if (!yRange.isInBetween(entity.blockPosition().getY())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    public static class YRange {
        public static Codec<YRange> CODEC = RecordCodecBuilder.create(builder -> {
            return builder.group(Codec.INT.fieldOf("minY").forGetter(yRange -> yRange.minY),
                    Codec.INT.fieldOf("maxY").forGetter(yRange -> yRange.maxY)
            ).apply(builder, YRange::new);
        });

        private final int minY;
        private final int maxY;

        public YRange(int minY, int maxY) {
            this.minY = minY;
            this.maxY = maxY;
        }

        public boolean isInBetween(int y) {
            return y >= this.minY && y <= this.maxY;
        }
    }
}
