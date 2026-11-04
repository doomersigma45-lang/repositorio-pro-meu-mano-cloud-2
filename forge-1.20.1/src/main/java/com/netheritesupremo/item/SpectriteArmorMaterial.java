package com.netheritesupremo.item;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.registry.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/** Armadura de Spectrite: levemente superior a armadura de Fusao (+1 de defesa por peca, mais dureza e durabilidade). */
public enum SpectriteArmorMaterial implements ArmorMaterial {
    INSTANCE;

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        int base = switch (type) {
            case BOOTS -> 13;
            case LEGGINGS -> 15;
            case CHESTPLATE -> 16;
            case HELMET -> 11;
        };
        return base * 50; // Fusao: x48
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        // Fusao: 5 / 14 / 11 / 5 (capacete / peitoral / calca / bota -> ordem abaixo: bota, calca, peitoral, capacete)
        return switch (type) {
            case BOOTS -> 6;
            case LEGGINGS -> 12;
            case CHESTPLATE -> 15;
            case HELMET -> 6;
        };
    }

    @Override
    public int getEnchantmentValue() {
        return 22; // Fusao: 19
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_DIAMOND;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.SPECTRITE_GEM.get());
    }

    @Override
    public String getName() {
        return NetheriteSupremo.MODID + ":spectrite";
    }

    @Override
    public float getToughness() {
        return 3.0F; // Fusao: 2
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0F;
    }
}
