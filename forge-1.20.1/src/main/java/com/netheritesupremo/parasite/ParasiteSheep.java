package com.netheritesupremo.parasite;

import java.util.ArrayList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Ovelha infectada: sem la (a la some), hostil e infecta o que morde. */
public class ParasiteSheep extends Sheep implements ParasiteMob {
    public ParasiteSheep(EntityType<? extends Sheep> type, Level level) {
        super(type, level);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Sheep.createAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        // o Sheep precisa do objetivo de comer grama criado (customServerAiStep o usa); depois trocamos todo o resto
        super.registerGoals();
        for (WrappedGoal w : new ArrayList<>(this.goalSelector.getAvailableGoals())) {
            this.goalSelector.removeGoal(w.getGoal());
        }
        ParasiteBehavior.addMeleeGoals(this, 1.2D);
        ParasiteBehavior.addTargets(this);
    }

    @Override
    public boolean isSheared() {
        return true;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return true;
    }
}
