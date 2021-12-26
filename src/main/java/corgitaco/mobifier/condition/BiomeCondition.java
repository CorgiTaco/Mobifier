package corgitaco.mobifier.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.CodecUtil;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class BiomeCondition implements Condition {

    public static final Codec<BiomeCondition> CODEC = RecordCodecBuilder.create(builder -> builder.group(CodecUtil.BIOME_CODEC.listOf().fieldOf("isBiome").forGetter(biomeCondition -> new ArrayList<>(biomeCondition.biomes))).apply(builder, BiomeCondition::new));
    private final Set<RegistryKey<Biome>> biomes;

    public BiomeCondition(Collection<RegistryKey<Biome>> biomes) {
        this.biomes = new ObjectOpenHashSet<>(biomes);
    }

    @Override
    public boolean passes(ServerWorld world, LivingEntity entity, boolean isDeath) {
        Optional<RegistryKey<Biome>> biomeKey = world.getBiomeName(entity.blockPosition());
        return biomeKey.isPresent() && this.biomes.contains(biomeKey.get());
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}
