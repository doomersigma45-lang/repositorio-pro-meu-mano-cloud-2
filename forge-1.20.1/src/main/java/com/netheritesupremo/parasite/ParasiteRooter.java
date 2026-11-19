package com.netheritesupremo.parasite;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Rooter (inspirado no Rooter do Scape and Run: Parasites): tentaculo parado no chao. Quem entra em 10 blocos e e
 * presa e puxado, e quem chega perto leva dano e e infectado.
 */
public class ParasiteRooter extends Monster implements ParasiteMob {
    public ParasiteRooter(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 70.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        ParasiteBehavior.addTargets(this);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(double x, double y, double z) {
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        LivingEntity target = this.getTarget();
        this.setAggressive(target != null && target.isAlive());
        if (target == null || !target.isAlive()) {
            return;
        }
        double dist = this.distanceTo(target);
        if (dist > 10.0D) {
            return;
        }
        if (dist > 2.2D) {
            Vec3 pull = this.position().subtract(target.position());
            Vec3 flat = new Vec3(pull.x, 0.0D, pull.z);
            if (flat.lengthSqr() > 1.0E-4D) {
                target.setDeltaMovement(target.getDeltaMovement().add(flat.normalize().scale(0.12D)));
                target.hurtMarked = true;
            }
        } else if (this.tickCount % 20 == 0) {
            this.swing(InteractionHand.MAIN_HAND);
            this.doHurtTarget(target);
        }
    }
}
