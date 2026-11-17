package com.netheritesupremo.parasite;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Coracao da Arvore Parasita: bloco com block entity que contamina o bioma em volta. Botao direito mostra o estado. */
public class ParasiteTreeBlock extends BaseEntityBlock {
    public ParasiteTreeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ParasiteTreeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ParasiteTree.TREE_BE.get(), ParasiteTreeBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof ParasiteTreeBlockEntity be) {
            player.displayClientMessage(Component.literal("Arvore Parasita - fase " + be.phase() + " | blocos parasitados: "
                    + be.getConsumed() + " | raio: " + be.radius() + " | altura: " + be.height()), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
