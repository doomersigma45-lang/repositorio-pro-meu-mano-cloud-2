package com.netheritesupremo.skeleton;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.AbstractSkeleton;

/** Esqueletos vivem em grupos: quem esta longe dos outros anda ate o esqueleto mais proximo. */
public class SkeletonGroupGoal extends Goal {
    private final AbstractSkeleton mob;
    private final double speed;
    private AbstractSkeleton buddy;

    public SkeletonGroupGoal(AbstractSkeleton mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.isPassenger() || this.mob.getTarget() != null || this.mob.getRandom().nextInt(20) != 0) {
            return false;
        }
        AbstractSkeleton best = null;
        double bestD = Double.MAX_VALUE;
        for (AbstractSkeleton m : this.mob.level().getEntitiesOfClass(AbstractSkeleton.class,
                this.mob.getBoundingBox().inflate(24.0D, 8.0D, 24.0D),
                o -> o != this.mob && o.isAlive() && !SkeletonFaction.isHostile(o))) {
            double d = this.mob.distanceToSqr(m);
            if (d < 49.0D) {
                return false; // ja esta num grupo
            }
            if (d < bestD) {
                bestD = d;
                best = m;
            }
        }
        this.buddy = best;
        return best != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.buddy != null && this.buddy.isAlive() && this.mob.getTarget() == null
                && !this.mob.getNavigation().isDone() && this.mob.distanceToSqr(this.buddy) > 16.0D;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.buddy, this.speed);
    }

    @Override
    public void stop() {
        this.buddy = null;
        this.mob.getNavigation().stop();
    }
}
