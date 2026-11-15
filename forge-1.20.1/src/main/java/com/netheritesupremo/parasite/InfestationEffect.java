package com.netheritesupremo.parasite;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** Infestacao: um parasita vive dentro do mob. Ele sofre aos poucos e, se morrer, vira a versao parasita. So afeta mobs. */
public class InfestationEffect extends MobEffect {
    public InfestationEffect() {
        super(MobEffectCategory.HARMFUL, 0x4C7A1E);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "5b0f1c2e-7a4d-4e8a-9c31-0d6a2f1e8b01",
                -0.2D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 60 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof Mob) || entity.level().isClientSide) {
            return;
        }
        entity.hurt(entity.damageSources().magic(), 1.0F);
        if (entity.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SNEEZE, entity.getX(), entity.getY() + entity.getBbHeight() * 0.6D,
                    entity.getZ(), 6, entity.getBbWidth() * 0.4D, entity.getBbHeight() * 0.3D,
                    entity.getBbWidth() * 0.4D, 0.01D);
        }
    }
}
