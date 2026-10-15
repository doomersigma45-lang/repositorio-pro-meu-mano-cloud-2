package com.netheritesupremo.enchant;

import com.netheritesupremo.NetheriteSupremo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, NetheriteSupremo.MODID);

    public static final RegistryObject<Enchantment> MULTIPLIER_2X =
            ENCHANTMENTS.register("multiplier_2x", () -> new MultiplierEnchantment(2));
    public static final RegistryObject<Enchantment> MULTIPLIER_4X =
            ENCHANTMENTS.register("multiplier_4x", () -> new MultiplierEnchantment(4));
    public static final RegistryObject<Enchantment> MULTIPLIER_8X =
            ENCHANTMENTS.register("multiplier_8x", () -> new MultiplierEnchantment(8));

    private ModEnchantments() {
    }

    /** Multiplicador do item (1 se nao tiver nenhum). */
    public static int multiplierOf(ItemStack stack) {
        if (stack.isEmpty() || !stack.isEnchanted()) {
            return 1;
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(MULTIPLIER_8X.get(), stack) > 0) {
            return 8;
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(MULTIPLIER_4X.get(), stack) > 0) {
            return 4;
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(MULTIPLIER_2X.get(), stack) > 0) {
            return 2;
        }
        return 1;
    }

    public static Enchantment forMultiplier(int multiplier) {
        return switch (multiplier) {
            case 2 -> MULTIPLIER_2X.get();
            case 4 -> MULTIPLIER_4X.get();
            case 8 -> MULTIPLIER_8X.get();
            default -> null;
        };
    }
}
