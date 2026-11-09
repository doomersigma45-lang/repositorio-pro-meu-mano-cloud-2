package com.netheritesupremo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Bolo de netherite: um bolo cinza com cobertura vermelha; cada fatia da regeneracao, absorcao e resistencia. */
public class NetheriteCakeBlock extends CakeBlock {
    public NetheriteCakeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = super.use(state, level, pos, player, hand, hit);
        if (!level.isClientSide && result.consumesAction()) {
            BlockState after = level.getBlockState(pos);
            boolean ate = !after.is(this) || after.getValue(BITES) != state.getValue(BITES);
            if (ate) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 1));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 1200, 3));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 0));
            }
        }
        return result;
    }
}
