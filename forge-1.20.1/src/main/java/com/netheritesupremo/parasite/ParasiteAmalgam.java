package com.netheritesupremo.parasite;

import com.netheritesupremo.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * Amalgama Assimilada: massa de varios corpos fundidos, surge quando um parasita infecta de novo um mob grande ja
 * infectado. Lenta e muito resistente, a mordida aplica Wither, infecta os mobs a 8 blocos e solta vermes ao morrer.
 */
public class ParasiteAmalgam extends Monster implements ParasiteMob {
    public ParasiteAmalgam(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 60;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 260.0D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        ParasiteBehavior.addMeleeGoals(this, 1.0D);
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
            living.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 0), this);
        }
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.tickCount % 100 == 0) {
            for (Mob m : this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(8.0D), e -> !(e instanceof ParasiteMob))) {
                ParasiteBehavior.infect(m);
            }
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (this.level() instanceof ServerLevel level) {
            for (int i = 0; i < 4; i++) {
                ParasiteWorm w = ModEntities.PARASITE_WORM.get().create(level);
                if (w != null) {
                    w.moveTo(this.getX() + level.random.nextDouble() * 3.0D - 1.5D, this.getY() + 0.3D,
                            this.getZ() + level.random.nextDouble() * 3.0D - 1.5D, level.random.nextFloat() * 360.0F, 0.0F);
                    level.addFreshEntity(w);
                }
            }
        }
    }
}
