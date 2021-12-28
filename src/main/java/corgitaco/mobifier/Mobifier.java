package corgitaco.mobifier;

import corgitaco.mobifier.common.MobifierConfig;
import corgitaco.mobifier.common.condition.Condition;
import corgitaco.mobifier.common.network.NetworkHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;

@Mod(Mobifier.MOD_ID)
public class Mobifier {
    public static final String MOD_ID = "mobifier";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Path CONFIG_PATH = new File(String.valueOf(FMLPaths.CONFIGDIR.get().resolve(MOD_ID))).toPath();

    public Mobifier() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        Condition.register();
        MobifierConfig.getConfig(true);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.init();
    }
}
