package com.netheritesupremo.weapon;

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

/** RPG: lanca um foguete que explode como TNT. A municao e 1 TNT do inventario por tiro (infinita no criativo). */
public class RpgItem extends Item {
    public static final int RELOAD_TICKS = 40;

    public RpgItem(Properties properties) {
        super(properties);
    }

    private static boolean takeTnt(Player player) {
        if (player.getAbilities().instabuild) {
            return true;
        }
        for (ItemStack s : player.getInventory().items) {
            if (s.is(Items.TNT)) {
                s.shrink(1);
                return true;
            }
        }
        if (player.getOffhandItem().is(Items.TNT)) {
            player.getOffhandItem().shrink(1);
            return true;
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide) {
            if (!takeTnt(player)) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.displayClientMessage(Component.translatable("message.netheritesupremo.no_ammo_tnt"), true);
                return InteractionResultHolder.fail(stack);
            }
            RocketProjectile rocket = new RocketProjectile(level, player);
            rocket.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.2F, 0.4F);
            level.addFreshEntity(rocket);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.5F, 0.6F);
        }
        player.getCooldowns().addCooldown(this, RELOAD_TICKS);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.netheritesupremo.rpg"));
    }
}
