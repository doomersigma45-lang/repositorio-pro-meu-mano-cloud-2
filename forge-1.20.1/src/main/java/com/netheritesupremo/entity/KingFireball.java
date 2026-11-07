package com.netheritesupremo.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;

/** Bola de fogo do Rei Esqueleto: nao acerta ele nem seus aliados e nao destroi blocos. */
public class KingFireball extends LargeFireball {
    public KingFireball(Level level, LivingEntity shooter, double dx, double dy, double dz, int power) {
        super(level, shooter, dx, dy, dz, power);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !SkeletonKing.isKingSide(entity);
    }
}
