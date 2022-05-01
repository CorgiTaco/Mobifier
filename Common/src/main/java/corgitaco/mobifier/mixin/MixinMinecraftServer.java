package corgitaco.mobifier.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import corgitaco.mobifier.common.MobifierConfig;
import corgitaco.mobifier.util.ModLoaderContext;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Files;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Shadow
    @Final
    private RegistryAccess.Frozen registryHolder;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void handleModifiersConfig(Thread $$0, LevelStorageSource.LevelStorageAccess $$1, PackRepository $$2, WorldStem $$3, Proxy $$4, DataFixer $$5, MinecraftSessionService $$6, GameProfileRepository $$7, GameProfileCache $$8, ChunkProgressListenerFactory $$9, CallbackInfo ci) {
        if (MobifierConfig.getConfig(true).isDumpRegistries()) {
            StringBuilder filetxt = new StringBuilder();
            for (Registry<?> registry : Registry.REGISTRY) {
                filetxt.append(registry.key()).append("\n");
                filetxt.append(dumpRegistryElements(registry));
                filetxt.append("\n\n");
            }
            filetxt.append("\n----------------------------------Dynamic Registries----------------------------------\n\n\n");
            this.registryHolder.registries().forEach((registryEntry) -> {
                filetxt.append(registryEntry.key()).append("\n");
                filetxt.append(dumpRegistryElements(registryEntry.value()));
                filetxt.append("\n\n");
            });

            try {
                Files.write(ModLoaderContext.getInstance().configPath().resolve("registryDump.txt"), filetxt.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static <T> StringBuilder dumpRegistryElements(Registry<T> registry) {
        StringBuilder registryElements = new StringBuilder();
        int i = 0;
        for (T t : registry) {
            registryElements.append(i++).append(". \"").append(registry.getKey(t).toString()).append("\"\n");
        }

        return registryElements;
    }
}