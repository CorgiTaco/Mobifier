package corgitaco.mobifier.mixin;

import corgitaco.mobifier.common.MobifierConfig;
import corgitaco.mobifier.common.network.NetworkHandler;
import corgitaco.mobifier.common.network.packet.MobifierConfigSyncPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class MixinPlayerList {

    @Inject(method = "sendLevelInfo", at = @At(value = "HEAD"))
    private void sendContext(ServerPlayerEntity playerIn, ServerWorld worldIn, CallbackInfo ci) {
        NetworkHandler.sendToPlayer(playerIn, new MobifierConfigSyncPacket(MobifierConfig.getConfig()));
    }
}
