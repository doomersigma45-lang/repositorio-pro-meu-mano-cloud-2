package com.netheritesupremo.block;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

/** Tronco/madeira da arvore cristalina: descascavel com machado. */
public class CrystalLogBlock extends RotatedPillarBlock {
    private final Supplier<Block> stripped;

    public CrystalLogBlock(Supplier<Block> stripped, Properties properties) {
        super(properties);
        this.stripped = stripped;
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction,
            boolean simulate) {
        if (toolAction == ToolActions.AXE_STRIP) {
            return this.stripped.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        }
        return super.getToolModifiedState(state, context, toolAction, simulate);
    }
}
