package corgitaco.mobifier;

import corgitaco.mobifier.common.condition.Condition;
import corgitaco.mobifier.network.ForgeNetworkHandler;
import corgitaco.mobifier.util.ModLoaderContext;
import corgitaco.mobifier.util.S2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@Mod(Mobifier.MOD_ID)
public class MobifierForge {

    public MobifierForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        Condition.register();
        ModLoaderContext.setInstance(getModLoaderData());
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        ForgeNetworkHandler.init();
    }

    @NotNull
    private static ModLoaderContext getModLoaderData() {
        return new ModLoaderContext() {
            @Override
            public Path configPath() {
                return FMLPaths.CONFIGDIR.get();
            }

            @Override
            public boolean isModLoaded(String isLoaded) {
                return ModList.get().isLoaded(isLoaded);
            }

            @Override
            public <P extends S2CPacket> void sendToClient(ServerPlayer player, P packet) {
                ForgeNetworkHandler.sendToPlayer(player, packet);
            }
        };
    }
}