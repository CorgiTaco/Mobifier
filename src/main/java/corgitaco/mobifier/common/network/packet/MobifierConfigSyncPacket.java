package corgitaco.mobifier.common.network.packet;


import corgitaco.mobifier.common.MobifierConfig;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.IOException;
import java.util.function.Supplier;

public class MobifierConfigSyncPacket {

    private final MobifierConfig config;

    public MobifierConfigSyncPacket(MobifierConfig config) {
        this.config = config;
    }

    public static void writeToPacket(MobifierConfigSyncPacket packet, PacketBuffer buf) {
        try {
            buf.writeWithCodec(MobifierConfig.CODEC, packet.config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MobifierConfigSyncPacket readFromPacket(PacketBuffer buf) {
        try {
            return new MobifierConfigSyncPacket(buf.readWithCodec(MobifierConfig.CODEC));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e.getMessage());
        }
    }

    public static void handle(MobifierConfigSyncPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> {
                MobifierConfig.setConfigInstance(message.config);
            });
        }
        ctx.get().setPacketHandled(true);
    }
}