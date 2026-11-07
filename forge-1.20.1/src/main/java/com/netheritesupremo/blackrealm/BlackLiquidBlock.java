package com.netheritesupremo.blackrealm;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.Vec3;

/** Liquido preto da Terra Negra: muito dano, lentidao forte e puxa para o fundo. */
public class BlackLiquidBlock extends LiquidBlock {
    public static final float DAMAGE = 8.0F;

    public BlackLiquidBlock(Supplier<? extends FlowingFluid> fluid, BlockBehaviour.Properties properties) {
        super(fluid, properties);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(entity instanceof LivingEntity living)) {
            return;
        }
        if (living.tickCount % 10 == 0) {
            living.hurt(level.damageSources().magic(), DAMAGE);
        }
        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 3, false, true));
        Vec3 v = living.getDeltaMovement();
        living.setDeltaMovement(v.x * 0.6D, Math.min(v.y, 0.0D) - 0.15D, v.z * 0.6D);
        living.hurtMarked = true;
    }
}
