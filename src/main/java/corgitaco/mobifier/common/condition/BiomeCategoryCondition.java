package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class BiomeCategoryCondition implements Condition {

    public static final Codec<BiomeCategoryCondition> CODEC = RecordCodecBuilder.create(builder -> builder.group(Biome.Category.CODEC.listOf().fieldOf("isBiomeCategory").forGetter(biomeCategoryCondition -> new ArrayList<>(biomeCategoryCondition.biomeCategories))).apply(builder, BiomeCategoryCondition::new));
    private final Set<Biome.Category> biomeCategories;

    public BiomeCategoryCondition(Collection<Biome.Category> biomeCategories) {
        this.biomeCategories = new ObjectOpenHashSet<>(biomeCategories);
    }

    @Override
    public boolean passes(ServerWorld world, LivingEntity entity, boolean isDeadOrDying) {
        final Biome biome = world.getBiome(entity.blockPosition());
        return biomeCategories.contains(biome.getBiomeCategory());
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}
