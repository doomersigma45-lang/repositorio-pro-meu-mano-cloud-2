package com.netheritesupremo.mob;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

/**
 * Perseguidor: aparece a noite (florestas), 120 HP, 12 de dano, persegue jogadores por longas distancias,
 * fica invisivel quando o alvo esta longe, nao atravessa agua e foge (some) quando amanhece.
 */
public class Stalker extends Monster {
    public static final double MAX_HEALTH = 120.0D;
    public static final double DAMAGE = 12.0D;
    public static final double INVISIBLE_DISTANCE = 24.0D;
    public static final int FLEE_TICKS = 300;

    private int dayTicks;

    public Stalker(EntityType<? extends Stalker> type, Level level) {
        super(type, level);
        this.xpReward = 40;
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.33D) // zumbi: 0.23
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, 24.0F, 1.2D, 1.6D, e -> this.level().isDay()));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false,
                p -> !this.level().isDay()));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (target != null && this.distanceTo(target) > INVISIBLE_DISTANCE) {
            this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60, 0, false, false));
        } else if (this.hasEffect(MobEffects.INVISIBILITY)) {
            this.removeEffect(MobEffects.INVISIBILITY);
        }
        if (this.level().isDay() && this.level().canSeeSky(this.blockPosition())) {
            this.setTarget(null);
            if (++this.dayTicks > FLEE_TICKS && this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 1.5D, this.getZ(), 20, 0.3D, 1.0D, 0.3D, 0.02D);
                this.discard();
            }
        } else {
            this.dayTicks = 0;
        }
    }
}
