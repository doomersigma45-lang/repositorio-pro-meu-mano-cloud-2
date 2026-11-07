package com.netheritesupremo.item;

import com.netheritesupremo.registry.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Clique em um bloco para invocar o Necromante (o item e consumido). */
public class NecromancerSummonItem extends Item {
    public NecromancerSummonItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            if (player != null && !level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.netheritesupremo.peaceful"), true);
            }
            return InteractionResult.FAIL;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel server = (ServerLevel) level;
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        var boss = ModEntities.NECROMANCER.get().create(server);
        if (boss == null) {
            return InteractionResult.FAIL;
        }
        boss.moveTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D,
                player != null ? player.getYRot() + 180.0F : 0.0F, 0.0F);
        boss.finalizeSpawn(server, server.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
        server.addFreshEntity(boss);
        server.playSound(null, pos, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0F, 0.8F);
        server.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, boss.getX(), boss.getY() + 1.0D, boss.getZ(),
                40, 0.6D, 1.2D, 0.6D, 0.03D);
        if (player == null || !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.netheritesupremo.necromancer_summoner").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.netheritesupremo.ash_sigil_warning").withStyle(ChatFormatting.RED));
    }
}
