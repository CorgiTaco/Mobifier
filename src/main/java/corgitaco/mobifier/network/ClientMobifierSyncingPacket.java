package corgitaco.mobifier.network;

import corgitaco.mobifier.AttributesModifier;
import corgitaco.mobifier.config.AttributesModifierConfigSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.IOException;
import java.util.function.Supplier;

public class ClientMobifierSyncingPacket {

    private final AttributesModifierConfigSerializer.Holder holder;

    public ClientMobifierSyncingPacket(AttributesModifierConfigSerializer.Holder holder) {
        this.holder = holder;
    }

    public static void writeToPacket(ClientMobifierSyncingPacket packet, PacketBuffer buf) {
        try {
            buf.writeWithCodec(AttributesModifierConfigSerializer.Holder.CODEC, packet.holder);
        } catch (IOException e) {
            throw new IllegalStateException("Mobifier packet could not be written to. This is really really bad...\n\n" + e.getMessage());

        }
    }

    public static ClientMobifierSyncingPacket readFromPacket(PacketBuffer buf) {
        try {
            return new ClientMobifierSyncingPacket(buf.readWithCodec(AttributesModifierConfigSerializer.Holder.CODEC));
        } catch (IOException e) {
            throw new IllegalStateException("Mobifier packet could not be read. This is really really bad...\n\n" + e.getMessage());
        }
    }

    public static void handle(ClientMobifierSyncingPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> {
                AttributesModifier.ENTITY_DIFFICULTY_ATTRIBUTES_MODIFIERS.clear();
                AttributesModifier.ENTITY_DIFFICULTY_ATTRIBUTES_MODIFIERS.putAll(message.holder.getMap());

            });
        }
        ctx.get().setPacketHandled(true);
    }
}