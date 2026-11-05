package com.netheritesupremo.claudecontent.block;

import com.netheritesupremo.claudecontent.entity.MossColossusEntity;
import com.netheritesupremo.claudecontent.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Altar de uso único que desperta o guardião da ruína. */
public class RootAltarBlock extends Block {
    public RootAltarBlock(Properties properties) { super(properties); }

    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel server)) return InteractionResult.PASS;
        if (!server.getEntitiesOfClass(MossColossusEntity.class,
                new net.minecraft.world.phys.AABB(pos).inflate(48.0D)).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.netheritesupremo.colossus_nearby"), true);
            return InteractionResult.CONSUME;
        }
        MossColossusEntity boss = ModEntities.MOSS_COLOSSUS.get().create(server);
        if (boss == null) return InteractionResult.FAIL;
        boss.moveTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, player.getYRot(), 0.0F);
        server.addFreshEntity(boss);
        server.playSound(null, pos, SoundEvents.IRON_GOLEM_REPAIR, SoundSource.BLOCKS, 1.7F, 0.55F);
        level.setBlock(pos, net.minecraft.world.level.block.Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 3);
        return InteractionResult.CONSUME;
    }
}
