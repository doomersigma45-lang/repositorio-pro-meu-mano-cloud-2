package com.netheritesupremo.skeleton;

import com.netheritesupremo.registry.ModItems;
import java.util.EnumSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

/** Ataque a distancia com a Old Gun: mira, atira e recarrega por ~5 s. Perto demais, quem tem melee assume. */
public class GunAttackGoal extends Goal {
    private final Mob mob;
    private final double speed;
    private final double minDist;
    private final double maxDist;
    private int cooldown = 30;

    public GunAttackGoal(Mob mob, double speed, double minDist, double maxDist) {
        this.mob = mob;
        this.speed = speed;
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = this.mob.getTarget();
        return t != null && t.isAlive() && this.mob.getMainHandItem().is(ModItems.OLD_GUN.get())
                && this.mob.distanceToSqr(t) >= this.minDist * this.minDist;
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
        this.mob.getNavigation().stop();
        this.cooldown = Math.max(this.cooldown, 30);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity t = this.mob.getTarget();
        if (t == null) {
            return;
        }
        double d2 = this.mob.distanceToSqr(t);
        boolean sees = this.mob.getSensing().hasLineOfSight(t);
        this.mob.getLookControl().setLookAt(t, 30.0F, 30.0F);
        if (d2 > this.maxDist * this.maxDist * 0.7D || !sees) {
            this.mob.getNavigation().moveTo(t, this.speed);
        } else {
            this.mob.getNavigation().stop();
        }
        if (this.cooldown > 0) {
            this.cooldown--;
        }
        if (this.cooldown <= 0 && sees && d2 <= this.maxDist * this.maxDist) {
            SkeletonFaction.fireGun(this.mob, t);
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.cooldown = OldGunItem.RELOAD_TICKS + this.mob.getRandom().nextInt(30);
        }
    }
}
