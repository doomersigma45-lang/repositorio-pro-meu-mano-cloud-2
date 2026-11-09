package com.netheritesupremo.entity;

import com.netheritesupremo.registry.ModEntities;
import com.netheritesupremo.skeleton.SkeletonFaction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/** Obus do tanque: explode ao bater e causa 750 de dano a tudo no raio (sem destruir blocos). */
public class TankShell extends ThrowableItemProjectile {
    public static final float DAMAGE = 750.0F;
    public static final double RADIUS = 3.5D;

    public TankShell(EntityType<? extends TankShell> type, Level level) {
        super(type, level);
    }

    public TankShell(Level level, LivingEntity owner) {
        super(ModEntities.TANK_SHELL.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FIRE_CHARGE;
    }

    @Override
    protected float getGravity() {
        return 0.02F;
    }

    private static boolean friendly(Entity e) {
        return e instanceof WarTank || SkeletonFaction.isFaction(e) || e.getTags().contains(SkeletonFaction.WAR_HORSE_TAG);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !friendly(entity);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount > 100) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level().isClientSide) {
            return;
        }
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            server.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 4.0F, 0.7F);
        }
        for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(RADIUS),
                x -> x.isAlive() && x != this.getOwner() && !friendly(x))) {
            if (e.distanceToSqr(this) <= (RADIUS + e.getBbWidth()) * (RADIUS + e.getBbWidth())) {
                e.hurt(this.damageSources().explosion(this, this.getOwner()), DAMAGE);
            }
        }
        this.discard();
    }
}
