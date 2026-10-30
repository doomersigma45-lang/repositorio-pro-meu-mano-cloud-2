package com.netheritesupremo.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Kunai: 8 de dano, rapido, e na mao principal da +75% de velocidade (1,75x). O pulo duplo e feito no
 * cliente (client/DoubleJump), valendo com o kunai em qualquer mao.
 */
public class KunaiItem extends Item {
    public static final float DAMAGE = 8.0F;
    private static final UUID SPEED_UUID = UUID.fromString("6d3a1f0e-5c52-4c0b-9a57-2e0c4b1f7a11");

    public KunaiItem(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND) {
            return super.getDefaultAttributeModifiers(slot);
        }
        return ImmutableMultimap.of(
                Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                        DAMAGE - 1.0F, AttributeModifier.Operation.ADDITION),
                Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                        -1.2D, AttributeModifier.Operation.ADDITION),
                Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_UUID, "Kunai speed",
                        0.75D, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.netheritesupremo.kunai.desc").withStyle(ChatFormatting.AQUA));
    }
}
