package corgitaco.mobifier.mixin.access;

import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ResourceKey.class)
public interface RegistryKeyAccess {

    @Accessor
    static Map<String, ResourceKey<?>> getVALUES() {
        throw new Error("Mixin did not apply!");
    }
}
