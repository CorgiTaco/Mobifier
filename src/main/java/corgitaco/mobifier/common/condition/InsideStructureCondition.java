package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.common.player.IsInsideStructureTracker;
import corgitaco.mobifier.common.util.CodecUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;

import java.util.List;
import java.util.Optional;

public class InsideStructureCondition implements Condition {

    public static final Codec<InsideStructureCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(CodecUtil.STRUCTURE_CODEC.listOf().fieldOf("structures").forGetter(insideStructureCondition -> insideStructureCondition.structures),
                Codec.BOOL.fieldOf("mustIntersectPiece").forGetter(insideStructureCondition -> insideStructureCondition.intersectsPiece)
        ).apply(builder, InsideStructureCondition::new);
    });

    private final List<Structure<?>> structures;
    private final boolean intersectsPiece;

    public InsideStructureCondition(List<Structure<?>> structures, boolean mustIntersectPiece) {
        if (structures.isEmpty()) {
            throw new IllegalArgumentException("No structures were specified.");
        }
        this.structures = structures;
        this.intersectsPiece = mustIntersectPiece;
    }

    @Override
    public boolean passes(World world, LivingEntity entity, boolean isDeadOrDying) {
        if (world.isClientSide) {
            for (Structure<?> structure : structures) {
                if (this.intersectsPiece) {
                    if (((IsInsideStructureTracker.Access) entity).getIsInsideStructureTracker().isInside(structure).isInsideStructurePiece()) {
                        return true;
                    }
                } else {
                    if (((IsInsideStructureTracker.Access) entity).getIsInsideStructureTracker().isInside(structure).isInsideStructure()) {
                        return true;
                    }
                }
            }
        } else {

            for (Structure<?> structure : structures) {
                BlockPos entityPosition = entity.blockPosition();
                Optional<? extends StructureStart<?>> possibleStructureStart = ((ISeedReader) world).startsForFeature(SectionPos.of(entityPosition), structure).findFirst();

                if (!possibleStructureStart.isPresent()) {
                    return false;
                }

                StructureStart<?> structureStart = possibleStructureStart.get();

                if (this.intersectsPiece) {
                    for (StructurePiece piece : structureStart.getPieces()) {
                        if (piece.getBoundingBox().isInside(entityPosition)) {
                            ((IsInsideStructureTracker.Access) entity).getIsInsideStructureTracker().setInside(world, entity, structure, new IsInsideStructureTracker.IsInside(true, true));
                            return true;
                        } else {
                            ((IsInsideStructureTracker.Access) entity).getIsInsideStructureTracker().setInside(world, entity, structure, new IsInsideStructureTracker.IsInside(structureStart.getBoundingBox().isInside(entityPosition), false));
                        }
                    }
                } else {
                    if (structureStart.getBoundingBox().isInside(entityPosition)) {
                        ((IsInsideStructureTracker.Access) entity).getIsInsideStructureTracker().setInside(world, entity, structure, new IsInsideStructureTracker.IsInside(true, false));
                        return true;
                    } else {
                        ((IsInsideStructureTracker.Access) entity).getIsInsideStructureTracker().setInside(world, entity, structure, new IsInsideStructureTracker.IsInside(false, true));
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}
