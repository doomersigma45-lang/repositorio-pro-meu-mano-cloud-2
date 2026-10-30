package com.netheritesupremo.entity;

import com.netheritesupremo.item.ShurikenItem;
import com.netheritesupremo.registry.ModEntities;
import com.netheritesupremo.registry.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** Shuriken arremessada: dano de 1x a 3x o dano de mao (6,5). Se cair num bloco, pode ser recolhida. */
public class ShurikenEntity extends ThrowableItemProjectile {
    public ShurikenEntity(EntityType<? extends ShurikenEntity> type, Level level) {
        super(type, level);
    }

    public ShurikenEntity(Level level, LivingEntity owner) {
        super(ModEntities.SHURIKEN.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SHURIKEN.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        float multiplier = 1.0F + this.random.nextFloat() * 2.0F; // 1x ate 3x
        target.hurt(this.damageSources().thrown(this, this.getOwner()), ShurikenItem.MELEE_DAMAGE * multiplier);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            if (result.getType() == HitResult.Type.BLOCK && this.random.nextFloat() < 0.6F) {
                this.spawnAtLocation(new ItemStack(ModItems.SHURIKEN.get()));
            }
            this.level().playSound(null, this.blockPosition(), SoundEvents.CHAIN_HIT, SoundSource.PLAYERS, 0.8F, 1.6F);
            this.discard();
        }
    }
}
