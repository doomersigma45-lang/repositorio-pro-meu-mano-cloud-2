package com.netheritesupremo.skeleton;

import com.netheritesupremo.registry.ModItems;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

/** Trocas do esqueleto villager: a moeda sao pepitas de ferro ou de ouro; a Old Gun custa 4 barras de ferro. */
public final class SkeletonTrades {
    private SkeletonTrades() {
    }

    private static void iron(MerchantOffers o, int nuggets, ItemStack result, int uses) {
        o.add(new MerchantOffer(new ItemStack(Items.IRON_NUGGET, nuggets), result, uses, 8, 0.05F));
    }

    private static void gold(MerchantOffers o, int nuggets, ItemStack result, int uses) {
        o.add(new MerchantOffer(new ItemStack(Items.GOLD_NUGGET, nuggets), result, uses, 12, 0.05F));
    }

    public static void addTrades(MerchantOffers o) {
        // A arma deles, por 4 barras de ferro
        o.add(new MerchantOffer(new ItemStack(Items.IRON_INGOT, 4), new ItemStack(ModItems.OLD_GUN.get()), 5, 20, 0.05F));
        // Pepitas de ferro
        iron(o, 2, new ItemStack(Items.BONE, 16), 16);
        iron(o, 3, new ItemStack(Items.ARROW, 32), 16);
        iron(o, 4, new ItemStack(Items.GUNPOWDER, 6), 12);
        iron(o, 5, new ItemStack(Items.COOKED_BEEF, 12), 12);
        iron(o, 6, new ItemStack(Items.IRON_INGOT, 2), 12);
        iron(o, 8, new ItemStack(Items.SHIELD), 6);
        // Pepitas de ouro
        gold(o, 2, new ItemStack(Items.EXPERIENCE_BOTTLE, 4), 12);
        gold(o, 3, new ItemStack(Items.GOLDEN_CARROT, 8), 12);
        gold(o, 4, new ItemStack(Items.GOLDEN_APPLE), 8);
        gold(o, 6, new ItemStack(Items.ENDER_PEARL, 2), 8);
        gold(o, 6, EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.UNBREAKING, 3)), 4);
        gold(o, 9, new ItemStack(Items.DIAMOND), 6);
    }
}
