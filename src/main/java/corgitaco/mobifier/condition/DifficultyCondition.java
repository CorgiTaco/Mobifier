package corgitaco.mobifier.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.CodecUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;

public class DifficultyCondition implements Condition {

    public static final Codec<DifficultyCondition> CODEC = RecordCodecBuilder.create(builder -> builder.group(Codec.unboundedMap(CodecUtil.DIFFICULTY_CODEC, Codec.BOOL).fieldOf("isDifficulty").forGetter(difficultyCondition -> difficultyCondition.isDifficulty)).apply(builder, DifficultyCondition::new));
    private final Map<Difficulty, Boolean> isDifficulty;

    public DifficultyCondition(Map<Difficulty, Boolean> isDifficulty) {
        this.isDifficulty = isDifficulty;
    }

    @Override
    public boolean passes(ServerWorld world, LivingEntity entity, boolean isDeath) {
        return this.isDifficulty.getOrDefault(world.getDifficulty(), false);
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}
