package com.netheritesupremo.claudecontent.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Peça da Armadura de Brasa.
 * Bônus do conjunto completo: resistência ao fogo permanente + 25% de chance de incendiar
 * quem te atacar (ver ForgeEvents).
 */
public class CinderArmorItem extends ArmorItem {

    public CinderArmorItem(Type type, Properties properties) {
        super(ModArmorMaterials.CINDER, type, properties);
    }

    public static boolean hasFullSet(LivingEntity entity) {
        int pieces = 0;
        for (ItemStack stack : entity.getArmorSlots()) {
            if (!(stack.getItem() instanceof CinderArmorItem)) {
                return false;
            }
            pieces++;
        }
        return pieces == 4;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide && level.getGameTime() % 20L == 0L && hasFullSet(player)) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, true, false, true));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.netheritesupremo.set_bonus").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.netheritesupremo.set_bonus_fire").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.netheritesupremo.set_bonus_thorns").withStyle(ChatFormatting.GRAY));
    }
}
