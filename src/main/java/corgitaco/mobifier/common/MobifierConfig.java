package corgitaco.mobifier.common;

import com.google.common.collect.ImmutableList;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.Mobifier;
import corgitaco.mobifier.common.condition.BiomeCategoryCondition;
import corgitaco.mobifier.common.condition.InDimensionCondition;
import corgitaco.mobifier.common.util.DoubleModifier;
import corgitaco.mobifier.common.util.MobifierUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobifierConfig {

    public static MobifierConfig INSTANCE = null;

    public static final MobifierConfig DEFAULT = new MobifierConfig(true, Util.make(new Object2ObjectOpenHashMap<>(), map -> {
        map.put(EntityType.HUSK, Util.make(new ArrayList<>(), list -> {
            list.add(new MobMobifier(new DoubleModifier("*2"), Util.make(new Object2ObjectOpenHashMap<>(), map1 -> {
                for (Attribute attribute : Registry.ATTRIBUTE) {
                    map1.put(attribute, new DoubleModifier("*2"));
                }

            }), true, new ArrayList<>(), Util.make(new ArrayList<>(), (list1) -> {
                list1.add(new BiomeCategoryCondition(ImmutableList.of(Biome.Category.DESERT)));
                list1.add(new InDimensionCondition(ImmutableList.of(World.OVERWORLD)));
            })));
        }));
        Registry.ENTITY_TYPE.forEach(entityType -> {
            if (entityType != EntityType.HUSK) {
                map.put(entityType, Util.make(new ArrayList<>(), list -> {
                    list.add(new MobMobifier(new DoubleModifier("*1"), Util.make(new Object2ObjectOpenHashMap<>(), map1 -> {
                        for (Attribute attribute : Registry.ATTRIBUTE) {
                            map1.put(attribute, new DoubleModifier("*1"));
                        }

                    }), true, new ArrayList<>(), new ArrayList<>()));
                }));
            }
        });
    }));

    public static MobifierConfig getConfig() {
        return getConfig(false);
    }

    public static MobifierConfig getConfig(boolean serialize) {
        if (INSTANCE == null || serialize) {
            INSTANCE = readConfig();
        }

        return INSTANCE;
    }

    public static void setConfigInstance(MobifierConfig config) {
        INSTANCE = config;
    }

    private static MobifierConfig readConfig() {
        final Path path = Mobifier.CONFIG_PATH.resolve(Mobifier.MOD_ID + ".json");

        if (!path.toFile().exists()) {
            JsonElement jsonElement = CODEC.encodeStart(JsonOps.INSTANCE, DEFAULT).result().get();

            try {
                Files.createDirectories(path.getParent());
                Files.write(path, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(jsonElement).getBytes());
            } catch (IOException e) {
                Mobifier.LOGGER.error(e.toString());
            }
        }
        Mobifier.LOGGER.info(String.format("\"%s\" was read.", path.toString()));

        try {
            return CODEC.decode(JsonOps.INSTANCE, new JsonParser().parse(new FileReader(path.toFile()))).result().orElseThrow(RuntimeException::new).getFirst();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return DEFAULT;
    }

    public static final Codec<Map<EntityType<?>, List<MobMobifier>>> CATEGORY_OR_ENTITY_TYPE_MAP_CODEC = Codec.unboundedMap(Codec.STRING, MobMobifier.CODEC.listOf()).comapFlatMap(s -> {
        Map<EntityType<?>, List<MobMobifier>> result = new Object2ObjectOpenHashMap<>();
        s.forEach((key, value) -> {
            final String filter = "category/";
            if (key.toLowerCase().startsWith(filter)) {
                for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
                    if (entityType.getCategory() == MobifierUtil.tryParseMobCategory(key.substring(filter.length()))) {
                        result.computeIfAbsent(entityType, (type -> new ArrayList<>())).addAll(value);
                    }
                }
            } else {
                result.computeIfAbsent(Registry.ENTITY_TYPE.get(new ResourceLocation(key)), (type) -> new ArrayList<>()).addAll(value);
            }
        });
        return DataResult.success(result);
    }, entityTypeListMap -> {
        Map<String, List<MobMobifier>> result = new HashMap<>();
        entityTypeListMap.forEach((type, mobMobifiers) -> {
            result.put(Registry.ENTITY_TYPE.getKey(type).toString(), mobMobifiers);
        });
        return result;
    });

    public static final Codec<MobifierConfig> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Codec.BOOL.optionalFieldOf("dump_registries", false).forGetter(mobifierConfig -> mobifierConfig.dumpRegistries),
            CATEGORY_OR_ENTITY_TYPE_MAP_CODEC.fieldOf("mobifier").forGetter(mobifierConfig -> mobifierConfig.mobMobifierMap)
        ).apply(builder, MobifierConfig::new);
    });

    private final boolean dumpRegistries;
    private final Map<EntityType<?>, List<MobMobifier>> mobMobifierMap;

    public MobifierConfig(boolean dumpRegistries, Map<EntityType<?>, List<MobMobifier>> mobMobifierMap) {
        this.dumpRegistries = dumpRegistries;
        this.mobMobifierMap = mobMobifierMap;
    }

    public Map<EntityType<?>, List<MobMobifier>> getMobMobifierMap() {
        return mobMobifierMap;
    }

    public boolean isDumpRegistries() {
        return dumpRegistries;
    }
}