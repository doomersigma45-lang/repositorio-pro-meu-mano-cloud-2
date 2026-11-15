package com.netheritesupremo.parasite;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Efeito escondido dos parasitas: amplificador 0 = fase 2 (vida x1,5, dano x1,4), amplificador 1 = fase 3
 * (vida x2,5, dano x2,2). Tambem e o que o cliente le para saber a fase e deformar o modelo.
 */
public class ParasitePhaseEffect extends MobEffect {
    public ParasitePhaseEffect() {
        super(MobEffectCategory.NEUTRAL, 0x6A0F14);
        this.addAttributeModifier(Attributes.MAX_HEALTH, "3d7c1a52-6e0b-4b8e-8a43-52c0f1e7a903", 0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "a81f0e64-2c9d-4d37-b5a2-17e3c4d9f804", 0.4D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "c25b9d70-8f13-4a6c-9e55-3b0a7d1c6e05", 0.04D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return modifier.getAmount() * (amplifier <= 0 ? 1.0D : 3.0D);
    }
}
