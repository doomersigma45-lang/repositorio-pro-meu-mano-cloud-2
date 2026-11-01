package com.netheritesupremo;

import com.netheritesupremo.item.ObsidianArmorMaterial;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Conjunto completo de obsidiana: imunidade a explosoes. */
public class ObsidianEvents {
    public static boolean hasFullSet(LivingEntity entity) {
        for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!(stack.getItem() instanceof ArmorItem armor) || armor.getMaterial() != ObsidianArmorMaterial.INSTANCE) {
                return false;
            }
        }
        return true;
    }

    @SubscribeEvent
    public void onAttacked(LivingAttackEvent event) {
        if (event.getSource().is(DamageTypeTags.IS_EXPLOSION) && hasFullSet(event.getEntity())) {
            event.setCanceled(true);
        }
    }
}
