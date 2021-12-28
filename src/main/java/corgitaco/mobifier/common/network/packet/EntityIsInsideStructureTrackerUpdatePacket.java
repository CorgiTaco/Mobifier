package corgitaco.mobifier.common.network.packet;


import corgitaco.mobifier.common.player.IsInsideStructureTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.IOException;
import java.util.function.Supplier;

public class EntityIsInsideStructureTrackerUpdatePacket {

    private final int id;
    private final Structure<?> structure;
    private final IsInsideStructureTracker.IsInside isInside;

    public EntityIsInsideStructureTrackerUpdatePacket(int id, Structure<?> structure, IsInsideStructureTracker.IsInside isInside) {
        this.id = id;
        this.structure = structure;
        this.isInside = isInside;
    }

    public static void writeToPacket(EntityIsInsideStructureTrackerUpdatePacket packet, PacketBuffer buf) {
        buf.writeVarInt(packet.id);
        buf.writeResourceLocation(Registry.STRUCTURE_FEATURE.getKey(packet.structure));
        try {
            buf.writeWithCodec(IsInsideStructureTracker.IsInside.CODEC, packet.isInside);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static EntityIsInsideStructureTrackerUpdatePacket readFromPacket(PacketBuffer buf) {
        try {
            return new EntityIsInsideStructureTrackerUpdatePacket(buf.readVarInt(), Registry.STRUCTURE_FEATURE.get(buf.readResourceLocation()), buf.readWithCodec(IsInsideStructureTracker.IsInside.CODEC));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e.getMessage());
        }
    }

    public static void handle(EntityIsInsideStructureTrackerUpdatePacket message, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> {
                Minecraft minecraft = Minecraft.getInstance();

                ClientWorld world = minecraft.level;
                if (world != null) {
                    final Entity entity = world.getEntity(message.id);
                    if (entity != null) {
                        ((IsInsideStructureTracker.Access) entity).getIsInsideStructureTracker().setInside(message.structure, message.isInside);
                    }
                }
            });
        }
        ctx.get().setPacketHandled(true);
    }
}