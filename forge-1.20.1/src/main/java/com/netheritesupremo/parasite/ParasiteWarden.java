package com.netheritesupremo.parasite;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
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
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Warden.createAttributes()
                .add(Attributes.MAX_HEALTH, 600.0D)
                .add(Attributes.ATTACK_DAMAGE, 40.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    /** Como o Warden comum, mas aceita o Warden normal como alvo (o comum ignora so o proprio tipo) e ignora parasitas. */
    @Override
    public boolean canTargetEntity(@Nullable Entity entity) {
        if (!(entity instanceof LivingEntity living) || ParasiteBehavior.isParasitic(entity)) {
            return false;
        }
        return living.level() == this.level() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(living)
                && !this.isAlliedTo(living) && living.getType() != EntityType.ARMOR_STAND
                && !living.isInvulnerable() && !living.isDeadOrDying()
                && this.level().getWorldBorder().isWithinBounds(living.getBoundingBox());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel level) || this.tickCount % 20 != 0) {
            return;
        }
        // o Warden comum se enterra e some depois de um tempo sem alvo; o infectado nunca faz isso
        this.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
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
        return false;
    }
}
