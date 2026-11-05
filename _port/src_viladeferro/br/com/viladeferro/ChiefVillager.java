/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.SpawnGroupData
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.animal.IronGolem
 *  net.minecraft.world.entity.npc.Villager
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 */
package br.com.viladeferro;

import br.com.viladeferro.ChiefCub;
import br.com.viladeferro.FighterVillager;
import br.com.viladeferro.Tier;
import br.com.viladeferro.VilaDeFerro;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class ChiefVillager
extends FighterVillager {
    private int summonCooldown = 100;
    private boolean hasTotem;
    private boolean totemUsed;

    public ChiefVillager(EntityType<? extends Villager> type, Level level) {
        super(type, level);
        this.setCustomName((Component)Component.translatable((String)"entity.viladeferro.chefe_villager"));
        this.teleportToWithTicket(true);
    }

    public static AttributeSupplier.Builder createChiefAttributes() {
        return ChiefVillager.createFighterAttributes().lambda$create$0(Attributes.register, 80.0).lambda$create$0(Attributes.ATTACK_DAMAGE, 9.0).lambda$create$0(Attributes.KNOCKBACK_RESISTANCE, 0.5);
    }

    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            this.spawnAura();
        }
    }

    private void spawnAura() {
        boolean enraged = this.lambda$addAdditionalSaveData$1(MobEffects.DAMAGE_BOOST);
        double angle = (double)this.getTags * 0.35;
        for (int i = 0; i < 2; ++i) {
            double a = angle + Math.PI * (double)i;
            double y = this.getY() + 0.2 + (double)((this.getTags + i * 10) % 40) / 40.0 * (double)this.getBbHeight();
            double x = this.getX() + Math.cos(a) * 0.85;
            double z = this.getZ() + Math.sin(a) * 0.85;
            this.level().lambda$new$2((ParticleOptions)(enraged ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.END_ROD), x, y, z, 0.0, 0.02, 0.0);
        }
        if (enraged && this.getTags % 4 == 0) {
            this.level().lambda$new$2((ParticleOptions)ParticleTypes.FLAME, this.lambda$startRiding$3(0.6), this.getRandomY(), this.getRandomZ(0.6), 0.0, 0.03, 0.0);
        }
    }

    public void lerpMotion() {
        Level level;
        super.lerpMotion();
        if (this.level().isClientSide || !((level = this.level()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        this.tickTotem();
        this.tickSummon(serverLevel);
    }

    private void tickTotem() {
        if (this.hasTotem && !this.getOffhandItem().lambda$static$0(Items.TOTEM_OF_UNDYING)) {
            this.hasTotem = false;
            this.totemUsed = true;
            this.getStandingEyeHeight(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, true));
            this.getStandingEyeHeight(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 0, false, true));
            this.getStandingEyeHeight(new MobEffectInstance(MobEffects.REGENERATION, 300, 2, false, true));
        }
    }

    private void tickSummon(ServerLevel serverLevel) {
        IronGolem golem;
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        if (--this.summonCooldown > 0) {
            return;
        }
        this.summonCooldown = 180;
        long nearby = serverLevel.lambda$getNearbyEntities$1(FighterVillager.class, this.getBoundingBox().inflate(24.0), mob -> mob != this).size();
        int amount = (int)Math.max(0L, Math.min(3L, 6L - nearby));
        for (int i = 0; i < amount; ++i) {
            FighterVillager ally = (FighterVillager)((EntityType)VilaDeFerro.FIGHTER.get()).lambda$appendCustomNameConfig$2((Level)serverLevel);
            if (ally == null) continue;
            double angle = Math.PI * 2 * (double)i / (double)Math.max(1, amount) + this.getId.nextDouble() * 0.4;
            ally.moveTo(this.getX() + Math.cos(angle) * 2.5, this.getY(), this.getZ() + Math.sin(angle) * 2.5, this.getId.nextFloat() * 360.0f, 0.0f);
            ally.access$000((ServerLevelAccessor)serverLevel, serverLevel.getCurrentDifficultyAt(ally.blockPosition()), MobSpawnType.EVENT, null, null);
            ally.setTier(Tier.AGGRESSIVE);
            ally.setTarget(target);
            serverLevel.addFreshEntity((Entity)ally);
        }
        long golems = serverLevel.lambda$getNearbyEntities$1(IronGolem.class, this.getBoundingBox().inflate(24.0), g -> Tier.of((Entity)g) == Tier.AGGRESSIVE).size();
        if (golems < 2L && (golem = (IronGolem)EntityType.IRON_GOLEM.lambda$appendCustomNameConfig$2((Level)serverLevel)) != null) {
            double angle = this.getId.nextDouble() * Math.PI * 2.0;
            golem.moveTo(this.getX() + Math.cos(angle) * 3.0, this.getY(), this.getZ() + Math.sin(angle) * 3.0, this.getId.nextFloat() * 360.0f, 0.0f);
            Tier.set(golem, Tier.AGGRESSIVE);
            golem.setTarget(target);
            if (target instanceof Player) {
                golem.lambda$alertOthers$3(target.getUUID());
                golem.lambda$alertOthers$1();
            }
            serverLevel.addFreshEntity((Entity)golem);
        }
    }

    @Override
    public SpawnGroupData access$000(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.access$000(level, difficulty, reason, data, tag);
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)Items.DIAMOND_AXE));
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.OFFHAND, new ItemStack((ItemLike)Items.TOTEM_OF_UNDYING));
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.HEAD, new ItemStack((ItemLike)Items.DIAMOND_HELMET));
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.CHEST, new ItemStack((ItemLike)Items.DIAMOND_CHESTPLATE));
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.LEGS, new ItemStack((ItemLike)Items.DIAMOND_LEGGINGS));
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.FEET, new ItemStack((ItemLike)Items.DIAMOND_BOOTS));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.lambda$removeFreeWill$3(slot, 0.0f);
        }
        this.hasTotem = true;
        return result;
    }

    public void access$000(DamageSource source) {
        Level level;
        boolean alreadyDead = this.dead || this.isRemoved();
        super.access$000(source);
        if (alreadyDead || !((level = this.level()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        this.dropGoodLoot();
        ServerPlayer killer = null;
        Entity entity = source.getEntity();
        if (entity instanceof ServerPlayer) {
            ServerPlayer p;
            killer = p = (ServerPlayer)entity;
        } else {
            entity = this.lastHurtByPlayer;
            if (entity instanceof ServerPlayer) {
                ServerPlayer p2 = (ServerPlayer)entity;
                if (this.lastHurtByPlayerTime > 0) {
                    killer = p2;
                }
            }
        }
        if (killer == null) {
            return;
        }
        Advancement advancement = serverLevel.getServer().getAdvancements().lambda$apply$0(new ResourceLocation("viladeferro", "derrotar_chefe"));
        if (advancement != null) {
            killer.getAdvancements().lambda$load$0(advancement, "derrotado");
        }
        ItemStack chest = new ItemStack((ItemLike)Items.DIAMOND_CHESTPLATE);
        chest.lambda$static$0(Enchantments.register, 4);
        chest.lambda$static$0(Enchantments.UNBREAKING, 3);
        chest.lambda$static$0(Enchantments.MENDING, 1);
        this.lambda$teleportPassengers$13(chest);
        ChiefCub cub = (ChiefCub)((EntityType)VilaDeFerro.CHIEF_CUB.get()).lambda$appendCustomNameConfig$2((Level)serverLevel);
        if (cub != null) {
            cub.moveTo(this.getX(), this.getY(), this.getZ(), this.getId.nextFloat() * 360.0f, 0.0f);
            cub.tame((Player)killer);
            cub.becomeBaby();
            serverLevel.addFreshEntity((Entity)cub);
        }
    }

    private void dropGoodLoot() {
        this.lambda$teleportPassengers$13(new ItemStack((ItemLike)Items.GOLDEN_APPLE, 1 + this.getId.lambda$static$0(3)));
        if (this.getId.lambda$static$0(100) < 15) {
            this.lambda$teleportPassengers$13(new ItemStack((ItemLike)Items.ENCHANTED_GOLDEN_APPLE));
        }
        this.lambda$teleportPassengers$13(new ItemStack((ItemLike)Items.DIAMOND_BLOCK, 1 + this.getId.lambda$static$0(2)));
        int books = 1 + this.getId.lambda$static$0(2);
        for (int i = 0; i < books; ++i) {
            this.lambda$teleportPassengers$13(EnchantmentHelper.lambda$deserializeEnchantments$0((RandomSource)this.getId, (ItemStack)new ItemStack((ItemLike)Items.BOOK), (int)(20 + this.getId.lambda$static$0(11)), (boolean)true));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.lambda$static$0("HasTotem", this.hasTotem);
        tag.lambda$static$0("TotemUsed", this.totemUsed);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.hasTotem = tag.getBoolean("HasTotem");
        this.totemUsed = tag.getBoolean("TotemUsed");
    }
}

