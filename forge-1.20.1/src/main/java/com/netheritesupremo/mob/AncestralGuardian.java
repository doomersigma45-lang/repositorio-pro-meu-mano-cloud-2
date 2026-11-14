package com.netheritesupremo.mob;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import com.netheritesupremo.registry.ModEntities;

/**
 * Guardiao Ancestral (boss): golem de pedra de 7 blocos, 1500 HP, 35 de dano. Dorme no Templo Ancestral
 * (subterraneo), acorda quando um jogador chega perto ou ele leva dano. Impacto em area, invoca
 * guardioes de pedra e recebe so 25% do dano de flechas e outros projeteis.
 */
public class AncestralGuardian extends Monster {
    public static final double MAX_HEALTH = 1500.0D;
    public static final double DAMAGE = 35.0D;
    public static final float SLAM_DAMAGE = 21.0F;
    public static final double SLAM_RADIUS = 7.0D;
    public static final int MAX_MINIONS = 8;

    private static final EntityDataAccessor<Boolean> DATA_DORMANT =
            SynchedEntityData.defineId(AncestralGuardian.class, EntityDataSerializers.BOOLEAN);

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
    private int slamCooldown = 80;
    private int summonCooldown = 160;

    public AncestralGuardian(EntityType<? extends AncestralGuardian> type, Level level) {
        super(type, level);
        this.xpReward = 500;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DORMANT, false);
    }

    public boolean isDormant() {
        return this.entityData.get(DATA_DORMANT);
    }

    private void setDormant(boolean dormant) {
        this.entityData.set(DATA_DORMANT, dormant);
        this.setNoAi(dormant);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
            @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        if (reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION || reason == MobSpawnType.STRUCTURE) {
            this.setDormant(true);
        }
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
    }

    private void wake() {
        if (!this.isDormant()) {
            return;
        }
        this.setDormant(false);
        this.playSound(SoundEvents.RAVAGER_ROAR, 3.0F, 0.5F);
        if (this.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 2.0D, this.getZ(), 40, 1.5D, 2.0D, 1.5D, 0.1D);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isDormant() && !this.level().isClientSide) {
            this.wake();
        }
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            amount *= 0.25F;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (this.isDormant()) {
            if (this.tickCount % 10 == 0) {
                Player p = level.getNearestPlayer(this, 10.0D);
                if (p != null && !p.isCreative() && !p.isSpectator()) {
                    this.wake();
                }
            }
            return;
        }
        if (this.slamCooldown > 0) {
            this.slamCooldown--;
        }
        if (this.summonCooldown > 0) {
            this.summonCooldown--;
        }
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        if (this.slamCooldown <= 0 && this.distanceTo(target) < SLAM_RADIUS + 1.0D) {
            this.slamCooldown = 100;
            this.slam(level);
        }
        if (this.summonCooldown <= 0) {
            this.summonCooldown = 400;
            this.summonGuardians(level, target);
        }
    }

    private void slam(ServerLevel level) {
        this.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        this.playSound(SoundEvents.GENERIC_EXPLODE, 2.0F, 0.5F);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 0.5D, this.getZ(), 1, 0, 0, 0, 0);
        level.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 0.5D, this.getZ(), 30, SLAM_RADIUS / 2, 0.3D, SLAM_RADIUS / 2, 0.1D);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(SLAM_RADIUS, 2.0D, SLAM_RADIUS),
                x -> x != this && !(x instanceof StoneGuardian) && x.isAlive()
                        && !(x instanceof Player p && (p.isCreative() || p.isSpectator())))) {
            Vec3 dir = e.position().subtract(this.position()).multiply(1.0D, 0.0D, 1.0D).normalize();
            e.hurt(this.damageSources().mobAttack(this), SLAM_DAMAGE);
            e.setDeltaMovement(dir.x * 1.5D, 0.8D, dir.z * 1.5D);
            e.hurtMarked = true;
        }
    }

    private void summonGuardians(ServerLevel level, LivingEntity target) {
        int alive = level.getEntitiesOfClass(StoneGuardian.class, this.getBoundingBox().inflate(40.0D)).size();
        for (int i = 0; i < 3 && alive < MAX_MINIONS; i++, alive++) {
            StoneGuardian g = ModEntities.STONE_GUARDIAN.get().create(level);
            if (g == null) {
                return;
            }
            g.moveTo(this.getX() + this.random.nextInt(7) - 3, this.getY(), this.getZ() + this.random.nextInt(7) - 3,
                    this.random.nextFloat() * 360.0F, 0.0F);
            g.setTarget(target);
            level.addFreshEntity(g);
            level.sendParticles(ParticleTypes.POOF, g.getX(), g.getY() + 0.8D, g.getZ(), 10, 0.3D, 0.5D, 0.3D, 0.02D);
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (this.level() instanceof ServerLevel level) {
            for (StoneGuardian g : level.getEntitiesOfClass(StoneGuardian.class, this.getBoundingBox().inflate(48.0D))) {
                g.discard();
            }
        }
    }

    /** Sem spawn natural: ele fica dormindo dentro do Templo Ancestral (estrutura) ou vem de ovo/comando. */
    public static boolean checkSpawnRules(EntityType<AncestralGuardian> type, ServerLevelAccessor level, MobSpawnType reason,
            BlockPos pos, RandomSource random) {
        return reason != MobSpawnType.NATURAL && reason != MobSpawnType.CHUNK_GENERATION;
    }

    // ------------------------------------------------------------------ dados e barra de boss
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Dormant", this.isDormant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setDormant(tag.getBoolean("Dormant"));
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }
}
