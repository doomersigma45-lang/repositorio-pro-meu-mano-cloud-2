package com.netheritesupremo.item;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.registry.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/** Armadura de obsidiana: 39 de armadura no conjunto (diamante: 20), durabilidade ~1,4x a do diamante. */
public enum ObsidianArmorMaterial implements ArmorMaterial {
    INSTANCE;

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> 13 * 45;
            case LEGGINGS -> 15 * 45;
            case CHESTPLATE -> 16 * 45;
            case HELMET -> 11 * 45;
        };
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> 8;
            case LEGGINGS -> 10;
            case CHESTPLATE -> 12;
            case HELMET -> 9;
        };
    }

    @Override
    public int getEnchantmentValue() {
        return 12;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.OBSIDIAN_INGOT.get());
    }

    @Override
    public String getName() {
        return NetheriteSupremo.MODID + ":obsidian";
    }

    @Override
    public float getToughness() {
        return 5.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.15F;
    }
}
