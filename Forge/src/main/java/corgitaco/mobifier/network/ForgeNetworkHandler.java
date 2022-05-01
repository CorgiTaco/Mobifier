package corgitaco.mobifier.network;

import corgitaco.mobifier.Mobifier;
import corgitaco.mobifier.common.network.packet.EntityIsInsideStructureTrackerUpdatePacket;
import corgitaco.mobifier.common.network.packet.MobifierConfigSyncPacket;
import corgitaco.mobifier.util.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;
import java.util.function.Supplier;

public class ForgeNetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel SIMPLE_CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(Mobifier.MOD_ID, "network"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static void init() {
        Mobifier.LOGGER.info(String.format("Initializing %s network...", Mobifier.MOD_ID));
        SIMPLE_CHANNEL.registerMessage(0, MobifierConfigSyncPacket.class, MobifierConfigSyncPacket::write, MobifierConfigSyncPacket::readFromPacket, ForgeNetworkHandler::handle);
        SIMPLE_CHANNEL.registerMessage(1, EntityIsInsideStructureTrackerUpdatePacket.class, EntityIsInsideStructureTrackerUpdatePacket::write, EntityIsInsideStructureTrackerUpdatePacket::readFromPacket, ForgeNetworkHandler::handle);
        Mobifier.LOGGER.info(String.format("Initialized %s network!", Mobifier.MOD_ID));
    }

    public static void sendToPlayer(ServerPlayer playerEntity, Object objectToSend) {
        SIMPLE_CHANNEL.sendTo(objectToSend, playerEntity.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToAllPlayers(List<ServerPlayer> playerEntities, Object objectToSend) {
        for (ServerPlayer playerEntity : playerEntities) {
            SIMPLE_CHANNEL.sendTo(objectToSend, playerEntity.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void sendToServer(Object objectToSend) {
        SIMPLE_CHANNEL.sendToServer(objectToSend);
    }

    public static <T extends S2CPacket> void handle(T packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> {
                Client.clientHandle(packet);
            });
            context.setPacketHandled(true);
        }
    }


    private static class Client {
        private static <T extends S2CPacket> void clientHandle(T packet) {
            packet.handle(Minecraft.getInstance().level);
        }
    }
}