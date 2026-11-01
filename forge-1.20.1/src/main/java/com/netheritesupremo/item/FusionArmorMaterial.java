package com.netheritesupremo.item;

import com.netheritesupremo.NetheriteSupremo;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/** Fusao de ferro + diamante: defesa e durabilidade sao a soma das duas armaduras. */
public enum FusionArmorMaterial implements ArmorMaterial {
    INSTANCE;

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        // ferro (15x) + diamante (33x)
        int base = switch (type) {
            case BOOTS -> 13;
            case LEGGINGS -> 15;
            case CHESTPLATE -> 16;
            case HELMET -> 11;
        };
        return base * (15 + 33);
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        // ferro 2/5/6/2 + diamante 3/6/8/3
        return switch (type) {
            case BOOTS -> 5;
            case LEGGINGS -> 11;
            case CHESTPLATE -> 14;
            case HELMET -> 5;
        };
    }

    @Override
    public int getEnchantmentValue() {
        return 19; // ferro 9 + diamante 10
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_DIAMOND;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(Items.IRON_INGOT, Items.DIAMOND);
    }

    @Override
    public String getName() {
        return NetheriteSupremo.MODID + ":fusion";
    }

    @Override
    public float getToughness() {
        return 2.0F; // diamante 2 + ferro 0
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0F;
    }
}
