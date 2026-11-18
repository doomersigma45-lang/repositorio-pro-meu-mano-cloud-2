package com.netheritesupremo.weapon;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

/** Mob (zumbi, esqueleto) segurando Uzi, AK ou RPG: fica a media distancia do alvo e atira. */
public class MobGunGoal extends Goal {
    private final Mob mob;
    private int cooldown = 20;

    public MobGunGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        LivingEntity t = this.mob.getTarget();
        if (t == null || !t.isAlive() || !MobGuns.isNewGun(this.mob.getMainHandItem())) {
            return false;
        }
        double d = this.mob.distanceToSqr(t);
        return d >= 25.0D && d <= 30.0D * 30.0D && this.mob.getSensing().hasLineOfSight(t);
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        this.mob.setAggressive(true);
    }

    @Override
    public void stop() {
        this.mob.setAggressive(false);
    }

    @Override
    public void tick() {
        LivingEntity t = this.mob.getTarget();
        if (t == null) {
            return;
        }
        this.mob.getLookControl().setLookAt(t, 30.0F, 30.0F);
        if (this.mob.distanceToSqr(t) > 22.0D * 22.0D) {
            this.mob.getNavigation().moveTo(t, 1.0D);
        } else {
            this.mob.getNavigation().stop();
        }
        if (this.cooldown > 0) {
            this.cooldown--;
        } else {
            this.cooldown = MobGuns.fire(this.mob, t, this.mob.getMainHandItem());
        }
    }
}
