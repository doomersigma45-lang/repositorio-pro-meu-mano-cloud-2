package com.netheritesupremo.trade;

import com.netheritesupremo.registry.ModItems;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

/** Trocas do Aldeao Precursor: a moeda sao lingotes de ferro e diamantes. */
public final class PrecursorTrades {
    private PrecursorTrades() {
    }

    private static void iron(MerchantOffers o, int iron, ItemStack result, int uses) {
        o.add(new MerchantOffer(new ItemStack(Items.IRON_INGOT, iron), result, uses, 10, 0.05F));
    }

    private static void diamond(MerchantOffers o, int diamonds, ItemStack result, int uses) {
        o.add(new MerchantOffer(new ItemStack(Items.DIAMOND, diamonds), result, uses, 20, 0.05F));
    }

    private static ItemStack book(Enchantment e, int level) {
        return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(e, level));
    }

    private static ItemStack gear(Item item, Object... pairs) {
        ItemStack stack = new ItemStack(item);
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            stack.enchant((Enchantment) pairs[i], (Integer) pairs[i + 1]);
        }
        return stack;
    }

    public static void addTrades(MerchantOffers o) {
        // ---- pagando com ferro
        iron(o, 1, new ItemStack(Items.COOKED_BEEF, 8), 16);
        iron(o, 2, new ItemStack(Items.GOLDEN_APPLE, 1), 8);
        iron(o, 3, new ItemStack(Items.ARROW, 32), 16);
        iron(o, 6, new ItemStack(ModItems.SHURIKEN.get(), 8), 12);
        iron(o, 8, new ItemStack(ModItems.KUNAI.get()), 4);
        iron(o, 5, new ItemStack(Items.SHIELD), 8);
        iron(o, 10, gear(Items.IRON_CHESTPLATE, Enchantments.ALL_DAMAGE_PROTECTION, 4, Enchantments.UNBREAKING, 3), 4);
        iron(o, 8, gear(Items.IRON_SWORD, Enchantments.SHARPNESS, 4, Enchantments.UNBREAKING, 3), 4);
        iron(o, 6, new ItemStack(Items.ENDER_PEARL, 4), 8);
        // ---- pagando com diamante
        diamond(o, 1, new ItemStack(Items.EXPERIENCE_BOTTLE, 8), 16);
        diamond(o, 1, new ItemStack(Items.GOLDEN_APPLE, 3), 12);
        diamond(o, 3, book(Enchantments.UNBREAKING, 3), 8);
        diamond(o, 6, book(Enchantments.MENDING, 1), 4);
        diamond(o, 4, gear(Items.DIAMOND_SWORD, Enchantments.SHARPNESS, 5, Enchantments.UNBREAKING, 3,
                Enchantments.MOB_LOOTING, 3), 4);
        diamond(o, 4, gear(Items.DIAMOND_PICKAXE, Enchantments.BLOCK_EFFICIENCY, 5, Enchantments.UNBREAKING, 3,
                Enchantments.BLOCK_FORTUNE, 3), 4);
        diamond(o, 6, gear(Items.DIAMOND_CHESTPLATE, Enchantments.ALL_DAMAGE_PROTECTION, 4,
                Enchantments.UNBREAKING, 3), 4);
        diamond(o, 8, new ItemStack(ModItems.KATANA.get()), 3);
        diamond(o, 12, new ItemStack(Items.TOTEM_OF_UNDYING), 3);
        diamond(o, 20, new ItemStack(Items.ELYTRA), 2);
        diamond(o, 24, new ItemStack(ModItems.ROBOT_SPAWN_EGG.get()), 2);
    }
}
