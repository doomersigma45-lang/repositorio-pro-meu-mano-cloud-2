package com.netheritesupremo.enchant;

import com.google.common.collect.Multimap;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Efeito dos encantamentos multiplicadores e a fusao deles na bigorna (2x + 2x = 4x, 4x + 4x = 8x, 8x + 8x = 16x). */
public class MultiplierEvents {
    /** Niveis de experiencia cobrados por fusao: 4x custa 10, 8x custa 20. */
    private static final int COST_PER_STEP = 5;

    /**
     * Multiplica os atributos aditivos positivos do item. Roda por ultimo para enxergar tambem os
     * valores definidos pelo restante do mod (armadura/espada de netherite). A velocidade de ataque
     * e negativa e fica de fora: multiplica-la tornaria o item impossivel de usar.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onItemAttributes(ItemAttributeModifierEvent event) {
        int multiplier = ModEnchantments.multiplierOf(event.getItemStack());
        if (multiplier <= 1) {
            return;
        }
        for (Map.Entry<Attribute, AttributeModifier> entry : new ArrayList<>(event.getModifiers().entries())) {
            AttributeModifier modifier = entry.getValue();
            if (modifier.getOperation() != AttributeModifier.Operation.ADDITION || modifier.getAmount() <= 0.0D
                    || entry.getKey() == Attributes.ATTACK_SPEED) {
                continue;
            }
            String key = "netheritesupremo_multiplier_" + BuiltInRegistries.ATTRIBUTE.getKey(entry.getKey())
                    + "_" + event.getSlotType().getName() + "_" + modifier.getId();
            UUID id = UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
            event.addModifier(entry.getKey(), new AttributeModifier(id, "Multiplicador " + multiplier + "x",
                    modifier.getAmount() * (multiplier - 1), AttributeModifier.Operation.ADDITION));
        }
    }

    /** Multiplica a velocidade de mineracao da ferramenta na mao. */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        int multiplier = ModEnchantments.multiplierOf(event.getEntity().getMainHandItem());
        if (multiplier > 1) {
            event.setNewSpeed(event.getNewSpeed() * multiplier);
        }
    }

    // ------------------------------------------------------------------ bigorna
    @SubscribeEvent
    public void onAnvil(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.isEmpty() || right.isEmpty() || left.getCount() != 1 || right.getCount() != 1
                || !right.is(Items.ENCHANTED_BOOK)) {
            return;
        }
        Map<Enchantment, Integer> rightMap = EnchantmentHelper.getEnchantments(right);
        Map<Enchantment, Integer> leftMap = EnchantmentHelper.getEnchantments(left);
        for (int multiplier : new int[] {2, 4, 8}) {
            Enchantment from = ModEnchantments.forMultiplier(multiplier);
            Enchantment to = ModEnchantments.forMultiplier(multiplier * 2);
            if (!leftMap.containsKey(from) || !rightMap.containsKey(from)) {
                continue;
            }
            ItemStack output = left.copy();
            leftMap.remove(from);
            leftMap.put(to, 1);
            if (output.is(Items.ENCHANTED_BOOK)) {
                output.removeTagKey("StoredEnchantments");
                for (Map.Entry<Enchantment, Integer> entry : leftMap.entrySet()) {
                    EnchantedBookItem.addEnchantment(output, new EnchantmentInstance(entry.getKey(), entry.getValue()));
                }
            } else {
                EnchantmentHelper.setEnchantments(leftMap, output);
            }
            event.setOutput(output);
            event.setCost(Math.min(COST_PER_STEP * multiplier, 39)); // 40+ a bigorna recusa ("caro demais")
            event.setMaterialCost(1);
            return;
        }
    }
}
