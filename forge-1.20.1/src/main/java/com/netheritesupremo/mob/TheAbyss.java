package com.netheritesupremo.mob;

import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
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
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.netheritesupremo.registry.ModEntities;

/**
 * O Abismo (chefe secreto da Terra Negra): 5000 HP, 60 de dano, 8 blocos. Seis tentaculos de alcances
 * diferentes atacam separadamente (cada um escolhe o proprio alvo), invoca Crias do Abismo e muda de
 * comportamento em 3 fases conforme a vida cai (mais rapido, mais tentaculos ativos, pulso que puxa).
 */
public class TheAbyss extends Monster {
    public static final double MAX_HEALTH = 5000.0D;
    public static final double DAMAGE = 60.0D;
    public static final int TENTACLES = 6;
    public static final double[] REACH = {11.5D, 10.5D, 9.5D, 8.5D, 8.0D, 7.0D};
    public static final float TENTACLE_DAMAGE = 24.0F;
    private static final int[] TENTACLE_COOLDOWN = {40, 26, 14};
    private static final int[] SUMMON_COOLDOWN = {400, 300, 200};
    private static final int[] SUMMON_COUNT = {2, 3, 4};
    private static final double[] SPEED = {0.20D, 0.24D, 0.28D};

    private static final EntityDataAccessor<Boolean> DATA_DORMANT =
            SynchedEntityData.defineId(TheAbyss.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_STRIKE =
            SynchedEntityData.defineId(TheAbyss.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_AIM =
            SynchedEntityData.defineId(TheAbyss.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PHASE =
            SynchedEntityData.defineId(TheAbyss.class, EntityDataSerializers.INT);

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_12);
    private final int[] cooldown = new int[TENTACLES];
    private final int[] strikeLeft = new int[TENTACLES];
    private int summonCooldown = 200;
    private int pulseCooldown = 200;

    public TheAbyss(EntityType<? extends TheAbyss> type, Level level) {
        super(type, level);
        this.xpReward = 1000;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, SPEED[0])
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_DORMANT, false);
        this.entityData.define(DATA_STRIKE, 0);
        this.entityData.define(DATA_AIM, 0);
        this.entityData.define(DATA_PHASE, 1);
    }

    public boolean isDormant() {
        return this.entityData.get(DATA_DORMANT);
    }

    private void setDormant(boolean dormant) {
        this.entityData.set(DATA_DORMANT, dormant);
        this.setNoAi(dormant);
    }

    public boolean isStriking(int tentacle) {
        return (this.entityData.get(DATA_STRIKE) >> tentacle & 1) != 0;
    }

    /** Angulo do alvo do tentaculo relativo ao corpo, em graus (0..360). */
    public float aimDegrees(int tentacle) {
        return (this.entityData.get(DATA_AIM) >> (tentacle * 5) & 31) * 360.0F / 32.0F;
    }

    public int phase() {
        return this.entityData.get(DATA_PHASE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.5D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    private void wake() {
        if (!this.isDormant()) {
            return;
        }
        this.setDormant(false);
        this.playSound(SoundEvents.WARDEN_EMERGE, 3.0F, 0.5F);
        if (this.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.REVERSE_PORTAL, this.getX(), this.getY() + 4.0D, this.getZ(), 80, 2.0D, 3.0D, 2.0D, 0.2D);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isDormant() && !this.level().isClientSide) {
            this.wake();
        }
        return super.hurt(source, amount);
    }

    private boolean isVictim(LivingEntity e) {
        if (e == this || !e.isAlive() || e instanceof TheAbyss || e instanceof AbyssSpawn || e instanceof ArmorStand) {
            return false;
        }
        return !(e instanceof Player p) || (!p.isCreative() && !p.isSpectator());
    }

    private int computePhase() {
        float ratio = this.getHealth() / this.getMaxHealth();
        return ratio > 0.66F ? 1 : ratio > 0.33F ? 2 : 3;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (this.isDormant()) {
            if (this.tickCount % 10 == 0) {
                Player p = level.getNearestPlayer(this, 24.0D);
                if (p != null && !p.isCreative() && !p.isSpectator()) {
                    this.wake();
                }
            }
            return;
        }
        int phase = this.computePhase();
        if (phase != this.phase()) {
            this.entityData.set(DATA_PHASE, phase);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(SPEED[phase - 1]);
            this.playSound(SoundEvents.WARDEN_ROAR, 3.0F, 0.4F);
            level.sendParticles(ParticleTypes.REVERSE_PORTAL, this.getX(), this.getY() + 4.0D, this.getZ(), 120, 2.5D, 3.5D, 2.5D, 0.3D);
        }
        this.tickTentacles(level, phase);
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        if (--this.summonCooldown <= 0) {
            this.summonCooldown = SUMMON_COOLDOWN[phase - 1];
            this.summon(level, target, SUMMON_COUNT[phase - 1]);
        }
        if (phase >= 2 && --this.pulseCooldown <= 0) {
            this.pulseCooldown = phase == 2 ? 200 : 120;
            this.pulse(level, phase);
        }
    }

    private void tickTentacles(ServerLevel level, int phase) {
        int strike = 0;
        int aim = this.entityData.get(DATA_AIM);
        List<LivingEntity> near = level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(REACH[0], 6.0D, REACH[0]), this::isVictim);
        for (int i = 0; i < TENTACLES; i++) {
            if (this.cooldown[i] > 0) {
                this.cooldown[i]--;
            }
            if (this.strikeLeft[i] > 0) {
                this.strikeLeft[i]--;
                strike |= 1 << i;
                continue;
            }
            if (this.cooldown[i] > 0 || near.isEmpty()) {
                continue;
            }
            final int idx = i;
            List<LivingEntity> reachable = near.stream()
                    .filter(e -> Math.sqrt(this.distanceToSqr(e.getX(), this.getY(), e.getZ())) <= REACH[idx])
                    .sorted(Comparator.comparingDouble(e -> this.distanceToSqr(e)))
                    .toList();
            if (reachable.isEmpty()) {
                continue;
            }
            LivingEntity victim = reachable.size() > 1 && this.random.nextInt(3) == 0 ? reachable.get(1) : reachable.get(0);
            victim.hurt(this.damageSources().mobAttack(this), TENTACLE_DAMAGE);
            Vec3 dir = victim.position().subtract(this.position()).multiply(1.0D, 0.0D, 1.0D).normalize();
            victim.setDeltaMovement(victim.getDeltaMovement().add(dir.x * 0.4D, 0.25D, dir.z * 0.4D));
            victim.hurtMarked = true;
            this.cooldown[i] = TENTACLE_COOLDOWN[phase - 1] + this.random.nextInt(10);
            this.strikeLeft[i] = 8;
            strike |= 1 << i;
            float angleToTarget = (float) (Mth.atan2(victim.getZ() - this.getZ(), victim.getX() - this.getX()) * 180.0D / Math.PI) - 90.0F;
            float rel = Mth.wrapDegrees(angleToTarget - this.yBodyRot);
            int q = Math.floorMod(Math.round(rel / 360.0F * 32.0F), 32);
            aim = (aim & ~(31 << (i * 5))) | (q << (i * 5));
            this.playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 1.5F, 0.6F);
        }
        this.entityData.set(DATA_STRIKE, strike);
        this.entityData.set(DATA_AIM, aim);
    }

    private void summon(ServerLevel level, LivingEntity target, int count) {
        int alive = level.getEntitiesOfClass(AbyssSpawn.class, this.getBoundingBox().inflate(40.0D)).size();
        for (int i = 0; i < count && alive < 12; i++, alive++) {
            AbyssSpawn s = ModEntities.ABYSS_SPAWN.get().create(level);
            if (s == null) {
                return;
            }
            s.moveTo(this.getX() + this.random.nextInt(9) - 4, this.getY(), this.getZ() + this.random.nextInt(9) - 4,
                    this.random.nextFloat() * 360.0F, 0.0F);
            s.setTarget(target);
            level.addFreshEntity(s);
            level.sendParticles(ParticleTypes.PORTAL, s.getX(), s.getY() + 0.3D, s.getZ(), 12, 0.3D, 0.3D, 0.3D, 0.3D);
        }
    }

    /** Pulso: puxa todos para perto, escurece a visao e (fase 3) machuca. */
    private void pulse(ServerLevel level, int phase) {
        this.playSound(SoundEvents.WARDEN_SONIC_BOOM, 2.0F, 0.5F);
        level.sendParticles(ParticleTypes.SCULK_SOUL, this.getX(), this.getY() + 4.0D, this.getZ(), 60, 4.0D, 3.0D, 4.0D, 0.1D);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(20.0D, 8.0D, 20.0D), this::isVictim)) {
            Vec3 pull = this.position().subtract(e.position()).multiply(1.0D, 0.0D, 1.0D).normalize();
            e.setDeltaMovement(pull.x * 0.9D, 0.3D, pull.z * 0.9D);
            e.hurtMarked = true;
            e.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0));
            if (phase >= 3) {
                e.hurt(this.damageSources().mobAttack(this), 10.0F);
            }
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (this.level() instanceof ServerLevel level) {
            for (AbyssSpawn s : level.getEntitiesOfClass(AbyssSpawn.class, this.getBoundingBox().inflate(48.0D))) {
                s.discard();
            }
        }
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
        this.entityData.set(DATA_PHASE, this.computePhase());
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(SPEED[this.phase() - 1]);
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
