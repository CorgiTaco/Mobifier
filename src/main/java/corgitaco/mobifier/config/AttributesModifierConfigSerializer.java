package corgitaco.mobifier.config;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.AttributesModifier;
import corgitaco.mobifier.CodecUtil;
import corgitaco.mobifier.Mobifier;
import it.unimi.dsi.fastutil.objects.Reference2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@SuppressWarnings("deprecation")
public class AttributesModifierConfigSerializer implements JsonDeserializer<AttributesModifierConfigSerializer.Holder> {

    @Override
    public AttributesModifierConfigSerializer.Holder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Set<Map.Entry<String, JsonElement>> entityEntries = json.getAsJsonObject().entrySet();
        Reference2ReferenceArrayMap<EntityType<?>, EnumMap<Difficulty, Reference2DoubleArrayMap<Attribute>>> output = new Reference2ReferenceArrayMap<>();

        Map<EntityClassification, List<EntityType<?>>> mobCategoryEntityTypes = new EnumMap<>(EntityClassification.class);

        for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
            mobCategoryEntityTypes.computeIfAbsent(entityType.getCategory(), (mobCategory -> new ArrayList<>())).add(entityType);
        }


        for (Map.Entry<String, JsonElement> entry : entityEntries) {
            String key = entry.getKey();
            if (key.startsWith("category/")) {
                String mobCategory = key.substring("category/".length()).toUpperCase();

                EntityClassification[] values = EntityClassification.values();
                if (Arrays.stream(values).noneMatch(difficulty -> difficulty.toString().equals(mobCategory))) {
                    Mobifier.LOGGER.error("\"" + mobCategory + "\" is not a valid mob category value. mob category entry...\nValid Mob Categories: " + Arrays.toString(values));
                    continue;
                }

                for (EntityType<?> entityType : mobCategoryEntityTypes.get(EntityClassification.valueOf(mobCategory))) {
                    processDifficulties(output, entry, Registry.ENTITY_TYPE.getKey(entityType));
                }
                continue;
            }

            ResourceLocation entityTypeID = tryParse(key.toLowerCase());
            if (entityTypeID != null && !Registry.ENTITY_TYPE.keySet().contains(entityTypeID)) {
                Mobifier.LOGGER.error("\"" + key + "\" is not a valid entity ID. Skipping entry...");
                continue;
            }

            processDifficulties(output, entry, entityTypeID);
        }

        return new Holder(output);
    }

    private void processDifficulties(Reference2ReferenceArrayMap<EntityType<?>, EnumMap<Difficulty, Reference2DoubleArrayMap<Attribute>>> output, Map.Entry<String, JsonElement> entry, ResourceLocation entityTypeID) {
        EnumMap<Difficulty, Reference2DoubleArrayMap<Attribute>> difficultyMap = new EnumMap<>(Difficulty.class);

        Set<Map.Entry<String, JsonElement>> difficultyEntries = entry.getValue().getAsJsonObject().entrySet();
        for (Map.Entry<String, JsonElement> difficultyEntry : difficultyEntries) {
            String difficultyName = difficultyEntry.getKey().toUpperCase();

            Difficulty[] difficulties = Difficulty.values();
            if (Arrays.stream(difficulties).noneMatch(difficulty -> difficulty.toString().equals(difficultyName))) {
                Mobifier.LOGGER.error("\"" + difficultyName + "\" is not a valid difficulty value. Skipping difficulty entry...\nValid Difficulties: " + Arrays.toString(difficulties));
                continue;
            }

            Reference2DoubleArrayMap<Attribute> attributesMap = new Reference2DoubleArrayMap<>();
            Set<Map.Entry<String, JsonElement>> attributeEntries = difficultyEntry.getValue().getAsJsonObject().entrySet();

            for (Map.Entry<String, JsonElement> attributeEntry : attributeEntries) {
                String attributeKey = attributeEntry.getKey().toLowerCase();
                ResourceLocation attributeID = tryParse(attributeKey);

                if (attributeID != null && !Registry.ATTRIBUTE.keySet().contains(attributeID)) {
                    Mobifier.LOGGER.error("\"" + attributeKey + "\" is not a valid attribute ID. Skipping attribute entry...");
                    continue;
                }
                attributesMap.put(Registry.ATTRIBUTE.get(attributeID), attributeEntry.getValue().getAsNumber().doubleValue());
            }
            difficultyMap.put(Difficulty.valueOf(difficultyName), attributesMap);
        }
        output.put(Registry.ENTITY_TYPE.get(entityTypeID), difficultyMap);
    }

    @Nullable
    public static ResourceLocation tryParse(String id) {
        try {
            return new ResourceLocation(id);
        } catch (ResourceLocationException resourcelocationexception) {
            Mobifier.LOGGER.error(resourcelocationexception.getMessage());
            return null;
        }
    }

    public static <T extends Map<?, ?>> void handleConfig(Path path, T defaults) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Holder.class, new AttributesModifierConfigSerializer()).setPrettyPrinting().disableHtmlEscaping().create();

        final File CONFIG_FILE = new File(String.valueOf(path));

        if (!CONFIG_FILE.exists()) {
            createJson(path, defaults);
        }
        try (Reader reader = new FileReader(path.toString())) {
            Holder holder = gson.fromJson(reader, Holder.class);
            if (holder != null) {
                AttributesModifier.ENTITY_DIFFICULTY_ATTRIBUTES_MODIFIERS.clear();
                AttributesModifier.ENTITY_DIFFICULTY_ATTRIBUTES_MODIFIERS.putAll(holder.getMap());
            } else {
                Mobifier.LOGGER.error("\"" + path.toString() + "\" failed to read.");
            }

        } catch (IOException e) {
            Mobifier.LOGGER.error("\"" + path.toString() + "\" failed.\n" + e.toString());
        }
    }

    public static <T extends Map<?, ?>> void createJson(Path path, T map) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        String jsonString = gson.toJson(map);

        try {
            Files.createDirectories(path.getParent());
            Files.write(path, jsonString.getBytes());
        } catch (IOException e) {
        }
    }

    public static class Holder {

        public static final Codec<Holder> CODEC = RecordCodecBuilder.create((builder) -> {
           return builder.group(Codec.unboundedMap(CodecUtil.ENTITY_TYPE_CODEC, Codec.unboundedMap(Codec.STRING, Codec.unboundedMap(CodecUtil.ATTRIBUTE_CODEC, Codec.DOUBLE))).fieldOf("mobifier").forGetter((holder) -> {
               Map<EntityType<?>, Map<String, Map<Attribute, Double>>> serializableMap = new HashMap<>();
               holder.map.forEach(((entityType, difficultyReference2DoubleArrayMapEnumMap) -> {
                   Map<String, Map<Attribute, Double>> serializableMap1 = new HashMap<>();

                   difficultyReference2DoubleArrayMapEnumMap.forEach((difficulty, attributeReference2DoubleArrayMap) -> {
                       serializableMap1.put(difficulty.name(), attributeReference2DoubleArrayMap);
                   });
                   serializableMap.put(entityType, serializableMap1);
               }));
               return serializableMap;
           })).apply(builder, Holder::new);
        });

        private final Reference2ReferenceArrayMap<EntityType<?>, EnumMap<Difficulty, Reference2DoubleArrayMap<Attribute>>> map = new Reference2ReferenceArrayMap<>();

        public Holder(Map<EntityType<?>, Map<String, Map<Attribute, Double>>> map) {
            map.forEach(((entityType, difficultyMapMap) -> {
                EnumMap<Difficulty, Reference2DoubleArrayMap<Attribute>> difficultyMap = new EnumMap<>(Difficulty.class);

                difficultyMapMap.forEach((difficulty, attributeDoubleMap) -> {
                    Difficulty difficulty1 = Difficulty.valueOf(difficulty);
                    Reference2DoubleArrayMap<Attribute> attributeDoubleMap1 = new Reference2DoubleArrayMap<>();
                    attributeDoubleMap.forEach(((attribute, aDouble) -> {
                        attributeDoubleMap1.put(attribute, aDouble.doubleValue());
                    }));
                    difficultyMap.put(difficulty1, attributeDoubleMap1);
                });

                this.map.put(entityType, difficultyMap);
            }));
        }


        public Holder(Reference2ReferenceArrayMap<EntityType<?>, EnumMap<Difficulty, Reference2DoubleArrayMap<Attribute>>> map) {
            this.map.putAll(map);
        }

        public Reference2ReferenceArrayMap<EntityType<?>, EnumMap<Difficulty, Reference2DoubleArrayMap<Attribute>>> getMap() {
            return map;
        }
    }
}
