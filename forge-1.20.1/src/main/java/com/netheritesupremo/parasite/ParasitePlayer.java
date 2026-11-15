package com.netheritesupremo.parasite;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Jogador parasita: surge quando um jogador morre para um parasita. */
public class ParasitePlayer extends Monster implements ParasiteMob {
    public ParasitePlayer(EntityType<? extends ParasitePlayer> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        ParasiteBehavior.addMeleeGoals(this, 1.1D);
        ParasiteBehavior.addTargets(this);
    }
}
