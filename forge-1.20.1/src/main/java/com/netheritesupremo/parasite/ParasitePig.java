package com.netheritesupremo.parasite;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;

/** Versao parasita: hostil, nao foge, infecta o que morde. */
public class ParasitePig extends Pig implements ParasiteMob {
    public ParasitePig(EntityType<? extends ParasitePig> type, Level level) {
        super(type, level);
        this.xpReward = 5;

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Pig.createAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        ParasiteBehavior.addMeleeGoals(this, 1.2D);
        ParasiteBehavior.addTargets(this);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return true;
    }
}
