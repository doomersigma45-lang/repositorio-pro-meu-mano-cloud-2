package com.netheritesupremo.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

/** Hibrido de Aldeao Precursor com aldeao normal: mais fraco que o Precursor, mas com as mesmas trocas. */
public class HybridVillager extends PrecursorVillager {
    public HybridVillager(EntityType<? extends PrecursorVillager> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createHybridAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public boolean isHybrid() {
        return true;
    }
}
