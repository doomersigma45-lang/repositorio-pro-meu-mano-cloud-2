package com.netheritesupremo.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

/**
 * Peca da armadura de Spectrite. Como no mod original, cada peca aumenta a vida maxima
 * (capacete +4, peitoral +8, calca +6, botas +2 = +20 de vida, 10 coracoes). O set completo
 * ainda da efeitos (ver SpectriteEvents).
 */
public class SpectriteArmorItem extends ArmorItem {
    private static final UUID[] IDS = {
            UUID.fromString("5b0f6c1e-91a2-4f6e-8d11-0c3e5a7b9d01"), // botas
            UUID.fromString("5b0f6c1e-91a2-4f6e-8d11-0c3e5a7b9d02"), // calca
            UUID.fromString("5b0f6c1e-91a2-4f6e-8d11-0c3e5a7b9d03"), // peitoral
            UUID.fromString("5b0f6c1e-91a2-4f6e-8d11-0c3e5a7b9d04")  // capacete
    };

    private final double bonusHealth;
    private final UUID healthId;

    public SpectriteArmorItem(ArmorMaterial material, Type type, Item.Properties properties) {
        super(material, type, properties);
        this.bonusHealth = switch (type) {
            case HELMET -> 4.0D;
            case CHESTPLATE -> 8.0D;
            case LEGGINGS -> 6.0D;
            case BOOTS -> 2.0D;
        };
        this.healthId = IDS[type.ordinal()];
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> base = super.getDefaultAttributeModifiers(slot);
        if (slot != this.type.getSlot()) {
            return base;
        }
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(base);
        builder.put(Attributes.MAX_HEALTH, new AttributeModifier(this.healthId, "Spectrite armor health",
                this.bonusHealth, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }
}
