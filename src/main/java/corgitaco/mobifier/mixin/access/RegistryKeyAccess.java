package corgitaco.mobifier.mixin.access;

import net.minecraft.util.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RegistryKey.class)
public interface RegistryKeyAccess {

    @Accessor
    static Map<String, RegistryKey<?>> getVALUES() {
        throw new Error("Mixin did not apply!");
    }
}
