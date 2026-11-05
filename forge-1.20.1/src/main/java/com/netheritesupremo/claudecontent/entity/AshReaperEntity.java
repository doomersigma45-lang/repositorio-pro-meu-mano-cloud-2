package com.netheritesupremo.claudecontent.entity;

import com.netheritesupremo.claudecontent.registry.ModEntities;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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

import java.util.EnumSet;
import java.util.UUID;

/**
 * BOSS: Ceifador de Cinzas.
 *
 * Fase 1 (100% a 50% de vida): ataque corpo a corpo que incendeia, bolas de fogo e golpe no chão.
 * Fase 2 (abaixo de 50%): fica mais rápido, dispara 3 bolas de fogo por vez, golpe no chão mais
 * frequente e invoca Espreitadores de Brasa.
 *
 * Proteção anti-one-shot: nenhum golpe causa mais de 40 de dano (exceto o vazio / /kill).
 */
public class AshReaperEntity extends Monster {

    private static final UUID ENRAGE_SPEED_UUID = UUID.fromString("5b6f1c4e-3a52-4b7a-9d57-0c1f2f6f7a11");
    private static final float MAX_DAMAGE_PER_HIT = 40.0F;
    private static final double SLAM_RADIUS = 6.0D;

    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(
            this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10)
            .setDarkenScreen(true);

    private boolean enraged = false;
    private int slamCooldown = 100;
    private int summonCooldown = 160;

    public AshReaperEntity(EntityType<? extends AshReaperEntity> type, Level level) {
        super(type, level);
        this.xpReward = 250;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AshFireballGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 32.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public boolean isEnraged() {
        return this.enraged;
    }

    // ---------------------------------------------------------------- IA do servidor

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        if (!this.enraged && this.getHealth() <= this.getMaxHealth() * 0.5F) {
            enrage(true);
        }

        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }

        if (this.slamCooldown > 0) {
            this.slamCooldown--;
        } else if (this.distanceToSqr(target) < SLAM_RADIUS * SLAM_RADIUS && this.onGround()) {
            groundSlam();
            this.slamCooldown = this.enraged ? 100 : 180;
        }

        if (this.enraged) {
            if (this.summonCooldown > 0) {
                this.summonCooldown--;
            } else {
                summonStalkers(target);
                this.summonCooldown = 300;
            }
        }
    }

    private void enrage(boolean withEffects) {
        this.enraged = true;
        var speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && speed.getModifier(ENRAGE_SPEED_UUID) == null) {
            speed.addTransientModifier(new AttributeModifier(
                    ENRAGE_SPEED_UUID, "ash_reaper_enrage", 0.3D, AttributeModifier.Operation.MULTIPLY_BASE));
        }
        if (withEffects) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.WITHER_AMBIENT, SoundSource.HOSTILE, 2.0F, 0.5F);
            if (this.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 1.5D, this.getZ(),
                        50, 1.0D, 1.5D, 1.0D, 0.1D);
            }
        }
        this.bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
    }

    private void groundSlam() {
        if (!(this.level() instanceof ServerLevel server)) {
            return;
        }
        server.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.5F, 0.7F);
        server.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.2D, this.getZ(),
                6, SLAM_RADIUS / 3.0D, 0.1D, SLAM_RADIUS / 3.0D, 0.0D);
        server.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY() + 0.2D, this.getZ(),
                60, SLAM_RADIUS / 2.0D, 0.1D, SLAM_RADIUS / 2.0D, 0.08D);

        for (Player player : server.getEntitiesOfClass(Player.class,
                this.getBoundingBox().inflate(SLAM_RADIUS, 2.0D, SLAM_RADIUS))) {
            if (player.isCreative() || player.isSpectator() || !player.onGround()) {
                continue; // quem está pulando desvia do golpe
            }
            player.hurt(this.damageSources().mobAttack(this), this.enraged ? 14.0F : 10.0F);
            player.setSecondsOnFire(3);
            Vec3 push = player.position().subtract(this.position()).normalize().scale(0.8D);
            player.setDeltaMovement(push.x, 0.6D, push.z);
            player.hurtMarked = true;
        }
    }

    private void summonStalkers(LivingEntity target) {
        if (!(this.level() instanceof ServerLevel server)) {
            return;
        }
        int existing = server.getEntitiesOfClass(CinderStalkerEntity.class, this.getBoundingBox().inflate(24.0D)).size();
        int toSpawn = Math.min(2, 4 - existing);
        for (int i = 0; i < toSpawn; i++) {
            CinderStalkerEntity stalker = ModEntities.CINDER_STALKER.get().create(server);
            if (stalker == null) {
                continue;
            }
            double angle = this.random.nextDouble() * Math.PI * 2.0D;
            stalker.moveTo(this.getX() + Math.cos(angle) * 3.0D, this.getY(), this.getZ() + Math.sin(angle) * 3.0D,
                    this.random.nextFloat() * 360.0F, 0.0F);
            if (!server.noCollision(stalker)) {
                stalker.discard();
                continue;
            }
            stalker.setTarget(target);
            server.addFreshEntity(stalker);
            server.sendParticles(ParticleTypes.LARGE_SMOKE, stalker.getX(), stalker.getY() + 0.5D, stalker.getZ(),
                    15, 0.4D, 0.4D, 0.4D, 0.02D);
        }
    }

    // ---------------------------------------------------------------- visual

    @Override
    public void aiStep() {
        if (this.level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(ParticleTypes.ASH,
                        this.getRandomX(0.6D), this.getRandomY(), this.getRandomZ(0.6D), 0.0D, -0.02D, 0.0D);
            }
            if (this.enraged || this.random.nextInt(4) == 0) {
                this.level().addParticle(ParticleTypes.FLAME,
                        this.getRandomX(0.5D), this.getY() + 2.6D, this.getRandomZ(0.5D), 0.0D, 0.02D, 0.0D);
            }
        }
        super.aiStep();
    }

    // ---------------------------------------------------------------- combate

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            target.setSecondsOnFire(5);
        }
        return hit;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            amount = Math.min(amount, MAX_DAMAGE_PER_HIT);
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    // ---------------------------------------------------------------- boss bar

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
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    // ---------------------------------------------------------------- NBT

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Enraged", this.enraged);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
        if (tag.getBoolean("Enraged")) {
            enrage(false);
        }
    }

    // ---------------------------------------------------------------- sons

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WITHER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.5F;
    }

    // ---------------------------------------------------------------- goal de bolas de fogo

    private static class AshFireballGoal extends Goal {
        private final AshReaperEntity boss;
        private int cooldown = 40;

        AshFireballGoal(AshReaperEntity boss) {
            this.boss = boss;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.boss.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            double distSqr = this.boss.distanceToSqr(target);
            return distSqr > 36.0D && distSqr < 24.0D * 24.0D && this.boss.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void tick() {
            LivingEntity target = this.boss.getTarget();
            if (target == null) {
                return;
            }
            this.boss.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (--this.cooldown > 0) {
                return;
            }
            this.cooldown = this.boss.isEnraged() ? 40 : 70;

            int count = this.boss.isEnraged() ? 3 : 1;
            double baseDx = target.getX() - this.boss.getX();
            double dy = target.getY(0.5D) - this.boss.getY(0.75D);
            double baseDz = target.getZ() - this.boss.getZ();
            for (int i = 0; i < count; i++) {
                double spread = (i - (count - 1) / 2.0D) * 0.35D;
                double dx = baseDx + spread * baseDz * 0.2D;
                double dz = baseDz - spread * baseDx * 0.2D;
                SmallFireball fireball = new SmallFireball(this.boss.level(), this.boss, dx, dy, dz);
                fireball.setPos(this.boss.getX(), this.boss.getY(2.4D), this.boss.getZ());
                this.boss.level().addFreshEntity(fireball);
            }
            this.boss.level().playSound(null, this.boss.blockPosition(),
                    SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.5F, 0.8F);
        }
    }
}
