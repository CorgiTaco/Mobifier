package corgitaco.mobifier.mixin;

import corgitaco.mobifier.Mobifier;
import corgitaco.mobifier.common.MobMobifier;
import corgitaco.mobifier.common.MobifierConfig;
import corgitaco.mobifier.common.condition.ConditionContext;
import corgitaco.mobifier.common.util.DoubleModifier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    public MixinLivingEntity(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }

    @Shadow
    protected abstract LootContext.Builder createLootContext(boolean p_213363_1_, DamageSource p_213363_2_);

    @Shadow
    public abstract AttributeMap getAttributes();

    @Shadow
    public abstract boolean isDeadOrDying();

    @Shadow
    public abstract int getExperienceReward();


    @Inject(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
    private void multiplyXPDrop(CallbackInfo ci) {
        Map<EntityType<?>, List<MobMobifier>> mobifierForType = MobifierConfig.getConfig().getMobMobifierMap();
        final EntityType<?> entityType = this.getType();
        int xpOrbReward = this.getExperienceReward();
        double totalValue = xpOrbReward;
        int mobifiersPassed = 0;
        if (mobifierForType.containsKey(entityType)) {
            for (MobMobifier mobMobifier : mobifierForType.get(entityType)) {
                if (mobMobifier.passes(new ConditionContext(this.level, (LivingEntity) (Object) this, this.isDeadOrDying(), mobifiersPassed))) {
                    totalValue = mobMobifier.getXpMultiplier().apply(totalValue);
                    mobifiersPassed++;
                }
            }
        }
        ExperienceOrb.award((ServerLevel) this.level, this.position(), (int) (totalValue - xpOrbReward));
    }

    @Inject(method = "getAttributeValue", at = @At("RETURN"), cancellable = true)
    private void getValue(Attribute attribute, CallbackInfoReturnable<Double> cir) {
        Map<EntityType<?>, List<MobMobifier>> mobifierForType = MobifierConfig.getConfig().getMobMobifierMap();
        final EntityType<?> entityType = this.getType();
        if (mobifierForType.containsKey(entityType)) {
            int mobifiersPassed = 0;
            for (MobMobifier mobMobifier : mobifierForType.get(entityType)) {
                if (mobMobifier.passes(new ConditionContext(this.level, (LivingEntity) (Object) this, this.isDeadOrDying(), mobifiersPassed))) {
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
        LootTables lootTables = this.level.getServer().getLootTables();
        if (lootTables == null) {
            return;
        }

        final EntityType<?> entityType = this.getType();
        if (mobifierForType.containsKey(entityType)) {
            int mobifiersPassed = 0;
            for (MobMobifier mobMobifier : mobifierForType.get(entityType)) {
                if (mobMobifier.passes(new ConditionContext(this.level, (LivingEntity) (Object) this, this.isDeadOrDying(), mobifiersPassed))) {
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
                        Mobifier.LOGGER.error(String.format("Found unknown loot table(s) for \"%s\": %s", Registry.ENTITY_TYPE.getKey(entityType), unknownTables));
                    }
                    mobifiersPassed++;
                }
            }
        }
    }

    private void spawnItems(DamageSource damageSource, boolean bl, ResourceLocation entityLootTableAdditions, LootTables lootTables) {
        LootTable loottable = lootTables.get(entityLootTableAdditions);
        LootContext.Builder lootcontext$builder = this.createLootContext(bl, damageSource);
        LootContext ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
        loottable.getRandomItems(ctx).forEach(((LivingEntity) (Object) this)::spawnAtLocation);
    }
}