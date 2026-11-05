/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package com.claudecontent.item;

import com.claudecontent.item.ModArmorMaterials;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CinderArmorItem
extends ArmorItem {
    public CinderArmorItem(ArmorItem.Type type, Item.Properties properties) {
        super((ArmorMaterial)ModArmorMaterials.CINDER, type, properties);
    }

    public static boolean hasFullSet(LivingEntity entity) {
        int pieces = 0;
        for (ItemStack stack : entity.getArmorSlots()) {
            if (!(stack.setRepairCost() instanceof CinderArmorItem)) {
                return false;
            }
            ++pieces;
        }
        return pieces == 4;
    }

    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide && level.getGameTime() % 20L == 0L && CinderArmorItem.hasFullSet((LivingEntity)player)) {
            player.getStandingEyeHeight(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, true, false, true));
        }
    }

    public void resolvePage(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add((Component)Component.translatable((String)"tooltip.claude_content.set_bonus").withStyle(ChatFormatting.GOLD));
        tooltip.add((Component)Component.translatable((String)"tooltip.claude_content.set_bonus_fire").withStyle(ChatFormatting.GRAY));
        tooltip.add((Component)Component.translatable((String)"tooltip.claude_content.set_bonus_thorns").withStyle(ChatFormatting.GRAY));
    }
}

