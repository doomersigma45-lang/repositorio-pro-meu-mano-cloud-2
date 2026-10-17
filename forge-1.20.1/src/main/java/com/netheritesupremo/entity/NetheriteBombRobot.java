package com.netheritesupremo.entity;

import com.netheritesupremo.SupremoConfig;
import java.util.EnumSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;

/**
 * Bomb-robot de netherite: hostil, muito rapido e voador, solta projeteis explosivos e,
 * ao morrer, explode com forca 30 (10x a de um creeper, que e 3).
 */
public class NetheriteBombRobot extends FlyingRobot {
    public static final double MAX_HEALTH = 45.0D;
    public static final double ATTACK_DAMAGE = 15.0D;
    public static final float DEATH_EXPLOSION_POWER = 30.0F;
    public static final int PROJECTILE_EXPLOSION_POWER = 2;

    private boolean exploded;

    public NetheriteBombRobot(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder createBombRobotAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.55D)
                .add(Attributes.FLYING_SPEED, 1.2D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected ParticleOptions backFlame() {
        return ParticleTypes.SOUL_FIRE_FLAME; // chama azul
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new BombShootGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.4D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /** Explode ao morrer, seja qual for a causa. */
    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide && !this.exploded) {
            this.exploded = true;
            Level.ExplosionInteraction mode = SupremoConfig.BOMB_ROBOT_DESTROYS_BLOCKS.get()
                    ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE;
            this.level().explode(this, this.getX(), this.getY(0.5D), this.getZ(), DEATH_EXPLOSION_POWER, false, mode);
            this.discard();
        }
    }

    /** Dispara bolas explosivas contra o alvo a media distancia. */
    static class BombShootGoal extends Goal {
        private final NetheriteBombRobot robot;
        private int cooldown = 30;

        BombShootGoal(NetheriteBombRobot robot) {
            this.robot = robot;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.robot.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            double dist = this.robot.distanceTo(target);
            return dist > 5.0D && dist < 26.0D && this.robot.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void tick() {
            LivingEntity target = this.robot.getTarget();
            if (target == null) {
                return;
            }
            this.robot.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (--this.cooldown > 0) {
                return;
            }
            this.cooldown = 45 + this.robot.getRandom().nextInt(25);
            double dx = target.getX() - this.robot.getX();
            double dy = target.getY(0.5D) - this.robot.getY(0.5D);
            double dz = target.getZ() - this.robot.getZ();
            LargeFireball bomb = new LargeFireball(this.robot.level(), this.robot, dx, dy, dz, PROJECTILE_EXPLOSION_POWER);
            bomb.setPos(this.robot.getX(), this.robot.getY(0.5D) + 0.5D, this.robot.getZ());
            this.robot.level().addFreshEntity(bomb);
            this.robot.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 0.8F);
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.FALL) || super.isInvulnerableTo(source);
    }
}
