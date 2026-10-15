package com.netheritesupremo.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Encantamento multiplicador (2x, 4x, 8x). Multiplica os atributos do item (armadura, resistencia,
 * dano de ataque...) e a velocidade de mineracao. Serve em armaduras e ferramentas/armas, nao
 * aparece na mesa de encantamento nem em loot comum e nao combina com outro multiplicador.
 */
public class MultiplierEnchantment extends Enchantment {
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.MAINHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private final int multiplier;

    public MultiplierEnchantment(int multiplier) {
        super(Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE, SLOTS);
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return multiplier;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinCost(int level) {
        return 30;
    }

    @Override
    public int getMaxCost(int level) {
        return 60;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem || stack.getItem() instanceof TieredItem;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return !(other instanceof MultiplierEnchantment) && super.checkCompatibility(other);
    }
}
