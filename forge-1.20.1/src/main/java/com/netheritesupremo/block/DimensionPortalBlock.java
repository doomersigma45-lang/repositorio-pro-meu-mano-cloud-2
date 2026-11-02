package com.netheritesupremo.block;

import com.netheritesupremo.DragonEyePortals;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Bloco de portal aberto pelo Olho do Dragao: leva a outra dimensao e some depois de 30 segundos. */
public class DimensionPortalBlock extends Block {
    public static final int LIFETIME_TICKS = 600;

    public DimensionPortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moving) {
        super.onPlace(state, level, pos, old, moving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, LIFETIME_TICKS);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        DragonEyePortals.TARGETS.remove(DragonEyePortals.key(level.dimension(), pos));
        level.removeBlock(pos, false);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level instanceof ServerLevel server) {
            DragonEyePortals.travel(entity, server, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    }
}
