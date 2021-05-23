package corgitaco.mobifier;

import it.unimi.dsi.fastutil.objects.Reference2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.util.EnumMap;

@SuppressWarnings("unchecked")
public class AttributesModifier {

    public static final Reference2ReferenceArrayMap<EntityType<?>, EnumMap<Difficulty, Reference2DoubleArrayMap<Attribute>>> ENTITY_DIFFICULTY_ATTRIBUTES_MODIFIERS = new Reference2ReferenceArrayMap<>();

    public static double modifyAttribute(LivingEntity entity, Attribute attribute, World level, double originalVal) {
        if (ENTITY_DIFFICULTY_ATTRIBUTES_MODIFIERS.containsKey(entity.getType())) {
            Reference2DoubleArrayMap<Attribute> difficultyAttributes = ENTITY_DIFFICULTY_ATTRIBUTES_MODIFIERS.get(entity.getType()).get(level.getDifficulty());
            if (difficultyAttributes.containsKey(attribute)) {
                if (entity.getAttributes().hasAttribute(attribute)) {
                    return originalVal * difficultyAttributes.getDouble(attribute);
                }
            }
        }
        return originalVal;
    }
}