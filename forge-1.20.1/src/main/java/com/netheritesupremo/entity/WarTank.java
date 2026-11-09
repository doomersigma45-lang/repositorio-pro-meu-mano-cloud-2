package com.netheritesupremo.entity;

import com.netheritesupremo.skeleton.SkeletonWar;
import java.util.EnumSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Tanque de guerra dos esqueletos: 270 de vida, atira obuses explosivos de 750 de dano. Aparece nas guerras
 * dos esqueletos (com um esqueleto de tripulante) e ataca jogadores e os moradores das vilas.
 */
public class WarTank extends PathfinderMob {
    public static final String CREW_TAG = "netheritesupremo_tank_crew";
    public static final int FIRE_INTERVAL = 70;
    public static final double APPROACH_DISTANCE = 18.0D;

    public WarTank(EntityType<? extends WarTank> type, Level level) {
        super(type, level);
        this.xpReward = 60;
        this.setMaxUpStep(1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 270.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 44.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new TankAttackGoal(this, 1.0D, 18.0D, 36.0D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 2, false, false,
                e -> (e instanceof Player p && !p.isCreative() && !p.isSpectator()) || SkeletonWar.isDefender(e)));
    }

    /** O tanque avanca em linha reta ate o alvo (sem depender da navegacao, que trava com tripulante) e para a distancia de tiro. */
    @Override
    public void aiStep() {
        LivingEntity t = this.getTarget();
        if (!this.level().isClientSide && t != null && t.isAlive() && this.isAlive()) {
            double dx = t.getX() - this.getX();
            double dz = t.getZ() - this.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            Vec3 m = this.getDeltaMovement();
            if (dist > APPROACH_DISTANCE || !this.getSensing().hasLineOfSight(t)) {
                if (dist > 3.0D) {
                    double speed = this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.75D;
                    this.setDeltaMovement(dx / dist * speed, m.y, dz / dist * speed);
                    float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
                    this.setYRot(yaw);
                    this.yBodyRot = yaw;
                }
            } else {
                this.setDeltaMovement(m.x * 0.5D, m.y, m.z * 0.5D);
            }
        }
        super.aiStep();
    }

    @Override
    public double getPassengersRidingOffset() {
        return 1.55D;
    }

    @Override
    protected boolean canAddPassenger(net.minecraft.world.entity.Entity passenger) {
        return this.getPassengers().isEmpty();
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distanceSqr) {
        return false;
    }

    /** O tripulante volta a ter IA quando o tanque e destruido. */
    @Override
    public void die(DamageSource source) {
        for (net.minecraft.world.entity.Entity passenger : this.getPassengers()) {
            if (passenger instanceof Mob crew) {
                crew.setNoAi(false);
                crew.removeTag(CREW_TAG);
            }
        }
        super.die(source);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_EXPLODE;
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 0.8F, 0.6F);
    }

    /** Dispara o obus na direcao do alvo. */
    public void fireShell(LivingEntity target) {
        if (this.level().isClientSide) {
            return;
        }
        TankShell shell = new TankShell(this.level(), this);
        Vec3 from = new Vec3(this.getX(), this.getY() + 1.75D, this.getZ());
        double dx = target.getX() - from.x;
        double dy = target.getEyeY() - from.y;
        double dz = target.getZ() - from.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        Vec3 dir = new Vec3(dx, 0.0D, dz).normalize();
        shell.setPos(from.x + dir.x * 2.2D, from.y, from.z + dir.z * 2.2D);
        shell.shoot(dx, dy + horizontal * 0.05D, dz, 2.0F, 0.6F);
        this.level().addFreshEntity(shell);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE,
                SoundSource.HOSTILE, 2.0F, 1.6F);
    }

    /** Avanca ate ficar a uma boa distancia e bombardeia o alvo. */
    static class TankAttackGoal extends Goal {
        private final WarTank tank;
        private final double speed;
        private final double near;
        private final double far;
        private int cooldown = 40;

        TankAttackGoal(WarTank tank, double speed, double near, double far) {
            this.tank = tank;
            this.speed = speed;
            this.near = near;
            this.far = far;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity t = this.tank.getTarget();
            return t != null && t.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity t = this.tank.getTarget();
            if (t == null) {
                return;
            }
            double d = this.tank.distanceTo(t);
            boolean sees = this.tank.getSensing().hasLineOfSight(t);
            this.tank.getLookControl().setLookAt(t, 40.0F, 40.0F);
            if (this.cooldown > 0) {
                this.cooldown--;
            }
            if (this.cooldown <= 0 && sees && d <= this.far) {
                this.tank.fireShell(t);
                this.cooldown = FIRE_INTERVAL;
            }
        }
    }
}
