package com.netheritesupremo.coven;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.level.Level;

/**
 * Corvo e Coruja (familiares do Bewitchment). Usam o corpo do papagaio: domesticam com sementes e voam. Domesticado
 * e perto do dono, a Coruja da Visao Noturna e o Corvo da Sorte.
 */
public class CovenBird extends Parrot {
    public CovenBird(EntityType<? extends Parrot> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createBirdAttributes() {
        return Parrot.createAttributes();
    }

    public boolean isOwl() {
        return this.getType() == Coven.OWL.get();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel) || !this.isTame()
                || this.tickCount % 100 != 0) {
            return;
        }
        LivingEntity owner = this.getOwner();
        if (owner != null && owner.isAlive() && this.distanceToSqr(owner) < 16.0D * 16.0D) {
            if (this.isOwl()) {
                owner.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false));
            } else {
                owner.addEffect(new MobEffectInstance(MobEffects.LUCK, 400, 0, true, false));
            }
        }
    }
}
