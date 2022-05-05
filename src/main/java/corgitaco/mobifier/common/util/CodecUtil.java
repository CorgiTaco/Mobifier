package corgitaco.mobifier.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.Arrays;

@SuppressWarnings("deprecation")
public class CodecUtil {

    public static final Codec<EntityType<?>> ENTITY_TYPE_CODEC = createLoggedExceptionRegistryCodec(Registry.ENTITY_TYPE);
    public static final Codec<Attribute> ATTRIBUTE_CODEC = createLoggedExceptionRegistryCodec(Registry.ATTRIBUTE);
    public static final Codec<Item> ITEM_CODEC = createLoggedExceptionRegistryCodec(Registry.ITEM);
    public static final Codec<Block> BLOCK_CODEC = createLoggedExceptionRegistryCodec(Registry.BLOCK);
    public static final Codec<Structure<?>> STRUCTURE_CODEC = createLoggedExceptionRegistryCodec(Registry.STRUCTURE_FEATURE);
    public static final Codec<Enchantment> ENCHANTMENT_CODEC = createLoggedExceptionRegistryCodec(Registry.ENCHANTMENT);
    public static final Codec<Effect> EFFECT_CODEC = createLoggedExceptionRegistryCodec(Registry.MOB_EFFECT);

    public static final Codec<RegistryKey<Biome>> BIOME_CODEC = ResourceLocation.CODEC.comapFlatMap(resourceLocation -> DataResult.success(RegistryKey.create(Registry.BIOME_REGISTRY, resourceLocation)), RegistryKey::location);

    public static final Codec<EquipmentSlotType> EQUIPMENT_SLOT_CODEC = Codec.STRING.comapFlatMap(s -> {
        final EquipmentSlotType equipmentSlotType = EquipmentSlotType.byName(s.toLowerCase());
        if (equipmentSlotType == null) {
            throw new IllegalArgumentException(String.format("\"%s\" is not a valid equipmentSlotType. Valid equipmentSlotTypes: %s", s, Arrays.toString(Arrays.stream(EquipmentSlotType.values()).map(EquipmentSlotType::getName).toArray())));
        }
        return DataResult.success(equipmentSlotType);

    }, EquipmentSlotType::getName);
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
