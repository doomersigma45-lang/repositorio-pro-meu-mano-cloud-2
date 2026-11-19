package com.netheritesupremo.coven;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Sapo (familiar do Bewitchment): usa o corpo da ra; quem bate nele com a mao leva veneno das verrugas. */
public class CovenToad extends Frog {
    public CovenToad(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createToadAttributes() {
        return Frog.createAttributes();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide && source.getDirectEntity() instanceof Player p) {
            p.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
        }
        return hurt;
    }
}
