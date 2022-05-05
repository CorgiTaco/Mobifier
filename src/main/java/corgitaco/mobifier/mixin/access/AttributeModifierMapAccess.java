package corgitaco.mobifier.mixin.access;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AttributeModifierMap.class)
public interface AttributeModifierMapAccess {

    @Accessor("instances")
    Map<Attribute, ModifiableAttributeInstance> mobifier_getInstances();


}
