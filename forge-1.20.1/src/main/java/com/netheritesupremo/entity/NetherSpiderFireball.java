package com.netheritesupremo.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

/** Bola de fogo da aranha do nether: nao acende blocos e nao acerta aranhas do nether. */
public class NetherSpiderFireball extends net.minecraft.world.entity.projectile.SmallFireball {
    public static final int FIRE_SECONDS = 5;
    private final float damage;

    public NetherSpiderFireball(Level level, LivingEntity shooter, double dx, double dy, double dz, float damage) {
        super(level, shooter, dx, dy, dz);
        this.damage = damage;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !(entity instanceof NetherSpider);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.level().isClientSide) {
            return;
        }
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        int previousFire = target.getRemainingFireTicks();
        target.setSecondsOnFire(FIRE_SECONDS);
        if (!target.hurt(this.damageSources().fireball(this, owner), damage)) {
            target.setRemainingFireTicks(previousFire);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        // Nao coloca fogo nos blocos.
    }
}
