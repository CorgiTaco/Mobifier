package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.common.util.CodecUtil;
import corgitaco.mobifier.common.util.ItemStackCheck;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;

public class HasInHandCondition implements Condition {
    public static final Codec<HasInHandCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(Codec.unboundedMap(CodecUtil.HAND_CODEC, ItemStackCheck.CODEC).fieldOf("hand_check").forGetter(hasInHandCondition -> hasInHandCondition.handItemsChecksMap)).
                apply(builder, HasInHandCondition::new);
    });

    private final Map<Hand, ItemStackCheck> handItemsChecksMap;

    public HasInHandCondition(Map<Hand, ItemStackCheck> handItemsChecksMap) {
        this.handItemsChecksMap = handItemsChecksMap;
    }

    @Override
    public boolean passes(ServerWorld world, LivingEntity entity, boolean isDeadOrDying) {
        if (handItemsChecksMap.isEmpty()) {
            return false;
        }
        for (Hand hand : Hand.values()) {
            if (handItemsChecksMap.containsKey(hand)) {
                final ItemStack itemInHand = entity.getItemInHand(hand);
                if (!handItemsChecksMap.get(hand).test(itemInHand)) {
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
