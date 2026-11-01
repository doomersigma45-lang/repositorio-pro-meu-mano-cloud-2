package com.netheritesupremo.item;

import com.netheritesupremo.registry.ModItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/** Ferramentas de obsidiana: duram mais que as de diamante (1561) e batem muito forte. */
public enum ObsidianTier implements Tier {
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
        return 0.0F;
    }

    @Override
    public int getLevel() {
        return 3;
    }

    @Override
    public int getEnchantmentValue() {
        return 14;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.OBSIDIAN_INGOT.get());
    }
}
