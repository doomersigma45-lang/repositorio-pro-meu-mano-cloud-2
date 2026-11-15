package com.netheritesupremo.parasite;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;

/** Warden infectado: nao enxerga parasitas como alvo e passa a cacar tudo o que nao e parasita (irritando-se com eles). */
public class ParasiteWarden extends Warden implements ParasiteMob {
    public ParasiteWarden(EntityType<? extends ParasiteWarden> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Warden.createAttributes()
                .add(Attributes.MAX_HEALTH, 600.0D)
                .add(Attributes.ATTACK_DAMAGE, 40.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public boolean canTargetEntity(@Nullable Entity entity) {
        return !ParasiteBehavior.isParasitic(entity) && super.canTargetEntity(entity);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel level) || this.tickCount % 20 != 0) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (target == null) {
            List<LivingEntity> prey = level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(32.0D),
                    e -> e != this && ParasiteBehavior.PREY.test(e));
            LivingEntity nearest = null;
            for (LivingEntity e : prey) {
                if (nearest == null || this.distanceToSqr(e) < this.distanceToSqr(nearest)) {
                    nearest = e;
                }
            }
            if (nearest != null) {
                this.increaseAngerAt(nearest, 150, false);
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return true;
    }
}
