package com.netheritesupremo.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Nucleo do Precursor: segurando, Forca VI + Health Boost 40 e (tecla K) atravessar paredes. Ver CoreEvents. */
public class PrecursorCoreItem extends Item {
    public PrecursorCoreItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.netheritesupremo.precursor_core.desc1").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.netheritesupremo.precursor_core.desc2").withStyle(ChatFormatting.GRAY));
    }
}
