/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.LeapAtTargetGoal
 *  net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
 *  net.minecraft.world.entity.ai.goal.MeleeAttackGoal
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 */
package com.claudecontent.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CinderStalkerEntity
extends Monster {
    public CinderStalkerEntity(EntityType<? extends CinderStalkerEntity> type, Level level) {
        super(type, level);
        this.getArmorSlots = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().lambda$create$0(Attributes.register, 26.0).lambda$create$0(Attributes.ATTACK_DAMAGE, 5.0).lambda$create$0(Attributes.MOVEMENT_SPEED, 0.32).lambda$create$0(Attributes.FOLLOW_RANGE, 28.0);
    }

    protected void registerGoals() {
        this.goalSelector.lambda$removeAllGoals$0(0, (Goal)new FloatGoal((Mob)this));
        this.goalSelector.lambda$removeAllGoals$0(3, (Goal)new LeapAtTargetGoal((Mob)this, 0.45f));
        this.goalSelector.lambda$removeAllGoals$0(4, (Goal)new MeleeAttackGoal((PathfinderMob)this, 1.0, false));
        this.goalSelector.lambda$removeAllGoals$0(5, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.8));
        this.goalSelector.lambda$removeAllGoals$0(6, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 8.0f));
        this.goalSelector.lambda$removeAllGoals$0(6, (Goal)new RandomLookAroundGoal((Mob)this));
        this.targetSelector.lambda$removeAllGoals$0(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
        this.targetSelector.lambda$removeAllGoals$0(2, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, true));
    }

    public void aiStep() {
        if (this.level().isClientSide) {
            if (this.getId.lambda$static$0(3) == 0) {
                this.level().lambda$new$2((ParticleOptions)ParticleTypes.SMOKE, this.lambda$startRiding$3(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.03, 0.0);
            }
            if (this.getId.lambda$static$0(8) == 0) {
                this.level().lambda$new$2((ParticleOptions)ParticleTypes.FLAME, this.lambda$startRiding$3(0.5), this.getY() + 0.8, this.getRandomZ(0.5), 0.0, 0.02, 0.0);
            }
        } else if (this.getTags % 20 == 0 && this.isInWaterRainOrBubble()) {
            this.sendSystemMessage(this.damageSources().drown(), 1.0f);
        }
        super.aiStep();
    }

    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            target.getRandomZ(4);
        }
        return hit;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    protected SoundEvent giveExperiencePoints(DamageSource source) {
        return SoundEvents.BLAZE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.BLAZE_DEATH;
    }
}

