package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class BlockStatesAreCondition implements Condition {

    public static final Codec<BlockStatesAreCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(BlockStateIs.CODEC.listOf().fieldOf("has").forGetter(blockStatesAreCondition -> new ArrayList<>(blockStatesAreCondition.blockStatesAre))).apply(builder, BlockStatesAreCondition::new);
    });

    private final Set<BlockStateIs> blockStatesAre;

    public BlockStatesAreCondition(List<BlockStateIs> blockStatesAre) {
        this.blockStatesAre = new ObjectOpenHashSet<>(blockStatesAre);
    }

    @Override
    public boolean passes(World world, LivingEntity entity, boolean isDeadOrDying) {
        for (BlockStateIs blockStateIs : blockStatesAre) {
            BlockPos offsetPos = entity.blockPosition().offset(blockStateIs.offset);

            if (!blockStateIs.is.contains(world.getBlockState(offsetPos))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }

    public static class BlockStateIs {
        public static final Codec<BlockStateIs> CODEC = RecordCodecBuilder.create(builder -> {
            return builder.group(BlockPos.CODEC.optionalFieldOf("offset", BlockPos.ZERO).forGetter(blockStateIs -> blockStateIs.offset),
                    BlockState.CODEC.listOf().fieldOf("is").forGetter(blockStateIs -> new ArrayList<>(blockStateIs.is))).apply(builder, BlockStateIs::new);
        });

        private final BlockPos offset;
        private final Set<BlockState> is;

        public BlockStateIs(BlockPos offset, Collection<BlockState> is) {
            this.offset = offset;
            this.is = new ObjectOpenHashSet<>(is);
        }
    }
}