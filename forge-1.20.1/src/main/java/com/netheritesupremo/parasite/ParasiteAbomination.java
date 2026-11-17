package com.netheritesupremo.parasite;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Level;

/** Abominacao Assimilada: surge quando um parasita infecta de novo um humanoide ja infectado. Aldeao gigante e deformado. */
public class ParasiteAbomination extends ParasiteVillager {
    public ParasiteAbomination(EntityType<? extends ZombieVillager> type, Level level) {
        super(type, level);
        this.xpReward = 40;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 220.0D)
                .add(Attributes.ATTACK_DAMAGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0D);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1), this);
        }
        return hit;
    }
}
