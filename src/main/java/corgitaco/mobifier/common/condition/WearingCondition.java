package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.common.util.ItemStackCheck;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class WearingCondition implements Condition {
    public static final Codec<WearingCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(ItemStackCheck.CODEC.listOf().fieldOf("is_wearing").forGetter(wearingCondition -> wearingCondition.stackChecks)).apply(builder, WearingCondition::new);
    });

    private final List<ItemStackCheck> stackChecks;
    private final Map<Item, ItemStackCheck> itemItemStackCheckMap;

    public WearingCondition(List<ItemStackCheck> stackChecks) {
        this.stackChecks = stackChecks;
        this.itemItemStackCheckMap = new Object2ObjectOpenHashMap<>();
        for (ItemStackCheck stackCheck : stackChecks) {
            final Item item = stackCheck.getItem();
            if (itemItemStackCheckMap.containsKey(item)) {
                throw new UnsupportedOperationException("Found another check for an already existing item.");
            } else {
                itemItemStackCheckMap.put(item.getItem(), stackCheck);
            }
        }
    }

    @Override
    public boolean passes(World world,LivingEntity entity, boolean isDeadOrDying) {
        for (ItemStack armorSlot : entity.getArmorSlots()) {
            final Item mapKey = armorSlot.getItem();
            if (itemItemStackCheckMap.containsKey(mapKey)) {
                if (!itemItemStackCheckMap.get(mapKey).test(armorSlot)) {
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
