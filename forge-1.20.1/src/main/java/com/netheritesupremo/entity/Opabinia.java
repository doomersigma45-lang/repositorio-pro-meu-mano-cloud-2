package com.netheritesupremo.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/** Animal do Cambriano: Opabinia. */
public class Opabinia extends CambrianCreature {
    public Opabinia(EntityType<? extends Opabinia> type, Level level) {
        super(type, level);
    }

    @Override
    protected boolean aggressive() {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return CambrianCreature.createMobAttributes().add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.MOVEMENT_SPEED, 0.6D);
    }
}
