package corgitaco.mobifier.common.network;

import corgitaco.mobifier.Mobifier;
import corgitaco.mobifier.common.network.packet.EntityIsInsideStructureTrackerUpdatePacket;
import corgitaco.mobifier.common.network.packet.MobifierConfigSyncPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.List;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel SIMPLE_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Mobifier.MOD_ID, "network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        SIMPLE_CHANNEL.registerMessage(0, MobifierConfigSyncPacket.class, MobifierConfigSyncPacket::writeToPacket, MobifierConfigSyncPacket::readFromPacket, MobifierConfigSyncPacket::handle);
        SIMPLE_CHANNEL.registerMessage(1, EntityIsInsideStructureTrackerUpdatePacket.class, EntityIsInsideStructureTrackerUpdatePacket::writeToPacket, EntityIsInsideStructureTrackerUpdatePacket::readFromPacket, EntityIsInsideStructureTrackerUpdatePacket::handle);
    }

    public static void sendToPlayer(ServerPlayerEntity playerEntity, Object objectToSend) {
        SIMPLE_CHANNEL.sendTo(objectToSend, playerEntity.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToAllPlayers(List<ServerPlayerEntity> playerEntities, Object objectToSend) {
        for (ServerPlayerEntity playerEntity : playerEntities) {
            SIMPLE_CHANNEL.sendTo(objectToSend, playerEntity.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void sendToServer(Object objectToSend) {
        SIMPLE_CHANNEL.sendToServer(objectToSend);
    }
}