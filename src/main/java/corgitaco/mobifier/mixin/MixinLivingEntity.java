package corgitaco.mobifier.mixin;

import corgitaco.mobifier.AttributesModifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Shadow
    public abstract ResourceLocation getLootTable();

    @Shadow
    protected abstract LootContext.Builder createLootContext(boolean p_213363_1_, DamageSource p_213363_2_);

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "getAttributeValue", at = @At("RETURN"), cancellable = true)
    private void getValue(Attribute attribute, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(AttributesModifier.modifyAttribute(((LivingEntity) (Object) this), attribute, ((LivingEntity) (Object) this).level, cir.getReturnValueD()));
    }

    @Inject(method = "dropFromLootTable", at = @At(value = "RETURN"))
    private void modifyLootTable(DamageSource damageSource, boolean bl, CallbackInfo ci) {
        ResourceLocation entityLootTableAdditions = new ResourceLocation(this.getLootTable().getNamespace(), this.getLootTable().getPath() + "_" + ((LivingEntity) (Object) this).level.getDifficulty().name().toLowerCase());
        ResourceLocation classificationLootTable = new ResourceLocation(this.getLootTable().getNamespace(), "entities/" + ((LivingEntity) (Object) this).getType().getCategory().toString().toLowerCase() + "_" + ((LivingEntity) (Object) this).level.getDifficulty().name().toLowerCase());
        LootTableManager lootTables = ((LivingEntity) (Object) this).level.getServer().getLootTables();

        if (lootTables.getIds().contains(entityLootTableAdditions)) {
            spawnItems(damageSource, bl, entityLootTableAdditions, lootTables);
        } else if (lootTables.getIds().contains(classificationLootTable)) {
            spawnItems(damageSource, bl, classificationLootTable, lootTables);
        }
    }

    private void spawnItems(DamageSource damageSource, boolean bl, ResourceLocation entityLootTableAdditions, LootTableManager lootTables) {
        LootTable loottable = lootTables.get(entityLootTableAdditions);
        LootContext.Builder lootcontext$builder = this.createLootContext(bl, damageSource);
        LootContext ctx = lootcontext$builder.create(LootParameterSets.ENTITY);
        loottable.getRandomItems(ctx).forEach(((LivingEntity) (Object) this)::spawnAtLocation);
    }
}
