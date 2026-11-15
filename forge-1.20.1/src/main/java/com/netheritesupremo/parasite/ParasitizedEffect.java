package com.netheritesupremo.parasite;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** Parasitado: o mob (sem versao parasita) passa a agir como um parasita. So afeta mobs. A logica fica em ParasiteEvents. */
public class ParasitizedEffect extends MobEffect {
    public ParasitizedEffect() {
        super(MobEffectCategory.HARMFUL, 0x2F5A12);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "9c4e7d11-3b2a-4f60-8d15-6a1b0c9e2f02",
                0.2D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Mob && entity.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SNEEZE, entity.getX(), entity.getY() + entity.getBbHeight() * 0.6D,
                    entity.getZ(), 3, entity.getBbWidth() * 0.4D, entity.getBbHeight() * 0.3D,
                    entity.getBbWidth() * 0.4D, 0.01D);
        }
    }
}
