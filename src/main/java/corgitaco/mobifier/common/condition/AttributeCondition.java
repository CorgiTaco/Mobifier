package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.common.util.CodecUtil;
import corgitaco.mobifier.common.util.comparator.DoubleComparator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class AttributeCondition implements Condition {

    public static final Codec<AttributeCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Codec.unboundedMap(CodecUtil.ATTRIBUTE_CODEC, DoubleComparator.CODEC).fieldOf("attributeComparator").forGetter(attributeCondition -> attributeCondition.attributeComparator)
        ).apply(builder, AttributeCondition::new);
    });
    private final Map<Attribute, DoubleComparator> attributeComparator;
    private final Set<Map.Entry<Attribute, DoubleComparator>> entries;

    public AttributeCondition(Map<Attribute, DoubleComparator> attributeComparator) {
        this.attributeComparator = attributeComparator;
        this.entries = attributeComparator.entrySet();
    }

    @Override
    public boolean passes(World world, LivingEntity entity, boolean isDeadOrDying) {
        if (attributeComparator.isEmpty()) {
            return false;
        }
        for (Map.Entry<Attribute, DoubleComparator> entry : entries) {
            @Nullable
            ModifiableAttributeInstance attribute = entity.getAttribute(entry.getKey());
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
