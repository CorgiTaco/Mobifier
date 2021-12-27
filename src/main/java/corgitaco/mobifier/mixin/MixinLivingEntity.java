package corgitaco.mobifier.mixin;

import corgitaco.mobifier.common.MobMobifier;
import corgitaco.mobifier.Mobifier;
import corgitaco.mobifier.common.MobifierConfig;
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
import net.minecraft.world.server.ServerWorld;
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
        if (mobifierForType.containsKey(entityType)) {
            for (MobMobifier mobMobifier : mobifierForType.get(entityType)) {
                if (mobMobifier.passes((ServerWorld) this.level, (LivingEntity) (Object) this, this.isDeadOrDying())) {
                    totalValue *= mobMobifier.getXpMultiplier();
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
            for (MobMobifier mobMobifier : mobifierForType.get(entityType)) {
                if (mobMobifier.passes((ServerWorld) this.level, (LivingEntity) (Object) this, this.isDeadOrDying())) {
                    if (this.getAttributes().hasAttribute(attribute)) {
                        cir.setReturnValue(cir.getReturnValueD() * mobMobifier.getAttributesMultipliers().getOrDefault(attribute, 1.0D));
                    }
                }
            }
        }
    }

    @Inject(method = "dropFromLootTable", at = @At(value = "RETURN"))
    private void modifyLootTable(DamageSource damageSource, boolean bl, CallbackInfo ci) {
        Map<EntityType<?>, List<MobMobifier>> mobifierForType = MobifierConfig.getConfig().getMobMobifierMap();
        LootTableManager lootTables = this.level.getServer().getLootTables();
        if (lootTables == null) {
            return;
        }

        final EntityType<?> entityType = this.getType();
        if (mobifierForType.containsKey(entityType)) {
            for (MobMobifier mobMobifier : mobifierForType.get(entityType)) {
                if (mobMobifier.passes((ServerWorld) this.level, (LivingEntity) (Object) this, this.isDeadOrDying())) {
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
