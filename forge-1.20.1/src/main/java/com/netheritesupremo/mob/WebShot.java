package com.netheritesupremo.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** Bola de teia da aranha colossal: deixa o alvo lento e prende ele numa teia. */
public class WebShot extends ThrowableItemProjectile {
    public WebShot(EntityType<? extends WebShot> type, Level level) {
        super(type, level);
    }

    public WebShot(EntityType<? extends WebShot> type, LivingEntity owner, Level level) {
        super(type, owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.COBWEB;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide && result.getEntity() instanceof LivingEntity target
                && !(target instanceof Spider)) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
            this.placeWeb(target.blockPosition());
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            if (result instanceof BlockHitResult block) {
                this.placeWeb(block.getBlockPos().relative(block.getDirection()));
            }
            this.discard();
        }
    }

    private void placeWeb(BlockPos pos) {
        if (this.level().getBlockState(pos).isAir()) {
            this.level().setBlockAndUpdate(pos, Blocks.COBWEB.defaultBlockState());
            if (this.getOwner() instanceof ColossalSpider spider) {
                spider.trackWeb(pos);
            }
        }
    }
}
