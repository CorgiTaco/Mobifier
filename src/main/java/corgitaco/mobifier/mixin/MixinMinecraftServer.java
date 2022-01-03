package corgitaco.mobifier.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import corgitaco.mobifier.common.MobifierConfig;
import corgitaco.mobifier.mixin.access.DynamicRegistriesImplAccess;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.fml.loading.FMLPaths;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Files;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/Thread;Lnet/minecraft/util/registry/DynamicRegistries$Impl;Lnet/minecraft/world/storage/SaveFormat$LevelSave;Lnet/minecraft/world/storage/IServerConfiguration;Lnet/minecraft/resources/ResourcePackList;Ljava/net/Proxy;Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/resources/DataPackRegistries;Lcom/mojang/authlib/minecraft/MinecraftSessionService;Lcom/mojang/authlib/GameProfileRepository;Lnet/minecraft/server/management/PlayerProfileCache;Lnet/minecraft/world/chunk/listener/IChunkStatusListenerFactory;)V")
    private void handleModifiersConfig(Thread thread, DynamicRegistries.Impl impl, SaveFormat.LevelSave session, IServerConfiguration saveProperties, ResourcePackList resourcePackManager, Proxy proxy, DataFixer dataFixer, DataPackRegistries serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, PlayerProfileCache userCache, IChunkStatusListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        if (MobifierConfig.getConfig(true).isDumpRegistries()) {
            StringBuilder filetxt = new StringBuilder();
            for (Registry<?> registry : Registry.REGISTRY) {
                filetxt.append(registry.key()).append("\n");
                filetxt.append(dumpRegistryElements(registry));
                filetxt.append("\n\n");
            }
            filetxt.append("\n----------------------------------Dynamic Registries----------------------------------\n\n\n");
            ((DynamicRegistriesImplAccess) (Object) impl).getRegistries().forEach((registryKey, registry) -> {
                filetxt.append(registryKey).append("\n");
                filetxt.append(dumpRegistryElements(registry));
                filetxt.append("\n\n");
            });

            try {
                Files.write(FMLPaths.CONFIGDIR.get().resolve("registryDump.txt"), filetxt.toString().getBytes());
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