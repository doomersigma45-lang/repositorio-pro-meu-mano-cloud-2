package com.netheritesupremo.item;

import com.netheritesupremo.registry.ModItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/** Ferramentas de Spectrite (portadas do mod Spectrite). */
public enum SpectriteTier implements Tier {
    INSTANCE;

    @Override
    public int getUses() {
        return 2800;
    }

    @Override
    public float getSpeed() {
        return 9.0F;
    }

    @Override
    public float getAttackDamageBonus() {
        return 4.0F;
    }

    @Override
    public int getLevel() {
        return 3;
    }

    @Override
    public int getEnchantmentValue() {
        return 22;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.SPECTRITE_GEM.get());
    }
}
