/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.claudecontent.block;

import com.claudecontent.entity.MossColossusEntity;
import com.claudecontent.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public class RootAltarBlock
extends Block {
    public RootAltarBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public InteractionResult onProjectileHit(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.PASS;
        }
        ServerLevel server = (ServerLevel)level;
        if (!server.lambda$getNearbyEntities$1(MossColossusEntity.class, new AABB(pos).inflate(48.0)).isEmpty()) {
            player.lambda$updateAutoJump$0((Component)Component.translatable((String)"message.claude_content.colossus_nearby"), true);
            return InteractionResult.CONSUME;
        }
        MossColossusEntity boss = (MossColossusEntity)((EntityType)ModEntities.MOSS_COLOSSUS.get()).lambda$appendCustomNameConfig$2((Level)server);
        if (boss == null) {
            return InteractionResult.FAIL;
        }
        boss.moveTo((double)pos.setX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5, player.getYRot(), 0.0f);
        server.addFreshEntity((Entity)boss);
        server.gameEvent(null, pos, SoundEvents.IRON_GOLEM_REPAIR, SoundSource.BLOCKS, 1.7f, 0.55f);
        level.destroyBlock(pos, Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 3);
        return InteractionResult.CONSUME;
    }
}

