package corgitaco.mobifier.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.mobifier.CodecUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.Optional;

public class ItemStackCheck {

    public static final Codec<ItemStackCheck> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(CodecUtil.ITEM_CODEC.fieldOf("item").forGetter(itemStackCheck -> itemStackCheck.item), DoubleComparator.CODEC.optionalFieldOf("durability_is").forGetter(itemStackCheck -> itemStackCheck.durabilityComparator),
                        DoubleComparator.CODEC.optionalFieldOf("stacksize_is").forGetter(itemStackCheck -> itemStackCheck.stackSizeComparator),
                        Codec.unboundedMap(CodecUtil.ENCHANTMENT_CODEC, DoubleComparator.CODEC).optionalFieldOf("enchantmentChecks").forGetter(itemStackCheck -> itemStackCheck.enchantmentLevelComparator))
                .apply(builder, ItemStackCheck::new);
    });

    private final Item item;
    private final Optional<DoubleComparator> durabilityComparator;
    private final Optional<DoubleComparator> stackSizeComparator;
    private final Optional<Map<Enchantment, DoubleComparator>> enchantmentLevelComparator;

    public ItemStackCheck(Item item, Optional<DoubleComparator> durabilityComparator, Optional<DoubleComparator> stackSizeComparator, Optional<Map<Enchantment, DoubleComparator>> enchantmentLevelComparator) {
        this.item = item;
        this.durabilityComparator = durabilityComparator;
        this.stackSizeComparator = stackSizeComparator;
        this.enchantmentLevelComparator = enchantmentLevelComparator;
    }

    public boolean test(ItemStack itemStack) {
        if (item == itemStack.getItem()) {
            if (durabilityComparator.isPresent() && !durabilityComparator.get().check(itemStack.getDamageValue())) {
                return false;
            }
            if (stackSizeComparator.isPresent() && !stackSizeComparator.get().check(itemStack.getCount())) {
                return false;
            }

            if (enchantmentLevelComparator.isPresent()) {
                Map<Enchantment, DoubleComparator> enchantmentComparator = enchantmentLevelComparator.get();
                for (Map.Entry<Enchantment, Integer> enchantmentIntegerEntry : EnchantmentHelper.getEnchantments(itemStack).entrySet()) {
                    if (enchantmentComparator.containsKey(enchantmentIntegerEntry.getKey())) {
                        final DoubleComparator doubleComparator = enchantmentComparator.get(enchantmentIntegerEntry.getKey());
                        if (!doubleComparator.check(enchantmentIntegerEntry.getValue())) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public Item getItem() {
        return item;
    }
}