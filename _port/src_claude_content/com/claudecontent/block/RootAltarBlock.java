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

    public InteractionResult m_6227_(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.f_46443_) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.PASS;
        }
        ServerLevel server = (ServerLevel)level;
        if (!server.m_45976_(MossColossusEntity.class, new AABB(pos).m_82400_(48.0)).isEmpty()) {
            player.m_5661_((Component)Component.m_237115_((String)"message.claude_content.colossus_nearby"), true);
            return InteractionResult.CONSUME;
        }
        MossColossusEntity boss = (MossColossusEntity)((EntityType)ModEntities.MOSS_COLOSSUS.get()).m_20615_((Level)server);
        if (boss == null) {
            return InteractionResult.FAIL;
        }
        boss.m_7678_((double)pos.m_123341_() + 0.5, (double)pos.m_123342_() + 1.0, (double)pos.m_123343_() + 0.5, player.m_146908_(), 0.0f);
        server.m_7967_((Entity)boss);
        server.m_5594_(null, pos, SoundEvents.f_12009_, SoundSource.BLOCKS, 1.7f, 0.55f);
        level.m_7731_(pos, Blocks.f_50224_.m_49966_(), 3);
        return InteractionResult.CONSUME;
    }
}

