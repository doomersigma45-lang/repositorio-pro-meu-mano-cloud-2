package com.netheritesupremo.coven;

import com.netheritesupremo.parasite.ParasiteLeapGoal;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

/**
 * Criaturas do covil (inspiradas no Bewitchment): Imp, Lobisomem, Pessoa-Sombra, Fantasma, Demonio (mini boss),
 * Cambion e Cobra. Uma classe so; o comportamento muda conforme o {@link Kind}.
 */
public class CovenMob extends Monster {
    public enum Kind {
        IMP(14, 3, 0.34D, 0.6F, true, false, 140),
        WEREWOLF(60, 9, 0.36D, 1.1F, false, false, 0),
        SHADOW_PERSON(26, 5, 0.3D, 1.0F, false, false, 0),
        GHOST(22, 4, 0.25D, 1.0F, false, false, 0),
        DEMON(260, 15, 0.3D, 1.5F, true, true, 70),
        CAMBION(70, 8, 0.3D, 1.1F, true, false, 100),
        SNAKE(8, 2, 0.28D, 1.0F, false, false, 0);

        public final double health;
        public final double damage;
        public final double speed;
        public final float scale;
        public final boolean fireImmune;
        public final boolean boss;
        public final int fireInterval;

        Kind(double health, double damage, double speed, float scale, boolean fireImmune, boolean boss, int fireInterval) {
            this.health = health;
            this.damage = damage;
            this.speed = speed;
            this.scale = scale;
            this.fireImmune = fireImmune;
            this.boss = boss;
            this.fireInterval = fireInterval;
        }
    }

    private final Kind kind;
    @Nullable
    private final ServerBossEvent bossEvent;
    private boolean enraged;
    private int summonCooldown = 200;

    public CovenMob(EntityType<? extends CovenMob> type, Level level, Kind kind) {
        super(type, level);
        this.kind = kind;
        this.xpReward = kind.boss ? 120 : 10;
        if (kind == Kind.GHOST) {
            this.moveControl = new FlyingMoveControl(this, 20, true);
            this.navigation = new FlyingPathNavigation(this, level);
            this.setNoGravity(true);
        }
        this.bossEvent = kind.boss ? new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED,
                BossEvent.BossBarOverlay.NOTCHED_10) : null;
        if (kind.boss) {
            this.setPersistenceRequired();
        }
    }

    public Kind kind() {
        return this.kind;
    }

    public static AttributeSupplier.Builder createAttributes(Kind kind) {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, kind.health)
                .add(Attributes.ATTACK_DAMAGE, kind.damage)
                .add(Attributes.MOVEMENT_SPEED, kind.speed)
                .add(Attributes.FLYING_SPEED, kind.speed)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, kind.boss ? 0.7D : 0.0D)
                .add(Attributes.ARMOR, kind.boss ? 6.0D : 0.0D);
    }

    @Override
    protected void registerGoals() {
        // registerGoals roda dentro do construtor da superclasse, quando "kind" ainda nao existe: decide pelo tipo.
        boolean ghost = this.getType() == Coven.GHOST.get();
        boolean snake = this.getType() == Coven.SNAKE.get();
        boolean wolf = this.getType() == Coven.WEREWOLF.get();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        if (wolf) {
            this.goalSelector.addGoal(2, new ParasiteLeapGoal(this, 0.7D, 50));
        }
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, false));
        if (ghost) {
            this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        } else {
            this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        }
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        if (!snake) {
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        }
    }

    @Override
    public MobType getMobType() {
        return this.kind != null && this.kind == Kind.GHOST ? MobType.UNDEAD : MobType.UNDEFINED;
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return this.kind != Kind.GHOST && super.causeFallDamage(distance, multiplier, source);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.kind == Kind.GHOST && source.is(DamageTypeTags.IS_PROJECTILE)) {
            return false;
        }
        if (this.kind == Kind.SHADOW_PERSON && !this.level().isClientSide && source.getEntity() != null
                && this.random.nextInt(3) == 0) {
            this.teleportRandomly();
        }
        return super.hurt(source, amount);
    }

    private void teleportRandomly() {
        for (int i = 0; i < 8; i++) {
            double x = this.getX() + (this.random.nextDouble() - 0.5D) * 16.0D;
            double z = this.getZ() + (this.random.nextDouble() - 0.5D) * 16.0D;
            if (this.randomTeleport(x, this.getY(), z, true)) {
                return;
            }
        }
    }

    private static void curse(LivingEntity target, RegistryObject<MobEffect> effect, int ticks) {
        target.addEffect(new MobEffectInstance(effect.get(), ticks, 0));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity victim) {
            switch (this.kind) {
                case IMP -> {
                    victim.setSecondsOnFire(4);
                    curse(victim, Coven.CURSE_OVERHEATING, 200);
                }
                case WEREWOLF -> {
                    victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
                    this.heal(2.0F);
                }
                case SHADOW_PERSON -> curse(victim, Coven.CURSE_DARKNESS, 300);
                case GHOST -> {
                    curse(victim, Coven.CURSE_PARANOIA, 300);
                    victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
                }
                case DEMON -> {
                    victim.setSecondsOnFire(6);
                    curse(victim, Coven.CURSE_OVERHEATING, 400);
                }
                case CAMBION -> {
                    victim.setSecondsOnFire(3);
                    curse(victim, Coven.CURSE_SOLAR_HATRED, 400);
                }
                case SNAKE -> victim.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
            }
        }
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel level)) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (this.bossEvent != null) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            if (!this.enraged && this.getHealth() <= this.getMaxHealth() / 2.0F) {
                this.enraged = true;
                AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speed != null) {
                    speed.setBaseValue(speed.getBaseValue() * 1.25D);
                }
                this.bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
            }
        }
        if (target == null || !target.isAlive()) {
            return;
        }
        if (this.kind.fireInterval > 0 && this.tickCount % this.kind.fireInterval == 0
                && this.distanceToSqr(target) < 24.0D * 24.0D && this.hasLineOfSight(target)) {
            double dx = target.getX() - this.getX();
            double dy = target.getY(0.5D) - this.getY(0.6D);
            double dz = target.getZ() - this.getZ();
            SmallFireball ball = new SmallFireball(level, this, dx, dy, dz);
            ball.setPos(this.getX(), this.getY(0.6D), this.getZ());
            level.addFreshEntity(ball);
            this.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 1.0F);
        }
        if (this.kind == Kind.SHADOW_PERSON && this.tickCount % 100 == 0) {
            double d = this.distanceTo(target);
            if (d > 4.0D && d < 18.0D) {
                var look = target.getLookAngle();
                this.randomTeleport(target.getX() - look.x * 2.5D, target.getY(), target.getZ() - look.z * 2.5D, true);
            }
        }
        if (this.kind == Kind.DEMON && --this.summonCooldown <= 0) {
            this.summonCooldown = 400;
            int imps = level.getEntitiesOfClass(CovenMob.class, this.getBoundingBox().inflate(24.0D),
                    e -> e.kind == Kind.IMP).size();
            for (int i = 0; i < 2 && imps < 6; i++, imps++) {
                CovenMob imp = Coven.IMP.get().create(level);
                if (imp == null) {
                    continue;
                }
                imp.moveTo(this.getX() + this.random.nextInt(5) - 2, this.getY(), this.getZ() + this.random.nextInt(5) - 2,
                        this.random.nextFloat() * 360.0F, 0.0F);
                level.addFreshEntity(imp);
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return !this.kind.boss && super.removeWhenFarAway(distance);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.kind == null) {
            return null;
        }
        return switch (this.kind) {
            case IMP -> SoundEvents.VEX_AMBIENT;
            case WEREWOLF -> SoundEvents.WOLF_GROWL;
            case SHADOW_PERSON -> SoundEvents.ENDERMAN_AMBIENT;
            case GHOST -> SoundEvents.PHANTOM_AMBIENT;
            case DEMON -> SoundEvents.RAVAGER_AMBIENT;
            case CAMBION -> SoundEvents.PIGLIN_BRUTE_AMBIENT;
            case SNAKE -> SoundEvents.SPIDER_AMBIENT;
        };
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (this.bossEvent != null) {
            this.bossEvent.addPlayer(player);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (this.bossEvent != null) {
            this.bossEvent.removePlayer(player);
        }
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        if (this.bossEvent != null) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }
}
