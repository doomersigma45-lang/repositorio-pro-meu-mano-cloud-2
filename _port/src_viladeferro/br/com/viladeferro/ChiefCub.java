/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.AgeableMob
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.SpawnGroupData
 *  net.minecraft.world.entity.TamableAnimal
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.FollowOwnerGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
 *  net.minecraft.world.entity.ai.goal.MeleeAttackGoal
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal
 *  net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal
 *  net.minecraft.world.entity.animal.IronGolem
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

import br.com.viladeferro.ChiefVillager;
import br.com.viladeferro.FighterVillager;
import br.com.viladeferro.Tier;
import br.com.viladeferro.VilaDeFerro;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class ChiefCub
extends TamableAnimal {
    private static final int GROW_TICKS = 12000;
    private static final int DROP_INTERVAL = 6000;
    private int summonCooldown = 100;
    private int dropTimer;
    private boolean geared;

    public ChiefCub(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createCubAttributes() {
        return Mob.createMobAttributes().lambda$create$0(Attributes.register, 20.0).lambda$create$0(Attributes.MOVEMENT_SPEED, 0.32).lambda$create$0(Attributes.ATTACK_DAMAGE, 3.0).lambda$create$0(Attributes.FOLLOW_RANGE, 32.0);
    }

    protected void registerGoals() {
        this.goalSelector.lambda$removeAllGoals$0(0, (Goal)new FloatGoal((Mob)this));
        this.goalSelector.lambda$removeAllGoals$0(1, (Goal)new SitWhenOrderedToGoal((TamableAnimal)this));
        this.goalSelector.lambda$removeAllGoals$0(2, (Goal)new MeleeAttackGoal((PathfinderMob)this, 1.2, true));
        this.goalSelector.lambda$removeAllGoals$0(3, (Goal)new FollowOwnerGoal((TamableAnimal)this, 1.1, 8.0f, 2.0f, false));
        this.goalSelector.lambda$removeAllGoals$0(4, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.8));
        this.goalSelector.lambda$removeAllGoals$0(5, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 8.0f));
        this.goalSelector.lambda$removeAllGoals$0(6, (Goal)new RandomLookAroundGoal((Mob)this));
        this.targetSelector.lambda$removeAllGoals$0(1, (Goal)new OwnerHurtByTargetGoal((TamableAnimal)this));
        this.targetSelector.lambda$removeAllGoals$0(2, (Goal)new OwnerHurtTargetGoal((TamableAnimal)this));
        this.targetSelector.lambda$removeAllGoals$0(3, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]).alertOther(new Class[0]));
    }

    public void becomeBaby() {
        this.equipGear();
        this.setAge(-12000);
        this.applyStage(true);
    }

    protected void lambda$hasFarmSeeds$9() {
        super.lambda$hasFarmSeeds$9();
        this.applyStage(true);
    }

    private void applyStage(boolean heal) {
        boolean baby = this.isBaby();
        this.lambda$addAdditionalSaveData$1(Attributes.register).lambda$replaceFrom$1(baby ? 20.0 : 60.0);
        this.lambda$addAdditionalSaveData$1(Attributes.ATTACK_DAMAGE).lambda$replaceFrom$1(baby ? 1.0 : 4.0);
        if (heal) {
            this.setHealth(this.getMaxHealth());
        }
    }

    public InteractionResult access$100(Player player, InteractionHand hand) {
        if (this.isOwnedBy((LivingEntity)player) && hand == InteractionHand.MAIN_HAND) {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.jumping = false;
                this.navigation.stop();
                this.setTarget(null);
            }
            return InteractionResult.sidedSuccess((boolean)this.level().isClientSide);
        }
        return super.access$100(player, hand);
    }

    public SpawnGroupData access$000(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.access$000(level, difficulty, reason, data, tag);
        this.equipGear();
        return result;
    }

    public void equipGear() {
        if (this.geared) {
            return;
        }
        this.geared = true;
        ItemStack helmet = ChiefCub.protect(new ItemStack((ItemLike)Items.NETHERITE_HELMET));
        helmet.lambda$static$0(Enchantments.RESPIRATION, 3);
        helmet.lambda$static$0(Enchantments.AQUA_AFFINITY, 1);
        ItemStack chest = ChiefCub.protect(new ItemStack((ItemLike)Items.NETHERITE_CHESTPLATE));
        ItemStack legs = ChiefCub.protect(new ItemStack((ItemLike)Items.NETHERITE_LEGGINGS));
        legs.lambda$static$0(Enchantments.SWIFT_SNEAK, 3);
        ItemStack boots = ChiefCub.protect(new ItemStack((ItemLike)Items.NETHERITE_BOOTS));
        boots.lambda$static$0(Enchantments.FALL_PROTECTION, 4);
        boots.lambda$static$0(Enchantments.DEPTH_STRIDER, 3);
        ItemStack axe = new ItemStack((ItemLike)Items.NETHERITE_AXE);
        axe.lambda$static$0(Enchantments.SHARPNESS, 5);
        axe.lambda$static$0(Enchantments.FIRE_ASPECT, 2);
        axe.lambda$static$0(Enchantments.MOB_LOOTING, 3);
        axe.lambda$static$0(Enchantments.BLOCK_EFFICIENCY, 5);
        axe.lambda$static$0(Enchantments.UNBREAKING, 3);
        axe.lambda$static$0(Enchantments.MENDING, 1);
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.HEAD, helmet);
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.CHEST, chest);
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.LEGS, legs);
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.FEET, boots);
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.MAINHAND, axe);
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.OFFHAND, new ItemStack((ItemLike)Items.TOTEM_OF_UNDYING));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.lambda$removeFreeWill$3(slot, 0.0f);
        }
    }

    private static ItemStack protect(ItemStack stack) {
        stack.lambda$static$0(Enchantments.register, 4);
        stack.lambda$static$0(Enchantments.FIRE_PROTECTION, 4);
        stack.lambda$static$0(Enchantments.BLAST_PROTECTION, 4);
        stack.lambda$static$0(Enchantments.PROJECTILE_PROTECTION, 4);
        stack.lambda$static$0(Enchantments.UNBREAKING, 3);
        stack.lambda$static$0(Enchantments.MENDING, 1);
        return stack;
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.lambda$static$0("Geared", this.geared);
        tag.lambda$static$0("DropTimer", this.dropTimer);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.dropTimer = tag.copy("DropTimer");
        this.geared = tag.getBoolean("Geared");
        this.applyStage(false);
    }

    public EntityDimensions lambda$addAdditionalSaveData$2(Pose pose) {
        EntityDimensions base = super.lambda$addAdditionalSaveData$2(pose);
        return this.isBaby() ? base.scale(0.5f) : base;
    }

    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && (target == this.getOwner() || VilaDeFerro.isNeverTarget((Entity)target))) {
            return;
        }
        super.setTarget(target);
    }

    public boolean access$000(LivingEntity target, LivingEntity owner) {
        TamableAnimal pet;
        if (VilaDeFerro.isNeverTarget((Entity)target)) {
            return false;
        }
        if (target instanceof TamableAnimal && (pet = (TamableAnimal)target).isTame() && pet.getOwner() == owner) {
            return false;
        }
        if (target instanceof Player) {
            Player ownerPlayer;
            Player other = (Player)target;
            if (owner instanceof Player && !(ownerPlayer = (Player)owner).lambda$addAdditionalSaveData$2(other)) {
                return false;
            }
        }
        return true;
    }

    public void lerpMotion() {
        IronGolem golem;
        Level level;
        super.lerpMotion();
        if (this.level().isClientSide || !((level = this.level()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        if (++this.dropTimer >= 6000) {
            this.dropTimer = 0;
            this.dropGift(serverLevel);
        }
        if (this.isOrderedToSit()) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        if (--this.summonCooldown > 0) {
            return;
        }
        boolean baby = this.isBaby();
        this.summonCooldown = baby ? 240 : 160;
        int warriorCap = baby ? 3 : 7;
        int golemCap = baby ? 1 : 2;
        long warriors = serverLevel.lambda$getNearbyEntities$1(FighterVillager.class, this.getBoundingBox().inflate(24.0), f -> !(f instanceof ChiefVillager)).size();
        long golems = serverLevel.lambda$getNearbyEntities$1(IronGolem.class, this.getBoundingBox().inflate(24.0)).size();
        int newWarriors = (int)Math.max(0L, Math.min(baby ? 1L : 3L, (long)warriorCap - warriors));
        for (int i = 0; i < newWarriors; ++i) {
            FighterVillager ally = (FighterVillager)((EntityType)VilaDeFerro.FIGHTER.get()).lambda$appendCustomNameConfig$2((Level)serverLevel);
            if (ally == null) continue;
            this.place((Mob)ally, i, newWarriors);
            ally.access$000((ServerLevelAccessor)serverLevel, serverLevel.getCurrentDifficultyAt(ally.blockPosition()), MobSpawnType.EVENT, null, null);
            ally.setTier(Tier.PASSIVE);
            ally.setTarget(target);
            serverLevel.addFreshEntity((Entity)ally);
        }
        if (golems < (long)golemCap && (golem = (IronGolem)EntityType.IRON_GOLEM.lambda$appendCustomNameConfig$2((Level)serverLevel)) != null) {
            golem.setPlayerCreated(true);
            Tier.set(golem, Tier.PASSIVE);
            this.place((Mob)golem, 0, 1);
            golem.setTarget(target);
            serverLevel.addFreshEntity((Entity)golem);
        }
    }

    private void dropGift(ServerLevel serverLevel) {
        int count = 1 + this.getId.lambda$static$0(2);
        for (int i = 0; i < count; ++i) {
            int roll = this.getId.lambda$static$0(100);
            ItemStack gift = roll < 30 ? new ItemStack((ItemLike)Items.GOLDEN_APPLE, 1 + this.getId.lambda$static$0(2)) : (roll < 55 ? new ItemStack((ItemLike)Items.DIAMOND, 1 + this.getId.lambda$static$0(3)) : (roll < 75 ? EnchantmentHelper.lambda$deserializeEnchantments$0((RandomSource)this.getId, (ItemStack)new ItemStack((ItemLike)Items.BOOK), (int)(15 + this.getId.lambda$static$0(16)), (boolean)true) : (roll < 90 ? new ItemStack((ItemLike)Items.DIAMOND_BLOCK) : (roll < 97 ? new ItemStack((ItemLike)Items.EXPERIENCE_BOTTLE, 4 + this.getId.lambda$static$0(5)) : new ItemStack((ItemLike)Items.ENCHANTED_GOLDEN_APPLE)))));
            this.lambda$teleportPassengers$13(gift);
        }
        serverLevel.lambda$new$0((ParticleOptions)ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 1.0, this.getZ(), 8, 0.4, 0.5, 0.4, 0.0);
    }

    private void place(Mob mob, int index, int total) {
        double angle = Math.PI * 2 * (double)index / (double)Math.max(1, total) + this.getId.nextDouble() * 0.5;
        mob.moveTo(this.getX() + Math.cos(angle) * 2.5, this.getY(), this.getZ() + Math.sin(angle) * 2.5, this.getId.nextFloat() * 360.0f, 0.0f);
    }

    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    public AgeableMob access$000(ServerLevel level, AgeableMob other) {
        return null;
    }
}

