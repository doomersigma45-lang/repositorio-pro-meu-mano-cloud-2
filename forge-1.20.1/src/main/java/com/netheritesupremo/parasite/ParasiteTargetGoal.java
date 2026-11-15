package com.netheritesupremo.parasite;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

/** Procura qualquer ser vivo que nao seja parasita. */
public class ParasiteTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
    public ParasiteTargetGoal(Mob mob) {
        super(mob, LivingEntity.class, 10, true, false, ParasiteBehavior.PREY);
    }
}
