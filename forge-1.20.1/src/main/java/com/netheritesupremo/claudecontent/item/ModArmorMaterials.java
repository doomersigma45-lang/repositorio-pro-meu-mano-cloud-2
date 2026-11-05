package com.netheritesupremo.claudecontent.item;

import com.netheritesupremo.claudecontent.ClaudeContent;
import com.netheritesupremo.claudecontent.registry.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum ModArmorMaterials implements ArmorMaterial {
    // nome, multiplicador de durabilidade, defesa [capacete, peitoral, calça, bota], encantamento, som, resistência, repulsão
    CINDER("cinder", 36, new int[]{3, 8, 6, 3}, 17, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.5F, 0.05F,
            () -> Ingredient.of(ModItems.CINDER_SCALE.get()));

    private final String name;
    private final int durabilityMultiplier;
    private final int[] defense; // ordem: HELMET, CHESTPLATE, LEGGINGS, BOOTS
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    ModArmorMaterials(String name, int durabilityMultiplier, int[] defense, int enchantability,
                      SoundEvent equipSound, float toughness, float knockbackResistance,
                      Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.defense = defense;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = new LazyLoadedValue<>(repairIngredient);
    }

    private static int baseDurability(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> 11;
            case CHESTPLATE -> 16;
            case LEGGINGS -> 15;
            case BOOTS -> 13;
        };
    }

    private static int index(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> 0;
            case CHESTPLATE -> 1;
            case LEGGINGS -> 2;
            case BOOTS -> 3;
        };
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return baseDurability(type) * durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return defense[index(type)];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    /** Com o prefixo do mod, o Forge procura textures/models/armor/cinder_layer_1.png dentro do namespace certo. */
    @Override
    public String getName() {
        return ClaudeContent.MODID + ":" + name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}
