package com.netheritesupremo.parasite;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Quimera Assimilada: surge quando um parasita infecta de novo um animal que ja estava infectado. Rapida, pula no alvo e enfraquece. */
public class ParasiteChimera extends Monster implements ParasiteMob {
    public ParasiteChimera(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 30;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 130.0D)
                .add(Attributes.ATTACK_DAMAGE, 14.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        ParasiteBehavior.addMeleeGoals(this, 1.2D);
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.5F));
        ParasiteBehavior.addTargets(this);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 160, 1), this);
        }
        return hit;
    }
}
