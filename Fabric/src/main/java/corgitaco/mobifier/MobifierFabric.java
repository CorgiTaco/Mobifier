package corgitaco.mobifier;

import corgitaco.mobifier.network.FabricNetworkHandler;
import corgitaco.mobifier.util.ModLoaderContext;
import corgitaco.mobifier.util.S2CPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class MobifierFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        ModLoaderContext.setInstance(getModLoaderData());
        FabricNetworkHandler.init();
    }
    @NotNull
    public static ModLoaderContext getModLoaderData() {
        return new ModLoaderContext() {
            @Override
            public Path configPath() {
                return FabricLoader.getInstance().getConfigDir();
            }

            @Override
            public boolean isModLoaded(String isLoaded) {
                return FabricLoader.getInstance().isModLoaded(isLoaded);
            }

            @Override
            public <P extends S2CPacket> void sendToClient(ServerPlayer player, P packet) {
                FabricNetworkHandler.sendToPlayer(player, packet);
            }
        };
    }
}
