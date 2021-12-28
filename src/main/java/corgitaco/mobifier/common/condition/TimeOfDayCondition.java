package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.common.util.pair.LongPair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.util.List;

public class TimeOfDayCondition implements Condition {

    public static final Codec<TimeOfDayCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(LongPair.createLongPairCodec("minTime", "maxTime").listOf().fieldOf("timesOfDay").forGetter(timeOfDayCondition -> timeOfDayCondition.timesOfDay),
                Codec.LONG.optionalFieldOf("dayLength", 24000L).forGetter(timeOfDayCondition -> timeOfDayCondition.dayLength)
        ).apply(builder, TimeOfDayCondition::new);
    });

    private final List<LongPair> timesOfDay;
    private final long dayLength;

    public TimeOfDayCondition(List<LongPair> timesOfDay, long dayLength) {
        this.dayLength = dayLength;
        if (timesOfDay.isEmpty()) {
            throw new IllegalArgumentException("No times of day were specified.");
        }

        this.timesOfDay = timesOfDay;
    }


    @Override
    public boolean passes(World world, LivingEntity entity, boolean isDeadOrDying) {
        for (LongPair longPair : this.timesOfDay) {
            if (longPair.isInBetween(world.getDayTime() % this.dayLength)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}