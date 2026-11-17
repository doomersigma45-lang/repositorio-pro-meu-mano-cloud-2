package com.netheritesupremo.parasite;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

/** Pulo de ataque: de 3 a 6 blocos do alvo (com linha de visao) o parasita salta nele e morde ao encostar. */
public class ParasiteLeapGoal extends Goal {
    private final Mob mob;
    private final double power;
    private final int cooldown;
    private LivingEntity target;
    private int wait;
    private int ticks;
    private boolean leaping;
    private boolean hit;

    public ParasiteLeapGoal(Mob mob, double power, int cooldown) {
        this.mob = mob;
        this.power = power;
        this.cooldown = cooldown;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        if (this.wait > 0) {
            this.wait--;
            return false;
        }
        this.target = this.mob.getTarget();
        if (this.target == null || !this.target.isAlive() || !this.mob.onGround()) {
            return false;
        }
        double d = this.mob.distanceToSqr(this.target);
        return d >= 9.0D && d <= 36.0D && this.mob.hasLineOfSight(this.target);
    }

    @Override
    public boolean canContinueToUse() {
        return this.leaping && this.ticks < 25 && (this.ticks < 4 || !this.mob.onGround());
    }

    @Override
    public void start() {
        Vec3 dir = new Vec3(this.target.getX() - this.mob.getX(), 0.0D, this.target.getZ() - this.mob.getZ()).normalize();
        this.mob.setDeltaMovement(dir.x * this.power, 0.42D, dir.z * this.power);
        this.mob.getNavigation().stop();
        this.leaping = true;
        this.hit = false;
        this.ticks = 0;
        this.wait = this.cooldown;
    }

    @Override
    public void tick() {
        this.ticks++;
        if (!this.hit && this.target != null && this.target.isAlive()
                && this.mob.getBoundingBox().inflate(0.6D).intersects(this.target.getBoundingBox())) {
            this.mob.doHurtTarget(this.target);
            this.hit = true;
        }
    }

    @Override
    public void stop() {
        this.leaping = false;
    }
}
