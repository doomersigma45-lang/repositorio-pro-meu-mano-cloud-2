package com.netheritesupremo.parasite;

import com.netheritesupremo.mob.Hunter;
import java.util.ArrayList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.level.Level;

/**
 * Hunter infectado: mantem tudo do Hunter (Old Gun de longe, espada de perto, escala paredes, quebra vidro e portas,
 * nao desiste do alvo), mas caca qualquer nao parasita, nao fica preso a cabana e e mais forte.
 */
public class ParasiteHunter extends Hunter implements ParasiteMob {
    public ParasiteHunter(EntityType<? extends Hunter> type, Level level) {
        super(type, level);
        this.xpReward = 60;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Hunter.createAttributes()
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.38D)
                .add(Attributes.FOLLOW_RANGE, 72.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        for (WrappedGoal w : new ArrayList<>(this.targetSelector.getAvailableGoals())) {
            this.targetSelector.removeGoal(w.getGoal());
        }
        ParasiteBehavior.addTargets(this);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.tickCount % 40 == 0) {
            this.clearRestriction();      // o Hunter se prende a 24 blocos da cabana; o infectado nao
        }
    }
}
