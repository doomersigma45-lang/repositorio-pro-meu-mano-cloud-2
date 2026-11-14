package com.netheritesupremo.mob;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.level.Level;

/** Cria do Abismo: pequena criatura invocada pelo Abismo (usa o modelo do endermite). */
public class AbyssSpawn extends Endermite {
    public AbyssSpawn(EntityType<? extends AbyssSpawn> type, Level level) {
        super(type, level);
        this.xpReward = 3;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Endermite.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D);
    }
}
