package com.netheritesupremo.item;

import com.netheritesupremo.registry.ModItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/** Ferramentas de Spectrite "special" (cabo de Spectrite): material "perfect_spectrite_tool" do mod original. */
public enum PerfectSpectriteTier implements Tier {
    INSTANCE;

    @Override
    public int getUses() {
        return 3600;
    }

    @Override
    public float getSpeed() {
        return 12.0F;
    }

    @Override
    public float getAttackDamageBonus() {
        return 10.0F;
    }

    @Override
    public int getLevel() {
        return 3;
    }

    @Override
    public int getEnchantmentValue() {
        return 19;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.SPECTRITE_GEM.get());
    }
}
