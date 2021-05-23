package corgitaco.mobifier.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import corgitaco.mobifier.Mobifier;
import corgitaco.mobifier.config.AttributesModifierConfigSerializer;
import it.unimi.dsi.fastutil.objects.Reference2DoubleArrayMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.Util;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.*;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/Thread;Lnet/minecraft/util/registry/DynamicRegistries$Impl;Lnet/minecraft/world/storage/SaveFormat$LevelSave;Lnet/minecraft/world/storage/IServerConfiguration;Lnet/minecraft/resources/ResourcePackList;Ljava/net/Proxy;Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/resources/DataPackRegistries;Lcom/mojang/authlib/minecraft/MinecraftSessionService;Lcom/mojang/authlib/GameProfileRepository;Lnet/minecraft/server/management/PlayerProfileCache;Lnet/minecraft/world/chunk/listener/IChunkStatusListenerFactory;)V")
    private void handleModifiersConfig(Thread thread, DynamicRegistries.Impl impl, SaveFormat.LevelSave session, IServerConfiguration saveProperties, ResourcePackList resourcePackManager, Proxy proxy, DataFixer dataFixer, DataPackRegistries serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, PlayerProfileCache userCache, IChunkStatusListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        SortedMap<String, EnumMap<Difficulty, Reference2DoubleArrayMap<String>>> entityDifficultyAttributesModifiersDefaults = Util.make(new TreeMap<>(Comparator.comparing(Objects::toString)), (map) -> {
            for (EntityType<?> entityType : ForgeRegistries.ENTITIES) {
                if (GlobalEntityTypeAttributes.hasSupplier(entityType)) {
                    AttributeModifierMap attributeModifierMap = GlobalEntityTypeAttributes.getSupplier((EntityType<? extends LivingEntity>) entityType);
                    Reference2DoubleArrayMap<String> doubleArrayMap = new Reference2DoubleArrayMap<>();
                    for (Attribute attribute : ForgeRegistries.ATTRIBUTES) {
                        if (attributeModifierMap.hasAttribute(attribute)) {
                            doubleArrayMap.put(ForgeRegistries.ATTRIBUTES.getKey(attribute).toString(), 1.0D);
                        }
                    }
                    EnumMap<Difficulty, Reference2DoubleArrayMap<String>> difficultyMap = new EnumMap<>(Difficulty.class);
                    for (Difficulty difficulty : Difficulty.values()) {
                        difficultyMap.put(difficulty, doubleArrayMap);
                    }
                    map.put(ForgeRegistries.ENTITIES.getKey(entityType).toString(), difficultyMap);
                }
            }
        });
        AttributesModifierConfigSerializer.handleConfig(Mobifier.CONFIG_PATH.resolve("mobifiers.json"), entityDifficultyAttributesModifiersDefaults);
    }
}