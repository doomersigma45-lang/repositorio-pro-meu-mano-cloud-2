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
        this.f_21364_ = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.m_33035_().m_22268_(Attributes.f_22276_, 26.0).m_22268_(Attributes.f_22281_, 5.0).m_22268_(Attributes.f_22279_, 0.32).m_22268_(Attributes.f_22277_, 28.0);
    }

    protected void m_8099_() {
        this.f_21345_.m_25352_(0, (Goal)new FloatGoal((Mob)this));
        this.f_21345_.m_25352_(3, (Goal)new LeapAtTargetGoal((Mob)this, 0.45f));
        this.f_21345_.m_25352_(4, (Goal)new MeleeAttackGoal((PathfinderMob)this, 1.0, false));
        this.f_21345_.m_25352_(5, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.8));
        this.f_21345_.m_25352_(6, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 8.0f));
        this.f_21345_.m_25352_(6, (Goal)new RandomLookAroundGoal((Mob)this));
        this.f_21346_.m_25352_(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
        this.f_21346_.m_25352_(2, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, true));
    }

    public void m_8107_() {
        if (this.m_9236_().f_46443_) {
            if (this.f_19796_.m_188503_(3) == 0) {
                this.m_9236_().m_7106_((ParticleOptions)ParticleTypes.f_123762_, this.m_20208_(0.5), this.m_20187_(), this.m_20262_(0.5), 0.0, 0.03, 0.0);
            }
            if (this.f_19796_.m_188503_(8) == 0) {
                this.m_9236_().m_7106_((ParticleOptions)ParticleTypes.f_123744_, this.m_20208_(0.5), this.m_20186_() + 0.8, this.m_20262_(0.5), 0.0, 0.02, 0.0);
            }
        } else if (this.f_19797_ % 20 == 0 && this.m_20071_()) {
            this.m_6469_(this.m_269291_().m_269063_(), 1.0f);
        }
        super.m_8107_();
    }

    public boolean m_7327_(Entity target) {
        boolean hit = super.m_7327_(target);
        if (hit) {
            target.m_20254_(4);
        }
        return hit;
    }

    protected SoundEvent m_7515_() {
        return SoundEvents.f_12432_;
    }

    protected SoundEvent m_7975_(DamageSource source) {
        return SoundEvents.f_11704_;
    }

    protected SoundEvent m_5592_() {
        return SoundEvents.f_11703_;
    }
}

