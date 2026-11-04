package com.netheritesupremo.item;

import com.netheritesupremo.registry.ModItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/** Ferramentas de Spectrite com cabo de diamante (material "spectrite_tool" do mod original). */
public enum SpectriteTier implements Tier {
    INSTANCE;

    @Override
    public int getUses() {
        return 2400;
    }

    @Override
    public float getSpeed() {
        return 10.0F;
    }

    @Override
    public float getAttackDamageBonus() {
        return 6.0F;
    }

    @Override
    public int getLevel() {
        return 3;
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.SPECTRITE_GEM.get());
    }
}
