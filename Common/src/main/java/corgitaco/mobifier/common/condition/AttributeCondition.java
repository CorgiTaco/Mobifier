package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.common.util.CodecUtil;
import corgitaco.mobifier.common.util.comparator.DoubleComparator;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Set;

public class AttributeCondition implements Condition {

    public static final Codec<AttributeCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Codec.unboundedMap(CodecUtil.ATTRIBUTE_CODEC, DoubleComparator.CODEC).fieldOf("attribute_is").forGetter(attributeCondition -> attributeCondition.attributeComparator)
        ).apply(builder, AttributeCondition::new);
    });
    private final Map<Attribute, DoubleComparator> attributeComparator;
    private final Set<Map.Entry<Attribute, DoubleComparator>> entries;

    public AttributeCondition(Map<Attribute, DoubleComparator> attributeComparator) {
        this.attributeComparator = attributeComparator;
        this.entries = attributeComparator.entrySet();
    }

    @Override
    public boolean passes(Level world, LivingEntity entity, boolean isDeadOrDying, int mobifiersPassed) {
        if (attributeComparator.isEmpty()) {
            return false;
        }
        for (Map.Entry<Attribute, DoubleComparator> entry : entries) {
            AttributeInstance attribute = entity.getAttribute(entry.getKey());
            if (attribute == null) {
                return false;
            }

            if (!entry.getValue().check(attribute.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}
