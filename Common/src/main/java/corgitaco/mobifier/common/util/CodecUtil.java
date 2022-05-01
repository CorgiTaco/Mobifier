package corgitaco.mobifier.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

import java.util.Arrays;

@SuppressWarnings("deprecation")
public class CodecUtil {

    public static final Codec<EntityType<?>> ENTITY_TYPE_CODEC = createLoggedExceptionRegistryCodec(Registry.ENTITY_TYPE);
    public static final Codec<Attribute> ATTRIBUTE_CODEC = createLoggedExceptionRegistryCodec(Registry.ATTRIBUTE);
    public static final Codec<Item> ITEM_CODEC = createLoggedExceptionRegistryCodec(Registry.ITEM);
    public static final Codec<Block> BLOCK_CODEC = createLoggedExceptionRegistryCodec(Registry.BLOCK);
    public static final Codec<StructureFeature<?>> STRUCTURE_CODEC = createLoggedExceptionRegistryCodec(Registry.STRUCTURE_FEATURE);
    public static final Codec<Enchantment> ENCHANTMENT_CODEC = createLoggedExceptionRegistryCodec(Registry.ENCHANTMENT);

    public static final Codec<ResourceKey<Biome>> BIOME_CODEC = ResourceLocation.CODEC.comapFlatMap(resourceLocation -> DataResult.success(ResourceKey.create(Registry.BIOME_REGISTRY, resourceLocation)), ResourceKey::location);

    public static final Codec<EquipmentSlot> EQUIPMENT_SLOT_CODEC = Codec.STRING.comapFlatMap(s -> {
        final EquipmentSlot equipmentSlotType = EquipmentSlot.byName(s.toLowerCase());
        if (equipmentSlotType == null) {
            throw new IllegalArgumentException(String.format("\"%s\" is not a valid equipmentSlotType. Valid equipmentSlotTypes: %s", s, Arrays.toString(Arrays.stream(EquipmentSlot.values()).map(EquipmentSlot::getName).toArray())));
        }
        return DataResult.success(equipmentSlotType);

    }, EquipmentSlot::getName);
    public static final Codec<Difficulty> DIFFICULTY_CODEC = Codec.STRING.comapFlatMap(s -> {
        final Difficulty difficulty = Difficulty.byName(s.toLowerCase());
        if (difficulty == null) {
            throw new IllegalArgumentException(String.format("\"%s\" is not a valid difficulty. Valid difficulties: %s", s, Arrays.toString(Arrays.stream(Difficulty.values()).map(Difficulty::getKey).toArray())));
        }
        return DataResult.success(difficulty);

    }, Difficulty::getKey);

    public static <T> Codec<T> createLoggedExceptionRegistryCodec(Registry<T> registry) {
        return ResourceLocation.CODEC.comapFlatMap(location -> {
            final T result = registry.get(location);

            if (result == null) {
                StringBuilder registryElements = new StringBuilder();
                for (int i = 0; i < registry.entrySet().size(); i++) {
                    final T object = registry.byId(i);
                    registryElements.append(i).append(". \"").append(registry.getKey(object).toString()).append("\"\n");
                }

                throw new IllegalArgumentException(String.format("\"%s\" is not a valid id in registry: %s.\n Current Registry Values:\n%s", location.toString(), registry.toString(), registryElements.toString()));
            }
            return DataResult.success(result);
        }, registry::getKey);
    }
}
