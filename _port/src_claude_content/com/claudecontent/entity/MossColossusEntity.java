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
    private final ServerBossEvent bossEvent = (ServerBossEvent)new ServerBossEvent(this.m_5446_(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_10).m_7003_(true);
    private int slamCooldown = 80;
    private boolean secondPhase;

    public MossColossusEntity(EntityType<? extends MossColossusEntity> type, Level level) {
        super(type, level);
        this.f_21364_ = 180;
        this.m_21530_();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.m_33035_().m_22268_(Attributes.f_22276_, 240.0).m_22268_(Attributes.f_22281_, 11.0).m_22268_(Attributes.f_22279_, 0.23).m_22268_(Attributes.f_22278_, 0.85).m_22268_(Attributes.f_22284_, 10.0).m_22268_(Attributes.f_22277_, 36.0);
    }

    protected void m_8099_() {
        this.f_21345_.m_25352_(0, (Goal)new FloatGoal((Mob)this));
        this.f_21345_.m_25352_(2, (Goal)new MeleeAttackGoal((PathfinderMob)this, 1.0, true));
        this.f_21345_.m_25352_(5, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.65));
        this.f_21345_.m_25352_(6, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 24.0f));
        this.f_21345_.m_25352_(7, (Goal)new RandomLookAroundGoal((Mob)this));
        this.f_21346_.m_25352_(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
        this.f_21346_.m_25352_(2, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, true));
    }

    protected void m_8024_() {
        LivingEntity target;
        super.m_8024_();
        this.bossEvent.m_142711_(this.m_21223_() / this.m_21233_());
        if (!this.secondPhase && this.m_21223_() <= this.m_21233_() * 0.5f) {
            this.secondPhase = true;
            this.m_21051_(Attributes.f_22279_).m_22100_(0.29);
            this.m_9236_().m_5594_(null, this.m_20183_(), SoundEvents.f_12009_, SoundSource.HOSTILE, 2.0f, 0.65f);
            Level level = this.m_9236_();
            if (level instanceof ServerLevel) {
                ServerLevel server = (ServerLevel)level;
                server.m_8767_((ParticleOptions)ParticleTypes.f_123748_, this.m_20185_(), this.m_20186_() + 2.0, this.m_20189_(), 35, 1.2, 1.4, 1.2, 0.02);
            }
        }
        if ((target = this.m_5448_()) == null || !target.m_6084_()) {
            return;
        }
        if (--this.slamCooldown <= 0 && this.m_20280_((Entity)target) < 64.0 && this.m_20096_()) {
            this.rootSlam();
            this.slamCooldown = this.secondPhase ? 75 : 125;
        }
    }

    private void rootSlam() {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel server = (ServerLevel)level;
        server.m_5594_(null, this.m_20183_(), SoundEvents.f_144206_, SoundSource.HOSTILE, 1.6f, 0.7f);
        server.m_8767_((ParticleOptions)new BlockParticleOption(ParticleTypes.f_123794_, Blocks.f_50079_.m_49966_()), this.m_20185_(), this.m_20186_() + 0.1, this.m_20189_(), 55, 3.0, 0.15, 3.0, 0.1);
        double radius = this.secondPhase ? 6.0 : 4.5;
        for (Player player : server.m_45976_(Player.class, new AABB(this.m_20183_()).m_82377_(radius, 2.0, radius))) {
            if (player.m_7500_() || player.m_5833_()) continue;
            Vec3 push = player.m_20182_().m_82546_(this.m_20182_()).m_82541_().m_82490_(0.85);
            player.m_20334_(push.f_82479_, 0.48, push.f_82481_);
            player.f_19864_ = true;
            player.m_6469_(this.m_269291_().m_269333_((LivingEntity)this), this.secondPhase ? 12.0f : 8.0f);
            if (!this.secondPhase || !player.m_6084_()) continue;
            player.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 50, 0));
        }
    }

    public boolean m_7327_(Entity target) {
        boolean hit = super.m_7327_(target);
        if (hit && target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)target;
            living.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 35, 0));
        }
        return hit;
    }

    public void m_8107_() {
        if (this.m_9236_().f_46443_) {
            this.m_9236_().m_7106_((ParticleOptions)ParticleTypes.f_175833_, this.m_20208_(0.75), this.m_20187_(), this.m_20262_(0.75), 0.0, 0.01, 0.0);
        }
        super.m_8107_();
    }

    public void m_6457_(ServerPlayer player) {
        super.m_6457_(player);
        this.bossEvent.m_6543_(player);
    }

    public void m_6452_(ServerPlayer player) {
        super.m_6452_(player);
        this.bossEvent.m_6539_(player);
    }

    public void m_6593_(Component name) {
        super.m_6593_(name);
        this.bossEvent.m_6456_(this.m_5446_());
    }

    protected SoundEvent m_7515_() {
        return SoundEvents.f_12010_;
    }

    protected SoundEvent m_7975_(DamageSource source) {
        return SoundEvents.f_12008_;
    }

    protected SoundEvent m_5592_() {
        return SoundEvents.f_12059_;
    }

    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        tag.m_128379_("SecondPhase", this.secondPhase);
    }

    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        this.secondPhase = tag.m_128471_("SecondPhase");
        if (this.secondPhase) {
            this.m_21051_(Attributes.f_22279_).m_22100_(0.29);
        }
        this.bossEvent.m_142711_(this.m_21223_() / this.m_21233_());
        if (this.m_8077_()) {
            this.bossEvent.m_6456_(this.m_5446_());
        }
    }
}

