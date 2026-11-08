package com.netheritesupremo.skeleton;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Old Gun: um tiro de 50 a 150 de dano, mas demora 5 segundos para recarregar. */
public class OldGunItem extends Item {
    public static final int RELOAD_TICKS = 100;

    public OldGunItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide) {
            OldGunBullet bullet = new OldGunBullet(level, player);
            bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 0.4F);
            level.addFreshEntity(bullet);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIREWORK_ROCKET_BLAST,
                    SoundSource.PLAYERS, 1.6F, 0.6F);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        }
        player.getCooldowns().addCooldown(this, RELOAD_TICKS);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
        return repair.is(Items.IRON_INGOT);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.netheritesupremo.old_gun"));
    }
}
