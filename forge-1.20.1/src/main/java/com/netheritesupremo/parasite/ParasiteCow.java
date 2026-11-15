package com.netheritesupremo.parasite;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.level.Level;

/** Versao parasita: hostil, nao foge, infecta o que morde. */
public class ParasiteCow extends Cow implements ParasiteMob {
    public ParasiteCow(EntityType<? extends ParasiteCow> type, Level level) {
        super(type, level);
        this.xpReward = 5;

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Cow.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
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
