package corgitaco.mobifier.mixin;

import corgitaco.mobifier.common.MobifierConfig;
import corgitaco.mobifier.common.network.packet.MobifierConfigSyncPacket;
import corgitaco.mobifier.util.ModLoaderContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class MixinPlayerList {

    @Inject(method = "sendLevelInfo", at = @At(value = "HEAD"))
    private void sendContext(ServerPlayer playerIn, ServerLevel worldIn, CallbackInfo ci) {
        ModLoaderContext.getInstance().sendToClient(playerIn, new MobifierConfigSyncPacket(MobifierConfig.getConfig()));
    }
}
