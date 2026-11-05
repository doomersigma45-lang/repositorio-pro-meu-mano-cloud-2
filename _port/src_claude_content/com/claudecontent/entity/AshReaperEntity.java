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
    private final ServerBossEvent bossEvent = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10).setDarkenScreen(true);
    private boolean enraged = false;
    private int slamCooldown = 100;
    private int summonCooldown = 160;

    public AshReaperEntity(EntityType<? extends AshReaperEntity> type, Level level) {
        super(type, level);
        this.getArmorSlots = 250;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().lambda$create$0(Attributes.register, 300.0).lambda$create$0(Attributes.ATTACK_DAMAGE, 12.0).lambda$create$0(Attributes.MOVEMENT_SPEED, 0.28).lambda$create$0(Attributes.KNOCKBACK_RESISTANCE, 0.9).lambda$create$0(Attributes.ARMOR, 8.0).lambda$create$0(Attributes.FOLLOW_RANGE, 40.0);
    }

    protected void registerGoals() {
        this.goalSelector.lambda$removeAllGoals$0(0, (Goal)new FloatGoal((Mob)this));
        this.goalSelector.lambda$removeAllGoals$0(2, (Goal)new AshFireballGoal(this));
        this.goalSelector.lambda$removeAllGoals$0(3, (Goal)new MeleeAttackGoal((PathfinderMob)this, 1.1, true));
        this.goalSelector.lambda$removeAllGoals$0(5, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.8));
        this.goalSelector.lambda$removeAllGoals$0(6, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 32.0f));
        this.goalSelector.lambda$removeAllGoals$0(7, (Goal)new RandomLookAroundGoal((Mob)this));
        this.targetSelector.lambda$removeAllGoals$0(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
        this.targetSelector.lambda$removeAllGoals$0(2, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, true));
    }

    public boolean isEnraged() {
        return this.enraged;
    }

    protected void customServerAiStep() {
        LivingEntity target;
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        if (!this.enraged && this.getHealth() <= this.getMaxHealth() * 0.5f) {
            this.enrage(true);
        }
        if ((target = this.getTarget()) == null || !target.isAlive()) {
            return;
        }
        if (this.slamCooldown > 0) {
            --this.slamCooldown;
        } else if (this.getZ((Entity)target) < 36.0 && this.onGround()) {
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
        AttributeInstance speed = this.lambda$addAdditionalSaveData$1(Attributes.MOVEMENT_SPEED);
        if (speed != null && speed.lambda$replaceFrom$1(ENRAGE_SPEED_UUID) == null) {
            speed.getModifiersOrEmpty(new AttributeModifier(ENRAGE_SPEED_UUID, "ash_reaper_enrage", 0.3, AttributeModifier.Operation.MULTIPLY_BASE));
        }
        if (withEffects) {
            this.level().gameEvent(null, this.blockPosition(), SoundEvents.WITHER_AMBIENT, SoundSource.HOSTILE, 2.0f, 0.5f);
            Level level = this.level();
            if (level instanceof ServerLevel) {
                ServerLevel server = (ServerLevel)level;
                server.lambda$new$0((ParticleOptions)ParticleTypes.LAVA, this.getX(), this.getY() + 1.5, this.getZ(), 50, 1.0, 1.5, 1.0, 0.1);
            }
        }
        this.bossEvent.setDarkenScreen(BossEvent.BossBarColor.PURPLE);
    }

    private void groundSlam() {
        Level level = this.level();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel server = (ServerLevel)level;
        server.gameEvent(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.5f, 0.7f);
        server.lambda$new$0((ParticleOptions)ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.2, this.getZ(), 6, 2.0, 0.1, 2.0, 0.0);
        server.lambda$new$0((ParticleOptions)ParticleTypes.FLAME, this.getX(), this.getY() + 0.2, this.getZ(), 60, 3.0, 0.1, 3.0, 0.08);
        for (Player player : server.lambda$getNearbyEntities$1(Player.class, this.getBoundingBox().getYsize(6.0, 2.0, 6.0))) {
            if (player.isCreative() || player.isSpectator() || !player.onGround()) continue;
            player.sendSystemMessage(this.damageSources().thrown((LivingEntity)this), this.enraged ? 14.0f : 10.0f);
            player.getRandomZ(3);
            Vec3 push = player.position().multiply(this.position()).multiply().lambda$static$1(0.8);
            player.setIsInPowderSnow(push.z, 0.6, push.reverse);
            player.getAddEntityPacket = true;
        }
    }

    private void summonStalkers(LivingEntity target) {
        Level level = this.level();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel server = (ServerLevel)level;
        int existing = server.lambda$getNearbyEntities$1(CinderStalkerEntity.class, this.getBoundingBox().inflate(24.0)).size();
        int toSpawn = Math.min(2, 4 - existing);
        for (int i = 0; i < toSpawn; ++i) {
            CinderStalkerEntity stalker = (CinderStalkerEntity)((EntityType)ModEntities.CINDER_STALKER.get()).lambda$appendCustomNameConfig$2((Level)server);
            if (stalker == null) continue;
            double angle = this.getId.nextDouble() * Math.PI * 2.0;
            stalker.moveTo(this.getX() + Math.cos(angle) * 3.0, this.getY(), this.getZ() + Math.sin(angle) * 3.0, this.getId.nextFloat() * 360.0f, 0.0f);
            if (!server.borderCollision((Entity)stalker)) {
                stalker.discard();
                continue;
            }
            stalker.setTarget(target);
            server.addFreshEntity((Entity)stalker);
            server.lambda$new$0((ParticleOptions)ParticleTypes.LARGE_SMOKE, stalker.getX(), stalker.getY() + 0.5, stalker.getZ(), 15, 0.4, 0.4, 0.4, 0.02);
        }
    }

    public void aiStep() {
        if (this.level().isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.level().lambda$new$2((ParticleOptions)ParticleTypes.ASH, this.lambda$startRiding$3(0.6), this.getRandomY(), this.getRandomZ(0.6), 0.0, -0.02, 0.0);
            }
            if (this.enraged || this.getId.lambda$static$0(4) == 0) {
                this.level().lambda$new$2((ParticleOptions)ParticleTypes.FLAME, this.lambda$startRiding$3(0.5), this.getY() + 2.6, this.getRandomZ(0.5), 0.0, 0.02, 0.0);
            }
        }
        super.aiStep();
    }

    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            target.getRandomZ(5);
        }
        return hit;
    }

    public boolean sendSystemMessage(DamageSource source, float amount) {
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            amount = Math.min(amount, 40.0f);
        }
        return super.sendSystemMessage(source, amount);
    }

    public boolean canChangeDimensions() {
        return false;
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

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.lambda$static$0("Enraged", this.enraged);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (this.hasCustomName()) {
            this.bossEvent.setDarkenScreen(this.getDisplayName());
        }
        if (tag.getBoolean("Enraged")) {
            this.enrage(false);
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    protected SoundEvent giveExperiencePoints(DamageSource source) {
        return SoundEvents.WITHER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_DEATH;
    }

    protected float getSoundVolume() {
        return 1.5f;
    }

    private static class AshFireballGoal
    extends Goal {
        private final AshReaperEntity boss;
        private int cooldown = 40;

        AshFireballGoal(AshReaperEntity boss) {
            this.boss = boss;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean lambda$hasSuitablePoi$0() {
            LivingEntity target = this.boss.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            double distSqr = this.boss.getZ((Entity)target);
            return distSqr > 36.0 && distSqr < 576.0 && this.boss.hasLineOfSight((Entity)target);
        }

        public boolean canContinueToUse() {
            return this.lambda$hasSuitablePoi$0();
        }

        public void tick() {
            LivingEntity target = this.boss.getTarget();
            if (target == null) {
                return;
            }
            this.boss.getLookControl().lambda$tick$1((Entity)target, 30.0f, 30.0f);
            if (--this.cooldown > 0) {
                return;
            }
            this.cooldown = this.boss.isEnraged() ? 40 : 70;
            int count = this.boss.isEnraged() ? 3 : 1;
            double baseDx = target.getX() - this.boss.getX();
            double dy = target.lambda$move$0(0.5) - this.boss.lambda$move$0(0.75);
            double baseDz = target.getZ() - this.boss.getZ();
            for (int i = 0; i < count; ++i) {
                double spread = ((double)i - (double)(count - 1) / 2.0) * 0.35;
                double dx = baseDx + spread * baseDz * 0.2;
                double dz = baseDz - spread * baseDx * 0.2;
                SmallFireball fireball = new SmallFireball(this.boss.level(), (LivingEntity)this.boss, dx, dy, dz);
                fireball.access$400(this.boss.getX(), this.boss.lambda$move$0(2.4), this.boss.getZ());
                this.boss.level().addFreshEntity((Entity)fireball);
            }
            this.boss.level().gameEvent(null, this.boss.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.5f, 0.8f);
        }
    }
}

