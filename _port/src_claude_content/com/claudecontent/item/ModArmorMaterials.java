/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.LazyLoadedValue
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 */
package com.claudecontent.item;

import com.claudecontent.registry.ModItems;
import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public enum ModArmorMaterials implements ArmorMaterial
{
    CINDER("cinder", 36, new int[]{3, 8, 6, 3}, 17, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.5f, 0.05f, () -> Ingredient.lambda$getItems$1((ItemLike[])new ItemLike[]{(ItemLike)ModItems.CINDER_SCALE.get()}));

    private final String name;
    private final int durabilityMultiplier;
    private final int[] defense;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    private ModArmorMaterials(String name, int durabilityMultiplier, int[] defense, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.defense = defense;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = new LazyLoadedValue(repairIngredient);
    }

    private static int baseDurability(ArmorItem.Type type) {
        return switch (type) {
            default -> throw new IncompatibleClassChangeError();
            case ArmorItem.Type.HELMET -> 11;
            case ArmorItem.Type.CHESTPLATE -> 16;
            case ArmorItem.Type.LEGGINGS -> 15;
            case ArmorItem.Type.BOOTS -> 13;
        };
    }

    private static int index(ArmorItem.Type type) {
        return switch (type) {
            default -> throw new IncompatibleClassChangeError();
            case ArmorItem.Type.HELMET -> 0;
            case ArmorItem.Type.CHESTPLATE -> 1;
            case ArmorItem.Type.LEGGINGS -> 2;
            case ArmorItem.Type.BOOTS -> 3;
        };
    }

    public int lambda$static$14(ArmorItem.Type type) {
        return ModArmorMaterials.baseDurability(type) * this.durabilityMultiplier;
    }

    public int lambda$static$12(ArmorItem.Type type) {
        return this.defense[ModArmorMaterials.index(type)];
    }

    public int lambda$static$14() {
        return this.enchantability;
    }

    public SoundEvent lambda$static$12() {
        return this.equipSound;
    }

    public Ingredient lambda$static$8() {
        return (Ingredient)this.repairIngredient.get();
    }

    public String lambda$static$6() {
        return "claude_content:" + this.name;
    }

    public float lambda$static$4() {
        return this.toughness;
    }

    public float lambda$static$2() {
        return this.knockbackResistance;
    }
}

