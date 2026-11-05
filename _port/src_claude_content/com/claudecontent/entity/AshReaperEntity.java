/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.world.BossEvent$BossBarColor
 *  net.minecraft.world.BossEvent$BossBarOverlay
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
 *  net.minecraft.world.entity.ai.goal.MeleeAttackGoal
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.SmallFireball
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.claudecontent.entity;

import com.claudecontent.entity.CinderStalkerEntity;
import com.claudecontent.registry.ModEntities;
import java.util.EnumSet;
import java.util.UUID;
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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AshReaperEntity
extends Monster {
    private static final UUID ENRAGE_SPEED_UUID = UUID.fromString("5b6f1c4e-3a52-4b7a-9d57-0c1f2f6f7a11");
    private static final float MAX_DAMAGE_PER_HIT = 40.0f;
    private static final double SLAM_RADIUS = 6.0;
    private final ServerBossEvent bossEvent = (ServerBossEvent)new ServerBossEvent(this.m_5446_(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10).m_7003_(true);
    private boolean enraged = false;
    private int slamCooldown = 100;
    private int summonCooldown = 160;

    public AshReaperEntity(EntityType<? extends AshReaperEntity> type, Level level) {
        super(type, level);
        this.f_21364_ = 250;
        this.m_21530_();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.m_33035_().m_22268_(Attributes.f_22276_, 300.0).m_22268_(Attributes.f_22281_, 12.0).m_22268_(Attributes.f_22279_, 0.28).m_22268_(Attributes.f_22278_, 0.9).m_22268_(Attributes.f_22284_, 8.0).m_22268_(Attributes.f_22277_, 40.0);
    }

    protected void m_8099_() {
        this.f_21345_.m_25352_(0, (Goal)new FloatGoal((Mob)this));
        this.f_21345_.m_25352_(2, (Goal)new AshFireballGoal(this));
        this.f_21345_.m_25352_(3, (Goal)new MeleeAttackGoal((PathfinderMob)this, 1.1, true));
        this.f_21345_.m_25352_(5, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.8));
        this.f_21345_.m_25352_(6, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 32.0f));
        this.f_21345_.m_25352_(7, (Goal)new RandomLookAroundGoal((Mob)this));
        this.f_21346_.m_25352_(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
        this.f_21346_.m_25352_(2, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, true));
    }

    public boolean isEnraged() {
        return this.enraged;
    }

    protected void m_8024_() {
        LivingEntity target;
        super.m_8024_();
        this.bossEvent.m_142711_(this.m_21223_() / this.m_21233_());
        if (!this.enraged && this.m_21223_() <= this.m_21233_() * 0.5f) {
            this.enrage(true);
        }
        if ((target = this.m_5448_()) == null || !target.m_6084_()) {
            return;
        }
        if (this.slamCooldown > 0) {
            --this.slamCooldown;
        } else if (this.m_20280_((Entity)target) < 36.0 && this.m_20096_()) {
            this.groundSlam();
            int n = this.slamCooldown = this.enraged ? 100 : 180;
        }
        if (this.enraged) {
            if (this.summonCooldown > 0) {
                --this.summonCooldown;
            } else {
                this.summonStalkers(target);
                this.summonCooldown = 300;
            }
        }
    }

    private void enrage(boolean withEffects) {
        this.enraged = true;
        AttributeInstance speed = this.m_21051_(Attributes.f_22279_);
        if (speed != null && speed.m_22111_(ENRAGE_SPEED_UUID) == null) {
            speed.m_22118_(new AttributeModifier(ENRAGE_SPEED_UUID, "ash_reaper_enrage", 0.3, AttributeModifier.Operation.MULTIPLY_BASE));
        }
        if (withEffects) {
            this.m_9236_().m_5594_(null, this.m_20183_(), SoundEvents.f_12554_, SoundSource.HOSTILE, 2.0f, 0.5f);
            Level level = this.m_9236_();
            if (level instanceof ServerLevel) {
                ServerLevel server = (ServerLevel)level;
                server.m_8767_((ParticleOptions)ParticleTypes.f_123756_, this.m_20185_(), this.m_20186_() + 1.5, this.m_20189_(), 50, 1.0, 1.5, 1.0, 0.1);
            }
        }
        this.bossEvent.m_6451_(BossEvent.BossBarColor.PURPLE);
    }

    private void groundSlam() {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel server = (ServerLevel)level;
        server.m_5594_(null, this.m_20183_(), SoundEvents.f_11913_, SoundSource.HOSTILE, 1.5f, 0.7f);
        server.m_8767_((ParticleOptions)ParticleTypes.f_123813_, this.m_20185_(), this.m_20186_() + 0.2, this.m_20189_(), 6, 2.0, 0.1, 2.0, 0.0);
        server.m_8767_((ParticleOptions)ParticleTypes.f_123744_, this.m_20185_(), this.m_20186_() + 0.2, this.m_20189_(), 60, 3.0, 0.1, 3.0, 0.08);
        for (Player player : server.m_45976_(Player.class, this.m_20191_().m_82377_(6.0, 2.0, 6.0))) {
            if (player.m_7500_() || player.m_5833_() || !player.m_20096_()) continue;
            player.m_6469_(this.m_269291_().m_269333_((LivingEntity)this), this.enraged ? 14.0f : 10.0f);
            player.m_20254_(3);
            Vec3 push = player.m_20182_().m_82546_(this.m_20182_()).m_82541_().m_82490_(0.8);
            player.m_20334_(push.f_82479_, 0.6, push.f_82481_);
            player.f_19864_ = true;
        }
    }

    private void summonStalkers(LivingEntity target) {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel server = (ServerLevel)level;
        int existing = server.m_45976_(CinderStalkerEntity.class, this.m_20191_().m_82400_(24.0)).size();
        int toSpawn = Math.min(2, 4 - existing);
        for (int i = 0; i < toSpawn; ++i) {
            CinderStalkerEntity stalker = (CinderStalkerEntity)((EntityType)ModEntities.CINDER_STALKER.get()).m_20615_((Level)server);
            if (stalker == null) continue;
            double angle = this.f_19796_.m_188500_() * Math.PI * 2.0;
            stalker.m_7678_(this.m_20185_() + Math.cos(angle) * 3.0, this.m_20186_(), this.m_20189_() + Math.sin(angle) * 3.0, this.f_19796_.m_188501_() * 360.0f, 0.0f);
            if (!server.m_45786_((Entity)stalker)) {
                stalker.m_146870_();
                continue;
            }
            stalker.m_6710_(target);
            server.m_7967_((Entity)stalker);
            server.m_8767_((ParticleOptions)ParticleTypes.f_123755_, stalker.m_20185_(), stalker.m_20186_() + 0.5, stalker.m_20189_(), 15, 0.4, 0.4, 0.4, 0.02);
        }
    }

    public void m_8107_() {
        if (this.m_9236_().f_46443_) {
            for (int i = 0; i < 2; ++i) {
                this.m_9236_().m_7106_((ParticleOptions)ParticleTypes.f_123783_, this.m_20208_(0.6), this.m_20187_(), this.m_20262_(0.6), 0.0, -0.02, 0.0);
            }
            if (this.enraged || this.f_19796_.m_188503_(4) == 0) {
                this.m_9236_().m_7106_((ParticleOptions)ParticleTypes.f_123744_, this.m_20208_(0.5), this.m_20186_() + 2.6, this.m_20262_(0.5), 0.0, 0.02, 0.0);
            }
        }
        super.m_8107_();
    }

    public boolean m_7327_(Entity target) {
        boolean hit = super.m_7327_(target);
        if (hit) {
            target.m_20254_(5);
        }
        return hit;
    }

    public boolean m_6469_(DamageSource source, float amount) {
        if (!source.m_269533_(DamageTypeTags.f_268738_)) {
            amount = Math.min(amount, 40.0f);
        }
        return super.m_6469_(source, amount);
    }

    public boolean m_6072_() {
        return false;
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

    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        tag.m_128379_("Enraged", this.enraged);
    }

    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        if (this.m_8077_()) {
            this.bossEvent.m_6456_(this.m_5446_());
        }
        if (tag.m_128471_("Enraged")) {
            this.enrage(false);
        }
    }

    protected SoundEvent m_7515_() {
        return SoundEvents.f_12559_;
    }

    protected SoundEvent m_7975_(DamageSource source) {
        return SoundEvents.f_12557_;
    }

    protected SoundEvent m_5592_() {
        return SoundEvents.f_12556_;
    }

    protected float m_6121_() {
        return 1.5f;
    }

    private static class AshFireballGoal
    extends Goal {
        private final AshReaperEntity boss;
        private int cooldown = 40;

        AshFireballGoal(AshReaperEntity boss) {
            this.boss = boss;
            this.m_7021_(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean m_8036_() {
            LivingEntity target = this.boss.m_5448_();
            if (target == null || !target.m_6084_()) {
                return false;
            }
            double distSqr = this.boss.m_20280_((Entity)target);
            return distSqr > 36.0 && distSqr < 576.0 && this.boss.m_142582_((Entity)target);
        }

        public boolean m_8045_() {
            return this.m_8036_();
        }

        public void m_8037_() {
            LivingEntity target = this.boss.m_5448_();
            if (target == null) {
                return;
            }
            this.boss.m_21563_().m_24960_((Entity)target, 30.0f, 30.0f);
            if (--this.cooldown > 0) {
                return;
            }
            this.cooldown = this.boss.isEnraged() ? 40 : 70;
            int count = this.boss.isEnraged() ? 3 : 1;
            double baseDx = target.m_20185_() - this.boss.m_20185_();
            double dy = target.m_20227_(0.5) - this.boss.m_20227_(0.75);
            double baseDz = target.m_20189_() - this.boss.m_20189_();
            for (int i = 0; i < count; ++i) {
                double spread = ((double)i - (double)(count - 1) / 2.0) * 0.35;
                double dx = baseDx + spread * baseDz * 0.2;
                double dz = baseDz - spread * baseDx * 0.2;
                SmallFireball fireball = new SmallFireball(this.boss.m_9236_(), (LivingEntity)this.boss, dx, dy, dz);
                fireball.m_6034_(this.boss.m_20185_(), this.boss.m_20227_(2.4), this.boss.m_20189_());
                this.boss.m_9236_().m_7967_((Entity)fireball);
            }
            this.boss.m_9236_().m_5594_(null, this.boss.m_20183_(), SoundEvents.f_11705_, SoundSource.HOSTILE, 1.5f, 0.8f);
        }
    }
}

