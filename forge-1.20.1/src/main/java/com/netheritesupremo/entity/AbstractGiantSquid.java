package com.netheritesupremo.entity;

import java.util.EnumSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Base das lulas brilhantes gigantes: nadam atras do alvo e lancam tinta brilhante. */
public abstract class AbstractGiantSquid extends GlowSquid {
    protected AbstractGiantSquid(EntityType<? extends GlowSquid> type, Level level) {
        super(type, level);
    }

    /** Dano de cada bola de tinta. */
    public abstract float inkDamage();

    /** Nao se afoga fora da agua (so se arrasta). */
    @Override
    protected void handleAirSupply(int airSupply) {
        this.setAirSupply(this.getMaxAirSupply());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new InkGoal(this));
        this.goalSelector.addGoal(2, new ChaseGoal(this));
        this.goalSelector.addGoal(6, new DriftGoal(this));
    }

    public void shootInk(LivingEntity target, float spread) {
        GlowInkBall ball = new GlowInkBall(com.netheritesupremo.registry.ModEntities.GLOW_INK_BALL.get(), this.level());
        ball.setOwner(this);
        ball.setDamage(this.inkDamage());
        Vec3 from = this.position().add(0.0D, this.getBbHeight() * 0.5D, 0.0D);
        Vec3 to = target.getBoundingBox().getCenter().subtract(from);
        ball.setPos(from.x, from.y, from.z);
        ball.shoot(to.x, to.y, to.z, 1.8F, spread);
        this.level().addFreshEntity(ball);
        this.level().playSound(null, this.blockPosition(), SoundEvents.GLOW_SQUID_SQUIRT, SoundSource.HOSTILE, 2.0F, 0.8F);
    }

    /** Lanca tinta no alvo; de tempos em tempos, uma rajada em leque. */
    static class InkGoal extends Goal {
        private final AbstractGiantSquid squid;
        private int nextShot = 40;
        private int shots;

        InkGoal(AbstractGiantSquid squid) {
            this.squid = squid;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.squid.getTarget();
            return target != null && target.isAlive() && this.squid.tickCount >= this.nextShot
                    && this.squid.distanceTo(target) < 32.0D && this.squid.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            LivingEntity target = this.squid.getTarget();
            if (target == null) {
                return;
            }
            this.shots++;
            this.nextShot = this.squid.tickCount + 25;
            if (this.shots % 6 == 0) {
                for (int i = 0; i < 5; i++) {
                    this.squid.shootInk(target, 6.0F);
                }
            } else {
                this.squid.shootInk(target, 1.0F);
            }
        }
    }

    /** Nada ate a distancia de tiro e ataca com os tentaculos de perto. */
    static class ChaseGoal extends Goal {
        private final AbstractGiantSquid squid;
        private int attackCooldown;

        ChaseGoal(AbstractGiantSquid squid) {
            this.squid = squid;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.squid.getTarget();
            return target != null && target.isAlive() && !this.squid.isVehicle();
        }

        @Override
        public void tick() {
            LivingEntity target = this.squid.getTarget();
            if (target == null) {
                return;
            }
            Vec3 to = target.getBoundingBox().getCenter().subtract(this.squid.position().add(0.0D, this.squid.getBbHeight() * 0.5D, 0.0D));
            double dist = to.length();
            double keep = this.squid.getBbWidth() * 2.5D;
            Vec3 dir = dist > 1.0E-3D ? to.scale(1.0D / dist) : Vec3.ZERO;
            if (dist > keep) {
                this.squid.setMovementVector((float) (dir.x * 0.45D), (float) (dir.y * 0.45D), (float) (dir.z * 0.45D));
            } else if (dist < keep * 0.5D) {
                this.squid.setMovementVector((float) (-dir.x * 0.2D), (float) (-dir.y * 0.2D), (float) (-dir.z * 0.2D));
            }
            if (--this.attackCooldown <= 0 && dist < this.squid.getBbWidth() * 1.3D + 1.0D) {
                this.attackCooldown = 20;
                this.squid.doHurtTarget(target);
            }
        }
    }

    /** Sem alvo (e sem cavaleiro): nada devagar em direcoes aleatorias. */
    static class DriftGoal extends Goal {
        private final AbstractGiantSquid squid;

        DriftGoal(AbstractGiantSquid squid) {
            this.squid = squid;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.squid.getTarget() == null && !this.squid.isVehicle();
        }

        @Override
        public void tick() {
            if (this.squid.getRandom().nextInt(60) == 0 || this.squid.getNoActionTime() > 100) {
                float angle = this.squid.getRandom().nextFloat() * (float) (Math.PI * 2.0D);
                this.squid.setMovementVector(net.minecraft.util.Mth.cos(angle) * 0.2F,
                        -0.1F + this.squid.getRandom().nextFloat() * 0.2F, net.minecraft.util.Mth.sin(angle) * 0.2F);
            }
        }
    }
}
