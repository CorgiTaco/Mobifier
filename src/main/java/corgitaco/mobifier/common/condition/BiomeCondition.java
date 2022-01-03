package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.common.util.CodecUtil;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class BiomeCondition implements Condition {

    public static final Codec<BiomeCondition> CODEC = RecordCodecBuilder.create(builder -> builder.group(CodecUtil.BIOME_CODEC.listOf().fieldOf("biome_is").forGetter(biomeCondition -> new ArrayList<>(biomeCondition.biomes))).apply(builder, BiomeCondition::new));
    private final Set<RegistryKey<Biome>> biomes;

    public BiomeCondition(Collection<RegistryKey<Biome>> biomes) {
        this.biomes = new ObjectOpenHashSet<>(biomes);
    }

    @Override
    public boolean passes(World world, LivingEntity entity, boolean isDeadOrDying, int mobifiersPassed) {
        Optional<RegistryKey<Biome>> biomeKey = world.getBiomeName(entity.blockPosition());
        return biomeKey.isPresent() && this.biomes.contains(biomeKey.get());
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}
