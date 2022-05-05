package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.common.util.CodecUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.world.World;

import java.util.List;

public class HasEffectCondition implements Condition {

    public static final Codec<HasEffectCondition> CODEC = RecordCodecBuilder.create(builder ->
        builder.group(
            CodecUtil.EFFECT_CODEC.listOf().fieldOf("effects").forGetter(hasEffectCondition -> hasEffectCondition.effects),
            Codec.BOOL.fieldOf("has_any").forGetter(hasEffectCondition -> hasEffectCondition.hasAny)
        ).apply(builder, HasEffectCondition::new));

    private final List<Effect> effects;
    private final boolean hasAny;

    public HasEffectCondition(List<Effect> effects, boolean hasAny) {
        this.effects = effects;
        if (effects.isEmpty()) {
            throw new IllegalArgumentException("Effects condition requires at least 1 effect to check against.");
        }
        this.hasAny = hasAny;
    }

    @Override
    public boolean passes(World world, LivingEntity entity, boolean isDeadOrDying, int mobifiersPassed) {
        for (Effect effect : this.effects) {
            if (hasAny) {
                return entity.hasEffect(effect);
            } else {
                if (!entity.hasEffect(effect)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}