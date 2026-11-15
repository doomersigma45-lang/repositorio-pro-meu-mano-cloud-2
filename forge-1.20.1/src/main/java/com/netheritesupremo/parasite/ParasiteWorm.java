package com.netheritesupremo.parasite;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.level.Level;

/** Parasita: pequeno verme que entra num mob (e some dentro dele) para infecta-lo. Em jogadores so morde. */
public class ParasiteWorm extends Endermite implements ParasiteMob {
    public ParasiteWorm(EntityType<? extends ParasiteWorm> type, Level level) {
        super(type, level);
        this.xpReward = 2;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Endermite.createAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D);
    }

    @Override
    protected void registerGoals() {
        ParasiteBehavior.addMeleeGoals(this, 1.2D);
        ParasiteBehavior.addTargets(this);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (target instanceof Mob mob && ParasiteBehavior.infect(mob)) {
            this.discard();
            return true;
        }
        return super.doHurtTarget(target);
    }
}
