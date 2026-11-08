package com.netheritesupremo.skeleton;

import com.netheritesupremo.registry.ModEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** Projetil da Old Gun: leve queda, dano aleatorio entre 50 e 150 e some ao bater. */
public class OldGunBullet extends ThrowableItemProjectile {
    public static final float MIN_DAMAGE = 50.0F;
    public static final float MAX_DAMAGE = 150.0F;

    public OldGunBullet(EntityType<? extends OldGunBullet> type, Level level) {
        super(type, level);
    }

    public OldGunBullet(Level level, LivingEntity owner) {
        super(ModEntities.OLD_GUN_BULLET.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.IRON_NUGGET;
    }

    @Override
    protected float getGravity() {
        return 0.015F;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount > 80) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        if (this.level().isClientSide || target == this.getOwner()) {
            return;
        }
        float damage = MIN_DAMAGE + this.random.nextFloat() * (MAX_DAMAGE - MIN_DAMAGE);
        target.hurt(this.damageSources().thrown(this, this.getOwner()), damage);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }
}
