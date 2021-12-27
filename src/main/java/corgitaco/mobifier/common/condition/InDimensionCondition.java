package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class InDimensionCondition implements Condition {

    public static Codec<InDimensionCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(World.RESOURCE_KEY_CODEC.listOf().fieldOf("dimension_is").forGetter(inDimensionCondition -> new ArrayList<>(inDimensionCondition.validWorlds))
        ).apply(builder, InDimensionCondition::new);
    });
    private final Set<RegistryKey<World>> validWorlds;

    public InDimensionCondition(Collection<RegistryKey<World>> validWorlds) {
        this.validWorlds = new ObjectOpenHashSet<>(validWorlds);
    }

    @Override
    public boolean passes(ServerWorld world, LivingEntity entity, boolean isDeadOrDying) {
        return this.validWorlds.contains(world.dimension());
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}
