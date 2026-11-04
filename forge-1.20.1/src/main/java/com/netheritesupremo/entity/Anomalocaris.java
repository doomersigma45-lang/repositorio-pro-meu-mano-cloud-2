package com.netheritesupremo.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/** Animal do Cambriano: Anomalocaris. */
public class Anomalocaris extends CambrianCreature {
    public Anomalocaris(EntityType<? extends Anomalocaris> type, Level level) {
        super(type, level);
    }

    @Override
    protected boolean aggressive() {
        return true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return CambrianCreature.createMobAttributes().add(Attributes.MAX_HEALTH, 60.0D).add(Attributes.ATTACK_DAMAGE, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.9D).add(Attributes.FOLLOW_RANGE, 24.0D);
    }
}
