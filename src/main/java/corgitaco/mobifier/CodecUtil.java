package corgitaco.mobifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class CodecUtil {

    public static final Codec<EntityType<?>> ENTITY_TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(location -> {
        try {
            return DataResult.success(Registry.ENTITY_TYPE.getOptional(location).orElseThrow(RuntimeException::new));
        } catch (Exception e) {
            return DataResult.error(e.getMessage());
        }
    }, Registry.ENTITY_TYPE::getKey);


    public static final Codec<Attribute> ATTRIBUTE_CODEC = ResourceLocation.CODEC.comapFlatMap(location -> {
        try {
            return DataResult.success(Registry.ATTRIBUTE.getOptional(location).orElseThrow(RuntimeException::new));
        } catch (Exception e) {
            return DataResult.error(e.getMessage());
        }
    }, Registry.ATTRIBUTE::getKey);
}
