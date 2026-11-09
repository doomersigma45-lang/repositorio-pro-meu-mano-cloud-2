package com.netheritesupremo.skeleton;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/** Marca de Guerra: o jogador que matou o Rei Esqueleto. Some so quando ele chega a uma vila ou outpost. */
public class WarMarkEffect extends MobEffect {
    public WarMarkEffect() {
        super(MobEffectCategory.HARMFUL, 0x7A0A0A);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
