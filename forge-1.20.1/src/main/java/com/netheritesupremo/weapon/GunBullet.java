package com.netheritesupremo.weapon;

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

/** Bala da Uzi e do AK: rapida, queda minima, dano definido pela arma (zera o tempo de invulnerabilidade para o tiro automatico). */
public class GunBullet extends ThrowableItemProjectile {
    private float damage = 4.0F;

    public GunBullet(EntityType<? extends GunBullet> type, Level level) {
        super(type, level);
    }

    public GunBullet(Level level, LivingEntity owner) {
        super(ModEntities.GUN_BULLET.get(), owner, level);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.IRON_NUGGET;
    }

    @Override
    protected float getGravity() {
        return 0.008F;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount > 50) {
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
        target.invulnerableTime = 0;
        target.hurt(this.damageSources().thrown(this, this.getOwner()), this.damage);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }
}
