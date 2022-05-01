package corgitaco.mobifier.common.network.packet;


import corgitaco.mobifier.common.MobifierConfig;
import corgitaco.mobifier.util.S2CPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

public class MobifierConfigSyncPacket implements S2CPacket {

    private final MobifierConfig config;

    public MobifierConfigSyncPacket(MobifierConfig config) {
        this.config = config;
    }

    public static MobifierConfigSyncPacket readFromPacket(FriendlyByteBuf buf) {
        return new MobifierConfigSyncPacket(buf.readWithCodec(MobifierConfig.CODEC));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeWithCodec(MobifierConfig.CODEC, this.config);

    }

    @Override
    public void handle(Level level) {
        MobifierConfig.setConfigInstance(this.config);
    }
}