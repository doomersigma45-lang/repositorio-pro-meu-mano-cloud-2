/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 *  org.jetbrains.annotations.Nullable
 */
package com.claudecontent.item;

import com.claudecontent.entity.AshReaperEntity;
import com.claudecontent.registry.ModEntities;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class AshSigilItem
extends Item {
    public AshSigilItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult m_6225_(UseOnContext context) {
        Level level = context.m_43725_();
        Player player = context.m_43723_();
        if (level.m_46791_() == Difficulty.PEACEFUL) {
            if (player != null && !level.f_46443_) {
                player.m_5661_((Component)Component.m_237115_((String)"message.claude_content.peaceful"), true);
            }
            return InteractionResult.FAIL;
        }
        if (level.f_46443_) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel server = (ServerLevel)level;
        BlockPos pos = context.m_8083_().m_121945_(context.m_43719_());
        AshReaperEntity boss = (AshReaperEntity)((EntityType)ModEntities.ASH_REAPER.get()).m_20615_((Level)server);
        if (boss == null) {
            return InteractionResult.FAIL;
        }
        boss.m_7678_((double)pos.m_123341_() + 0.5, pos.m_123342_(), (double)pos.m_123343_() + 0.5, player != null ? player.m_146908_() + 180.0f : 0.0f, 0.0f);
        if (!server.m_45786_((Entity)boss)) {
            boss.m_146870_();
            if (player != null) {
                player.m_5661_((Component)Component.m_237115_((String)"message.claude_content.no_space"), true);
            }
            return InteractionResult.FAIL;
        }
        boss.m_6518_((ServerLevelAccessor)server, server.m_6436_(pos), MobSpawnType.MOB_SUMMONED, null, null);
        server.m_7967_((Entity)boss);
        server.m_5594_(null, pos, SoundEvents.f_12563_, SoundSource.HOSTILE, 1.0f, 0.6f);
        server.m_8767_((ParticleOptions)ParticleTypes.f_123755_, boss.m_20185_(), boss.m_20186_() + 1.5, boss.m_20189_(), 40, 0.8, 1.4, 0.8, 0.02);
        server.m_8767_((ParticleOptions)ParticleTypes.f_123744_, boss.m_20185_(), boss.m_20186_() + 0.2, boss.m_20189_(), 30, 0.9, 0.2, 0.9, 0.05);
        if (player == null || !player.m_150110_().f_35937_) {
            context.m_43722_().m_41774_(1);
        }
        return InteractionResult.CONSUME;
    }

    public void m_7373_(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add((Component)Component.m_237115_((String)"tooltip.claude_content.ash_sigil").m_130940_(ChatFormatting.GRAY));
        tooltip.add((Component)Component.m_237115_((String)"tooltip.claude_content.ash_sigil_warning").m_130940_(ChatFormatting.RED));
    }
}

