package corgitaco.mobifier.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;

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

    public static final Codec<Difficulty> DIFFICULTY_CODEC = Codec.STRING.comapFlatMap(s -> DataResult.success(Difficulty.byName(s.toUpperCase())), Enum::name);
    public static final Codec<RegistryKey<Biome>> BIOME_CODEC = ResourceLocation.CODEC.comapFlatMap(resourceLocation -> DataResult.success(RegistryKey.create(Registry.BIOME_REGISTRY, resourceLocation)), RegistryKey::location);
    public static final Codec<Item> ITEM_CODEC = ResourceLocation.CODEC.comapFlatMap(resourceLocation -> DataResult.success(Registry.ITEM.get(resourceLocation)), Registry.ITEM::getKey);
    public static final Codec<Block> BLOCK_CODEC = ResourceLocation.CODEC.comapFlatMap(resourceLocation -> DataResult.success(Registry.BLOCK.get(resourceLocation)), Registry.BLOCK::getKey);
    public static final Codec<Structure<?>> STRUCTURE_CODEC = ResourceLocation.CODEC.comapFlatMap(resourceLocation -> DataResult.success(Registry.STRUCTURE_FEATURE.get(resourceLocation)), Registry.STRUCTURE_FEATURE::getKey);
    public static final Codec<Enchantment> ENCHANTMENT_CODEC = ResourceLocation.CODEC.comapFlatMap(resourceLocation -> DataResult.success(Registry.ENCHANTMENT.get(resourceLocation)), Registry.ENCHANTMENT::getKey);
    public static final Codec<Hand> HAND_CODEC = Codec.STRING.comapFlatMap(s -> DataResult.success(Hand.valueOf(s.toUpperCase())), Hand::name);


}
