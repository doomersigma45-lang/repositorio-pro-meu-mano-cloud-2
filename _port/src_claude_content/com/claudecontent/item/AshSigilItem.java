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

    public InteractionResult resolvePage(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            if (player != null && !level.isClientSide) {
                player.lambda$updateAutoJump$0((Component)Component.translatable((String)"message.claude_content.peaceful"), true);
            }
            return InteractionResult.FAIL;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel server = (ServerLevel)level;
        BlockPos pos = context.getClickedPos().lambda$offsetCodec$3(context.getClickedFace());
        AshReaperEntity boss = (AshReaperEntity)((EntityType)ModEntities.ASH_REAPER.get()).lambda$appendCustomNameConfig$2((Level)server);
        if (boss == null) {
            return InteractionResult.FAIL;
        }
        boss.moveTo((double)pos.setX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, player != null ? player.getYRot() + 180.0f : 0.0f, 0.0f);
        if (!server.borderCollision((Entity)boss)) {
            boss.discard();
            if (player != null) {
                player.lambda$updateAutoJump$0((Component)Component.translatable((String)"message.claude_content.no_space"), true);
            }
            return InteractionResult.FAIL;
        }
        boss.access$000((ServerLevelAccessor)server, server.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
        server.addFreshEntity((Entity)boss);
        server.gameEvent(null, pos, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0f, 0.6f);
        server.lambda$new$0((ParticleOptions)ParticleTypes.LARGE_SMOKE, boss.getX(), boss.getY() + 1.5, boss.getZ(), 40, 0.8, 1.4, 0.8, 0.02);
        server.lambda$new$0((ParticleOptions)ParticleTypes.FLAME, boss.getX(), boss.getY() + 0.2, boss.getZ(), 30, 0.9, 0.2, 0.9, 0.05);
        if (player == null || !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.CONSUME;
    }

    public void resolvePage(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add((Component)Component.translatable((String)"tooltip.claude_content.ash_sigil").withStyle(ChatFormatting.GRAY));
        tooltip.add((Component)Component.translatable((String)"tooltip.claude_content.ash_sigil_warning").withStyle(ChatFormatting.RED));
    }
}

