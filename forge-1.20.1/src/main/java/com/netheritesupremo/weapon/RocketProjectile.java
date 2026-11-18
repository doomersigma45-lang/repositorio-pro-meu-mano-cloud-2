package com.netheritesupremo.weapon;

import com.netheritesupremo.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/** Foguete do RPG: deixa fumaca no caminho e explode como TNT (potencia 4) ao bater. */
public class RocketProjectile extends ThrowableItemProjectile {
    public RocketProjectile(EntityType<? extends RocketProjectile> type, Level level) {
        super(type, level);
    }

    public RocketProjectile(Level level, LivingEntity owner) {
        super(ModEntities.ROCKET.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FIREWORK_ROCKET;
    }

    @Override
    protected float getGravity() {
        return 0.006F;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 2, 0.05D, 0.05D, 0.05D, 0.0D);
            sl.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 1, 0.02D, 0.02D, 0.02D, 0.0D);
            if (this.tickCount > 120) {
                this.explodeNow(sl);
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level() instanceof ServerLevel sl) {
            this.explodeNow(sl);
        }
    }

    private void explodeNow(ServerLevel level) {
        if (this.isRemoved()) {
            return;
        }
        level.explode(this.getOwner() != null ? this.getOwner() : this, this.getX(), this.getY(), this.getZ(), 4.0F, Level.ExplosionInteraction.TNT);
        this.discard();
    }
}
