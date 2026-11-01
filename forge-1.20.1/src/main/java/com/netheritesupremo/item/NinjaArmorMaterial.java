package com.netheritesupremo.item;

import com.netheritesupremo.NetheriteSupremo;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/** Armadura ninja: um pouco acima do couro (7 no conjunto) e bem mais duravel. */
public enum NinjaArmorMaterial implements ArmorMaterial {
    INSTANCE;

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> 13 * 12;
            case LEGGINGS -> 15 * 12;
            case CHESTPLATE -> 16 * 12;
            case HELMET -> 11 * 12;
        };
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> 2;
            case LEGGINGS -> 3;
            case CHESTPLATE -> 4;
            case HELMET -> 2;
        };
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(Items.LEATHER, Items.IRON_INGOT);
    }

    @Override
    public String getName() {
        return NetheriteSupremo.MODID + ":ninja";
    }

    @Override
    public float getToughness() {
        return 0.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0F;
    }
}
