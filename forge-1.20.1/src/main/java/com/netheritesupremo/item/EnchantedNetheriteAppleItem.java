package com.netheritesupremo.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Maca de netherite encantada: brilha como a maca dourada encantada. */
public class EnchantedNetheriteAppleItem extends Item {
    public EnchantedNetheriteAppleItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
