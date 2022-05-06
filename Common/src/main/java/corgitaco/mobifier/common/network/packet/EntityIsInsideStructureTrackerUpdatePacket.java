package corgitaco.mobifier.common.network.packet;


import corgitaco.mobifier.common.player.IsInsideStructureTracker;
import corgitaco.mobifier.util.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class EntityIsInsideStructureTrackerUpdatePacket implements S2CPacket {

    private final int id;
    private final IsInsideStructureTracker.IsInside isInside;

    public EntityIsInsideStructureTrackerUpdatePacket(int id, IsInsideStructureTracker.IsInside isInside) {
        this.id = id;
        this.isInside = isInside;
    }


    public static EntityIsInsideStructureTrackerUpdatePacket readFromPacket(FriendlyByteBuf buf) {
        return new EntityIsInsideStructureTrackerUpdatePacket(buf.readVarInt(), buf.readWithCodec(IsInsideStructureTracker.IsInside.CODEC));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeWithCodec(IsInsideStructureTracker.IsInside.CODEC, this.isInside);
    }

    @Override
    public void handle(Level level) {
        Minecraft minecraft = Minecraft.getInstance();

        ClientLevel world = minecraft.level;
        if (world != null) {
            final Entity entity = world.getEntity(this.id);
            if (entity != null) {
                IsInsideStructureTracker.IsInside tracker = ((IsInsideStructureTracker.Access) entity).getIsInsideStructureTracker().getTracker();
                tracker.setInsideStructure(this.isInside.isInsideStructure());
                tracker.setInsideStructurePiece(this.isInside.isInsideStructurePiece());
            }
        }
    }
}