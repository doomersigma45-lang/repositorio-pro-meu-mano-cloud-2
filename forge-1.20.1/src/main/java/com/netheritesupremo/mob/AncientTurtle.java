package com.netheritesupremo.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

/**
 * Tartaruga Ancestral: neutra, 300 HP, 20 de dano (cabecada). Nunca ataca primeiro; quando ferida se
 * esconde no casco por 3 s recebendo 80% menos dano, depois revida.
 */
public class AncientTurtle extends Turtle {
    public static final double MAX_HEALTH = 300.0D;
    public static final double DAMAGE = 20.0D;
    public static final int HIDE_TICKS = 60;
    public static final int HIDE_COOLDOWN = 100;

    private static final EntityDataAccessor<Boolean> DATA_HIDING =
            SynchedEntityData.defineId(AncientTurtle.class, EntityDataSerializers.BOOLEAN);

    private int hideLeft;
    private int hideCooldown;

    public AncientTurtle(EntityType<? extends AncientTurtle> type, Level level) {
        super(type, level);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Turtle.createAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_HIDING, false);
    }

    public boolean isHiding() {
        return this.entityData.get(DATA_HIDING);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    protected boolean isImmobile() {
        return this.isHiding() || super.isImmobile();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide && !this.isHiding() && this.hideCooldown <= 0 && this.isAlive()) {
            this.hideLeft = HIDE_TICKS;
            this.entityData.set(DATA_HIDING, true);
            this.getNavigation().stop();
        }
        return hurt;
    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        super.actuallyHurt(source, this.isHiding() ? amount * 0.2F : amount);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.hideCooldown > 0) {
            this.hideCooldown--;
        }
        if (this.isHiding()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0D, 1.0D, 0.0D));
            if (--this.hideLeft <= 0) {
                this.entityData.set(DATA_HIDING, false);
                this.hideCooldown = HIDE_COOLDOWN;
            }
        }
    }

    /** Cabecada: empurra o alvo. */
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity le) {
            Vec3 dir = le.position().subtract(this.position()).multiply(1.0D, 0.0D, 1.0D).normalize();
            le.setDeltaMovement(dir.x * 1.2D, 0.4D, dir.z * 1.2D);
            le.hurtMarked = true;
        }
        return hit;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    /** Nasce dentro d'agua (rios e oceanos). */
    public static boolean checkSpawnRules(EntityType<AncientTurtle> type, ServerLevelAccessor level, MobSpawnType reason,
            BlockPos pos, RandomSource random) {
        return level.getFluidState(pos).is(FluidTags.WATER) && level.getFluidState(pos.above()).is(FluidTags.WATER);
    }
}
