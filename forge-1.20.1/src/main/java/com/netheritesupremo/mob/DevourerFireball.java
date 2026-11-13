package com.netheritesupremo.mob;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;

/** Bola de fogo do Devorador do Nether: nao acerta ele nem os guardioes de pedra. */
public class DevourerFireball extends LargeFireball {
    public DevourerFireball(Level level, LivingEntity shooter, double dx, double dy, double dz, int power) {
        super(level, shooter, dx, dy, dz, power);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !(entity instanceof StoneGuardian) && !(entity instanceof AncientGuardian);
    }
}
