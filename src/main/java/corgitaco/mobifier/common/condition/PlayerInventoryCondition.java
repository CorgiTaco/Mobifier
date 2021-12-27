package corgitaco.mobifier.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.common.util.ItemStackCheck;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Map;

public class PlayerInventoryCondition implements Condition {

    public static final Codec<PlayerInventoryCondition> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(ItemStackCheck.CODEC.listOf().fieldOf("has").forGetter(wearingCondition -> wearingCondition.stackChecks)).apply(builder, PlayerInventoryCondition::new);
    });

    private final List<ItemStackCheck> stackChecks;
    private final Map<Item, ItemStackCheck> itemItemStackCheckMap;

    public PlayerInventoryCondition(List<ItemStackCheck> stackChecks) {
        if (stackChecks.isEmpty()) {
            throw new IllegalArgumentException("No item stack checks were specified.");
        }
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
    public boolean passes(ServerWorld world, LivingEntity entity, boolean isDeadOrDying) {
        if (entity instanceof PlayerEntity) {
            for (ItemStack item : ((PlayerEntity) entity).inventory.items) {
                if (itemItemStackCheckMap.containsKey(item.getItem())) {
                    ItemStackCheck itemStackCheck = itemItemStackCheckMap.get(item.getItem());
                    if (!itemStackCheck.test(item)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Codec<? extends Condition> codec() {
        return CODEC;
    }
}
