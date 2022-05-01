package corgitaco.mobifier.common.network.packet;


import corgitaco.mobifier.common.player.IsInsideStructureTracker;
import corgitaco.mobifier.util.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;

public class EntityIsInsideStructureTrackerUpdatePacket implements S2CPacket {

    private final int id;
    private final ResourceKey<ConfiguredStructureFeature<?, ?>> structure;
    private final IsInsideStructureTracker.IsInside isInside;

    public EntityIsInsideStructureTrackerUpdatePacket(int id, Holder<ConfiguredStructureFeature<?, ?>> structure, IsInsideStructureTracker.IsInside isInside) {
        this(id, structure.unwrapKey().orElseThrow(), isInside);
    }

    public EntityIsInsideStructureTrackerUpdatePacket(int id, ResourceKey<ConfiguredStructureFeature<?, ?>> structure, IsInsideStructureTracker.IsInside isInside) {
        this.id = id;
        this.structure = structure;
        this.isInside = isInside;
    }


    public static EntityIsInsideStructureTrackerUpdatePacket readFromPacket(FriendlyByteBuf buf) {
        return new EntityIsInsideStructureTrackerUpdatePacket(buf.readVarInt(), ResourceKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, buf.readResourceLocation()), buf.readWithCodec(IsInsideStructureTracker.IsInside.CODEC));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeResourceLocation(this.structure.location());
        buf.writeWithCodec(IsInsideStructureTracker.IsInside.CODEC, this.isInside);
    }

    @Override
    public void handle(Level level) {
        Minecraft minecraft = Minecraft.getInstance();

        ClientLevel world = minecraft.level;
        if (world != null) {
            final Entity entity = world.getEntity(this.id);
            if (entity != null) {
                ((IsInsideStructureTracker.Access) entity).getIsInsideStructureTracker().setInside(world.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).getHolderOrThrow(this.structure), this.isInside);
            }
        }
    }
}