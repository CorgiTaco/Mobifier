package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.common.player.IsInsideStructureTracker;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.List;
import java.util.Optional;

public class InsideStructureTagCondition implements Condition {

    public static final Codec<InsideStructureTagCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(TagKey.codec(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).listOf().fieldOf("structure_tag_is").forGetter(insideStructureTagCondition -> insideStructureTagCondition.structureTags),
            Codec.BOOL.optionalFieldOf("in_piece", false).forGetter(insideStructureTagCondition -> insideStructureTagCondition.intersectsPiece)
        ).apply(builder, InsideStructureTagCondition::new);
    });

    private final List<TagKey<ConfiguredStructureFeature<?, ?>>> structureTags;
    private final boolean intersectsPiece;

    public InsideStructureTagCondition(List<TagKey<ConfiguredStructureFeature<?, ?>>> structureTags, boolean mustIntersectPiece) {
        if (structureTags.isEmpty()) {
            throw new IllegalArgumentException("No structures were specified.");
        }
        this.structureTags = structureTags;
        this.intersectsPiece = mustIntersectPiece;
    }

    @Override
    public boolean passes(Level world, LivingEntity entity, boolean isDeadOrDying, int mobifiersPassed) {
        Registry<ConfiguredStructureFeature<?, ?>> configuredStructureFeatures = world.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
        if (world.isClientSide) {
            return clientPasses((IsInsideStructureTracker.Access) entity);
        } else {
            for (TagKey<ConfiguredStructureFeature<?, ?>> structureTag : structureTags) {
                HolderSet.Named<ConfiguredStructureFeature<?, ?>> tag = configuredStructureFeatures.getOrCreateTag(structureTag);

                List<Holder<ConfiguredStructureFeature<?, ?>>> structures = tag.stream().toList();

                for (Holder<ConfiguredStructureFeature<?, ?>> structure : structures) {
                    BlockPos entityPosition = entity.blockPosition();
                    Optional<? extends StructureStart> possibleStructureStart = ((ServerLevel) world).structureFeatureManager().startsForFeature(SectionPos.of(entityPosition), structure.value()).stream().findFirst();
                    ResourceKey<ConfiguredStructureFeature<?, ?>> key = structure.unwrapKey().orElseThrow();

                    if (possibleStructureStart.isEmpty()) {
                        return false;
                    }

                    StructureStart structureStart = possibleStructureStart.get();

                    if (this.intersectsPiece) {
                        for (StructurePiece piece : structureStart.getPieces()) {
                            if (piece.getBoundingBox().isInside(entityPosition)) {
                                ((IsInsideStructureTracker.Access) entity).getIsInsideStructureTracker().setInside(world, entity, new IsInsideStructureTracker.IsInside(true, true));
                                return true;
                            } else {
                                ((IsInsideStructureTracker.Access) entity).getIsInsideStructureTracker().setInside(world, entity, new IsInsideStructureTracker.IsInside(structureStart.getBoundingBox().isInside(entityPosition), false));
                            }
                        }
                    } else {
                        if (structureStart.getBoundingBox().isInside(entityPosition)) {
                            ((IsInsideStructureTracker.Access) entity).getIsInsideStructureTracker().setInside(world, entity, new IsInsideStructureTracker.IsInside(true, false));
                            return true;
                        } else {
                            ((IsInsideStructureTracker.Access) entity).getIsInsideStructureTracker().setInside(world, entity, new IsInsideStructureTracker.IsInside(false, true));
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean clientPasses(IsInsideStructureTracker.Access entity) {
        IsInsideStructureTracker.IsInside tracker = entity.getIsInsideStructureTracker().getTracker();
        return (tracker.isInsideStructurePiece() && this.intersectsPiece) || tracker.isInsideStructure();
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}
