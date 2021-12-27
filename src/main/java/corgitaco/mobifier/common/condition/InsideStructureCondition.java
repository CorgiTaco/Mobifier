package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Optional;

public class InsideStructureCondition implements Condition {

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
    public boolean passes(ServerWorld world, LivingEntity entity, boolean isDeadOrDying) {

        for (Structure<?> structure : structures) {
            BlockPos entityPosition = entity.blockPosition();
            Optional<? extends StructureStart<?>> possibleStructureStart = world.startsForFeature(SectionPos.of(entityPosition), structure).findFirst();

            if (!possibleStructureStart.isPresent()) {
                return false;
            }

            StructureStart<?> structureStart = possibleStructureStart.get();

            if (this.intersectsPiece) {
                for (StructurePiece piece : structureStart.getPieces()) {
                    if(piece.getBoundingBox().isInside(entityPosition)) {
                        return true;
                    }
                }
            } else {
                if (structureStart.getBoundingBox().isInside(entityPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Codec<? extends Condition> codec() {
        return null;
    }
}
