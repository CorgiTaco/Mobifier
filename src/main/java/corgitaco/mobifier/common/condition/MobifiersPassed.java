package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.common.util.comparator.DoubleComparator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class MobifiersPassed implements Condition {

    public static final Codec<MobifiersPassed> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(DoubleComparator.CODEC.fieldOf("mobifiers_passed_omparator").forGetter(mobifiersPassed -> {
                    return mobifiersPassed.doubleComparator;
                })
        ).apply(builder, MobifiersPassed::new);
    });

    private final DoubleComparator doubleComparator;

    public MobifiersPassed(DoubleComparator doubleComparator) {
        this.doubleComparator = doubleComparator;
    }

    @Override
    public boolean passes(World world, LivingEntity entity, boolean isDeadOrDying, int mobifiersPassed) {
        return doubleComparator.check(mobifiersPassed);
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}