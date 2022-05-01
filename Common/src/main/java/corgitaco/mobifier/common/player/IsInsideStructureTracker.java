package corgitaco.mobifier.common.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.common.network.packet.EntityIsInsideStructureTrackerUpdatePacket;
import corgitaco.mobifier.util.ModLoaderContext;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;

public class IsInsideStructureTracker {

    private final Object2ObjectOpenHashMap<Holder<ConfiguredStructureFeature<?, ?>>, IsInside> tracker = new Object2ObjectOpenHashMap<>();

    public void setInside(Level world, Entity entity, Holder<ConfiguredStructureFeature<?, ?>> structure, IsInside isInside) {
        tracker.put(structure, isInside);
        if (!world.isClientSide) {
            ModLoaderContext.getInstance().sendToAllClients(((ServerLevel) world).players(), new EntityIsInsideStructureTrackerUpdatePacket(entity.getId(), structure, isInside));
        }
    }

    public void setInside(Holder<ConfiguredStructureFeature<?, ?>> structure, IsInside isInside) {
        tracker.put(structure, isInside);
    }

    public IsInside isInside(Holder<ConfiguredStructureFeature<?, ?>> structure) {
        return tracker.getOrDefault(structure, new IsInside(false, false));
    }


    public static class IsInside {

        public static final Codec<IsInside> CODEC = RecordCodecBuilder.create(builder -> {
            return builder.group(Codec.BOOL.fieldOf("insideStructure").forGetter(isInside -> isInside.insideStructure),
                    Codec.BOOL.fieldOf("insideStructurePiece").forGetter(isInside -> isInside.insideStructure)
            ).apply(builder, IsInside::new);
        });

        private boolean insideStructure;
        private boolean insideStructurePiece;

        public IsInside(boolean insideStructure, boolean insideStructurePiece) {
            this.insideStructure = insideStructure;
            this.insideStructurePiece = insideStructurePiece;
        }

        public boolean isInsideStructure() {
            return insideStructure;
        }

        public boolean isInsideStructurePiece() {
            return insideStructurePiece;
        }

        public IsInside setInsideStructure(boolean insideStructure) {
            this.insideStructure = insideStructure;
            return this;
        }

        public IsInside setInsideStructurePiece(boolean insideStructurePiece) {
            this.insideStructurePiece = insideStructurePiece;
            return this;
        }
    }

    public interface Access {
        IsInsideStructureTracker getIsInsideStructureTracker();
    }
}
