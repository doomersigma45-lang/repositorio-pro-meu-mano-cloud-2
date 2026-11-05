/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerBossEvent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.BossEvent$BossBarColor
 *  net.minecraft.world.BossEvent$BossBarOverlay
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
 *  net.minecraft.world.entity.ai.goal.MeleeAttackGoal
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.claudecontent.entity;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MossColossusEntity
extends Monster {
    private final ServerBossEvent bossEvent = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_10).setDarkenScreen(true);
    private int slamCooldown = 80;
    private boolean secondPhase;

    public MossColossusEntity(EntityType<? extends MossColossusEntity> type, Level level) {
        super(type, level);
        this.getArmorSlots = 180;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().lambda$create$0(Attributes.register, 240.0).lambda$create$0(Attributes.ATTACK_DAMAGE, 11.0).lambda$create$0(Attributes.MOVEMENT_SPEED, 0.23).lambda$create$0(Attributes.KNOCKBACK_RESISTANCE, 0.85).lambda$create$0(Attributes.ARMOR, 10.0).lambda$create$0(Attributes.FOLLOW_RANGE, 36.0);
    }

    protected void registerGoals() {
        this.goalSelector.lambda$removeAllGoals$0(0, (Goal)new FloatGoal((Mob)this));
        this.goalSelector.lambda$removeAllGoals$0(2, (Goal)new MeleeAttackGoal((PathfinderMob)this, 1.0, true));
        this.goalSelector.lambda$removeAllGoals$0(5, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.65));
        this.goalSelector.lambda$removeAllGoals$0(6, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 24.0f));
        this.goalSelector.lambda$removeAllGoals$0(7, (Goal)new RandomLookAroundGoal((Mob)this));
        this.targetSelector.lambda$removeAllGoals$0(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
        this.targetSelector.lambda$removeAllGoals$0(2, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, true));
    }

    protected void customServerAiStep() {
        LivingEntity target;
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        if (!this.secondPhase && this.getHealth() <= this.getMaxHealth() * 0.5f) {
            this.secondPhase = true;
            this.lambda$addAdditionalSaveData$1(Attributes.MOVEMENT_SPEED).lambda$replaceFrom$1(0.29);
            this.level().gameEvent(null, this.blockPosition(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 2.0f, 0.65f);
            Level level = this.level();
            if (level instanceof ServerLevel) {
                ServerLevel server = (ServerLevel)level;
                server.lambda$new$0((ParticleOptions)ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 2.0, this.getZ(), 35, 1.2, 1.4, 1.2, 0.02);
            }
        }
        if ((target = this.getTarget()) == null || !target.isAlive()) {
            return;
        }
        if (--this.slamCooldown <= 0 && this.getZ((Entity)target) < 64.0 && this.onGround()) {
            this.rootSlam();
            this.slamCooldown = this.secondPhase ? 75 : 125;
        }
    }

    private void rootSlam() {
        Level level = this.level();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel server = (ServerLevel)level;
        server.gameEvent(null, this.blockPosition(), SoundEvents.ROOTED_DIRT_BREAK, SoundSource.HOSTILE, 1.6f, 0.7f);
        server.lambda$new$0((ParticleOptions)new BlockParticleOption(ParticleTypes.lambda$static$2, Blocks.MOSSY_COBBLESTONE.defaultBlockState()), this.getX(), this.getY() + 0.1, this.getZ(), 55, 3.0, 0.15, 3.0, 0.1);
        double radius = this.secondPhase ? 6.0 : 4.5;
        for (Player player : server.lambda$getNearbyEntities$1(Player.class, new AABB(this.blockPosition()).getYsize(radius, 2.0, radius))) {
            if (player.isCreative() || player.isSpectator()) continue;
            Vec3 push = player.position().multiply(this.position()).multiply().lambda$static$1(0.85);
            player.setIsInPowderSnow(push.z, 0.48, push.reverse);
            player.getAddEntityPacket = true;
            player.sendSystemMessage(this.damageSources().thrown((LivingEntity)this), this.secondPhase ? 12.0f : 8.0f);
            if (!this.secondPhase || !player.isAlive()) continue;
            player.getStandingEyeHeight(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 0));
        }
    }

    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)target;
            living.getStandingEyeHeight(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 35, 0));
        }
        return hit;
    }

    public void aiStep() {
        if (this.level().isClientSide) {
            this.level().lambda$new$2((ParticleOptions)ParticleTypes.SPORE_BLOSSOM_AIR, this.lambda$startRiding$3(0.75), this.getRandomY(), this.getRandomZ(0.75), 0.0, 0.01, 0.0);
        }
        super.aiStep();
    }

    public void canBeAffected(ServerPlayer player) {
        super.canBeAffected(player);
        this.bossEvent.addPlayer(player);
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removeAllPlayers(player);
    }

    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossEvent.setDarkenScreen(this.getDisplayName());
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.IRON_GOLEM_STEP;
    }

    protected SoundEvent giveExperiencePoints(DamageSource source) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.lambda$static$0("SecondPhase", this.secondPhase);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.secondPhase = tag.getBoolean("SecondPhase");
        if (this.secondPhase) {
            this.lambda$addAdditionalSaveData$1(Attributes.MOVEMENT_SPEED).lambda$replaceFrom$1(0.29);
        }
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        if (this.hasCustomName()) {
            this.bossEvent.setDarkenScreen(this.getDisplayName());
        }
    }
}

