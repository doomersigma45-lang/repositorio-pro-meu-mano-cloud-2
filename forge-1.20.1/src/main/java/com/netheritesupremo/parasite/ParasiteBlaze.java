package com.netheritesupremo.parasite;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.Level;

/** Blaze sequestrada (Hijacked Blaze do Scape and Run: Parasites): atira bolas de fogo e infecta quem chega perto. */
public class ParasiteBlaze extends Blaze implements ParasiteMob {
    public ParasiteBlaze(EntityType<? extends Blaze> type, Level level) {
        super(type, level);
        this.xpReward = 15;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Blaze.createAttributes().add(Attributes.MAX_HEALTH, 40.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        ParasiteBehavior.addTargets(this);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.level() instanceof ServerLevel level && this.tickCount % 100 == 0) {
            for (Mob m : level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(5.0D), e -> !(e instanceof ParasiteMob))) {
                ParasiteBehavior.infect(m);
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return true;
    }
}
