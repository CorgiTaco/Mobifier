package corgitaco.mobifier.util;

import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;
import java.util.List;

public interface ModLoaderContext {


    Path configPath();

    boolean isModLoaded(String isLoaded);

    static ModLoaderContext getInstance() {
        ModLoaderContext data = ContextStorage.CONTEXT;

        if (data == null) {
            throw new RuntimeException("Accessed ModLoaderContext too early!");
        }

        return data;
    }

    static ModLoaderContext setInstance(ModLoaderContext context) {
        ContextStorage.CONTEXT = context;
        return context;
    }

    <P extends S2CPacket> void sendToClient(ServerPlayer player, P packet);

    default <P extends S2CPacket> void sendToAllClients(List<ServerPlayer> players, P packet) {
        for (ServerPlayer player : players) {
            sendToClient(player, packet);
        }
    }
    class ContextStorage {
       private static ModLoaderContext CONTEXT = null;

    }
}
