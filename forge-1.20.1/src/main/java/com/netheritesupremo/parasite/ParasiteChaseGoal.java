package com.netheritesupremo.parasite;

import java.util.EnumSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

/** Persegue e morde o alvo. Usado por mobs sem versao parasita que receberam o efeito Parasitado. */
public class ParasiteChaseGoal extends Goal {
    private final Mob mob;
    private int cooldown;
    private int repath;

    public ParasiteChaseGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = this.mob.getTarget();
        return t != null && t.isAlive() && ParasiteBehavior.PREY.test(t);
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
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
        this.mob.getLookControl().setLookAt(t, 30.0F, 30.0F);
        if (--this.repath <= 0) {
            this.repath = 8;
            this.mob.getNavigation().moveTo(t, 1.15D);
        }
        if (this.cooldown > 0) {
            this.cooldown--;
        }
        double reach = Math.max(2.0D, this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + t.getBbWidth());
        if (this.cooldown <= 0 && this.mob.distanceToSqr(t) <= reach) {
            this.cooldown = 20;
            this.mob.swing(InteractionHand.MAIN_HAND);
            float dmg = this.mob.getAttribute(Attributes.ATTACK_DAMAGE) != null
                    ? (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE) : 3.0F;
            t.hurt(this.mob.damageSources().mobAttack(this.mob), dmg);
        }
    }
}
