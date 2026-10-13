package com.netheritesupremo.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

/**
 * Bola de fogo do golem de netherite. E uma SmallFireball (o cliente a desenha como a
 * bola de fogo normal), mas com dano proprio, sem acender blocos (nao queima aldeias) e sem
 * acertar golens nem aldeoes.
 */
public class GolemFireball extends SmallFireball {
    public static final float DAMAGE = 40.0F;
    public static final int FIRE_SECONDS = 8;

    public GolemFireball(Level level, LivingEntity shooter, double dx, double dy, double dz) {
        super(level, shooter, dx, dy, dz);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !(entity instanceof IronGolem) && !(entity instanceof AbstractVillager);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.level.isClientSide) {
            return;
        }
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        int previousFire = target.getRemainingFireTicks();
        target.setSecondsOnFire(FIRE_SECONDS);
        if (!target.hurt(DamageSource.fireball(this, owner), DAMAGE)) {
            target.setRemainingFireTicks(previousFire);
        } else if (owner instanceof LivingEntity livingOwner) {
            this.doEnchantDamageEffects(livingOwner, target);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        // Propositalmente vazio: nao coloca fogo nos blocos.
    }
}
