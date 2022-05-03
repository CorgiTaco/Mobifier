package corgitaco.mobifier.mixin;

import corgitaco.mobifier.Mobifier;
import corgitaco.mobifier.common.MobMobifier;
import corgitaco.mobifier.common.MobifierConfig;
import corgitaco.mobifier.common.util.DoubleModifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    public MixinLivingEntity(EntityType<?> p_i48580_1_, World p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }

    @Shadow
    protected abstract LootContext.Builder createLootContext(boolean p_213363_1_, DamageSource p_213363_2_);

    @Shadow
    public abstract AttributeModifierManager getAttributes();

    @Shadow
    public abstract boolean isDeadOrDying();

    @Inject(method = "dropExperience", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;addFreshEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void multiplyXPDrop(CallbackInfo ci, int xpReward, int xpOrbReward) {
        Map<EntityType<?>, List<MobMobifier>> mobifierForType = MobifierConfig.getConfig().getMobMobifierMap();
        final EntityType<?> entityType = this.getType();
        double totalValue = xpOrbReward;
        int mobifiersPassed = 0;
        if (mobifierForType.containsKey(entityType)) {
            for (MobMobifier mobMobifier : mobifierForType.get(entityType)) {
                if (mobMobifier.passes(this.level, (LivingEntity) (Object) this, this.isDeadOrDying(), mobifiersPassed)) {
                    totalValue = mobMobifier.getXpMultiplier().apply(totalValue);
                    mobifiersPassed++;
                }
            }
        }
        this.level.addFreshEntity(new ExperienceOrbEntity(this.level, this.getX(), this.getY(), this.getZ(), (int) (totalValue - xpOrbReward)));
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "getAttributeValue", at = @At("RETURN"), cancellable = true)
    private void getValue(Attribute attribute, CallbackInfoReturnable<Double> cir) {
        Map<EntityType<?>, List<MobMobifier>> mobifierForType = MobifierConfig.getConfig().getMobMobifierMap();
        final EntityType<?> entityType = this.getType();
        if (mobifierForType.containsKey(entityType)) {
            int mobifiersPassed = 0;
            for (MobMobifier mobMobifier : mobifierForType.get(entityType)) {
                if (mobMobifier.passes(this.level, (LivingEntity) (Object) this, this.isDeadOrDying(), mobifiersPassed)) {
                    if (this.getAttributes().hasAttribute(attribute)) {
                        final Map<Attribute, DoubleModifier> attributesMultipliers = mobMobifier.getAttributesMultipliers();
                        if (attributesMultipliers.containsKey(attribute)) {
                            cir.setReturnValue(attributesMultipliers.get(attribute).apply(cir.getReturnValueD()));
                        }
                    }
                    mobifiersPassed++;
                }
            }
        }
    }

    @Inject(method = "dropFromLootTable", at = @At(value = "HEAD"), cancellable = true)
    private void modifyLootTable(DamageSource damageSource, boolean bl, CallbackInfo ci) {
        Map<EntityType<?>, List<MobMobifier>> mobifierForType = MobifierConfig.getConfig().getMobMobifierMap();
        LootTableManager lootTables = this.level.getServer().getLootTables();
        if (lootTables == null) {
            return;
        }

        final EntityType<?> entityType = this.getType();
        if (mobifierForType.containsKey(entityType)) {
            int mobifiersPassed = 0;
            for (MobMobifier mobMobifier : mobifierForType.get(entityType)) {
                if (mobMobifier.passes(this.level, (LivingEntity) (Object) this, this.isDeadOrDying(), mobifiersPassed)) {
                    // TODO: Maybe move this out from here so we aren't cancelling it per mobifier?
                    if (!mobMobifier.isDropDefaultTable()) {
                        ci.cancel();
                    }

                    StringBuilder unknownTablesBuilder = new StringBuilder();
                    for (ResourceLocation lootTableLocation : mobMobifier.getDroppedTables()) {
                        if (lootTables.getIds().contains(lootTableLocation)) {
                            spawnItems(damageSource, bl, lootTableLocation, lootTables);
                        } else {
                            unknownTablesBuilder.append(lootTableLocation.toString()).append(", ");
                        }
                    }
                    final String unknownTables = unknownTablesBuilder.toString();
                    if (!unknownTables.isEmpty()) {
                        Mobifier.LOGGER.error(String.format("Found unknown loot table(s) for \"%s\": %s", Registry.ENTITY_TYPE.getKey(entityType).toString(), unknownTables));
                    }
                    mobifiersPassed++;
                }
            }
        }
    }

    private void spawnItems(DamageSource damageSource, boolean bl, ResourceLocation entityLootTableAdditions, LootTableManager lootTables) {
        LootTable loottable = lootTables.get(entityLootTableAdditions);
        LootContext.Builder lootcontext$builder = this.createLootContext(bl, damageSource);
        LootContext ctx = lootcontext$builder.create(LootParameterSets.ENTITY);
        loottable.getRandomItems(ctx).forEach(((LivingEntity) (Object) this)::spawnAtLocation);
    }
}