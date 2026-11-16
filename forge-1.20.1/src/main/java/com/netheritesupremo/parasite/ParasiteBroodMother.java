package com.netheritesupremo.parasite;

import com.netheritesupremo.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * Matriarca (parasita novo): massa de carne lenta e muito resistente que "choca" parasitas. Com um alvo por perto bota 2
 * vermes e 1 parasita a cada 10 s (ate 12 parasitas por perto) e regenera os parasitas ao redor; ao morrer solta 6 vermes.
 */
public class ParasiteBroodMother extends Monster implements ParasiteMob {
    private int broodCooldown = 100;

    public ParasiteBroodMother(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 80;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.ATTACK_DAMAGE, 18.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
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
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (this.tickCount % 100 == 0) {
            for (Mob m : level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(12.0D), e -> e instanceof ParasiteMob && e != this)) {
                m.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 0, false, false, false));
            }
        }
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || --this.broodCooldown > 0) {
            return;
        }
        this.broodCooldown = 200;
        int near = level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(32.0D), e -> e instanceof ParasiteMob && e != this).size();
        if (near >= 12) {
            return;
        }
        int stage = ParasiteBehavior.phaseOf(this);
        for (int i = 0; i < 3; i++) {
            EntityType<? extends Mob> type = i < 2 ? ModEntities.PARASITE_WORM.get()
                    : (level.random.nextBoolean() ? ModEntities.PARASITE_ZOMBIE.get() : ModEntities.PARASITE_SPIDER.get());
            Mob m = type.create(level);
            if (m == null) {
                continue;
            }
            m.moveTo(this.getX() + level.random.nextInt(7) - 3, this.getY(), this.getZ() + level.random.nextInt(7) - 3,
                    level.random.nextFloat() * 360.0F, 0.0F);
            OutbreakEvents.stampStage(m, stage);
            m.setTarget(target);
            level.addFreshEntity(m);
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (this.level() instanceof ServerLevel level) {
            for (int i = 0; i < 6; i++) {
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
