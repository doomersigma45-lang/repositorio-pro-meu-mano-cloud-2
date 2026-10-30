package com.netheritesupremo.item;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/** Katana: durabilidade acima da do diamante (1561). Dano total 17 vem do modificador da espada. */
public enum KatanaTier implements Tier {
    INSTANCE;

    @Override
    public int getUses() {
        return 2500;
    }

    @Override
    public float getSpeed() {
        return 8.0F;
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
        return 18;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(Items.DIAMOND, Items.NETHERITE_INGOT);
    }
}
