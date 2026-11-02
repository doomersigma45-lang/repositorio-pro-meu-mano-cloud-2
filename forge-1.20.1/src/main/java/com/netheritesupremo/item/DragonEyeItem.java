package com.netheritesupremo.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Olho do Dragao: segurando, voo rapido, +15 coracoes e o dobro de forca. Ver DragonEyeEvents. */
public class DragonEyeItem extends Item {
    public DragonEyeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            if (sp.isShiftKeyDown()) {
                com.netheritesupremo.DragonEyePortals.cycle(sp);
            } else {
                com.netheritesupremo.DragonEyePortals.open(sp);
            }
        }
        player.getCooldowns().addCooldown(this, 20);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.netheritesupremo.dragon_eye.desc1").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("item.netheritesupremo.dragon_eye.desc2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.netheritesupremo.dragon_eye.desc3").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.netheritesupremo.dragon_eye.desc4").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.netheritesupremo.dragon_eye.desc5").withStyle(ChatFormatting.AQUA));
    }
}
