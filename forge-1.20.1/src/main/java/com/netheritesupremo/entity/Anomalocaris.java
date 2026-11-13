package com.netheritesupremo.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Anomalocaris: nada em oceanos profundos, 160 HP, 16 de dano, nada muito rapido. Caca peixes, lulas e
 * outros pequenos animais; evita jogadores, mas ataca se for provocado.
 */
public class Anomalocaris extends CambrianCreature {
    public Anomalocaris(EntityType<? extends Anomalocaris> type, Level level) {
        super(type, level);
    }

    @Override
    protected boolean aggressive() {
        return true;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.3D, true));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<Player>(this, Player.class, 12.0F, 1.0D, 1.4D) {
            @Override
            public boolean canUse() {
                return this.mob.getTarget() == null && super.canUse(); // provocado nao foge
            }
        });
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.LivingEntity.class, 10, true, false,
                e -> e instanceof AbstractFish || e instanceof Squid || e instanceof Axolotl));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return CambrianCreature.createMobAttributes().add(Attributes.MAX_HEALTH, 160.0D).add(Attributes.ATTACK_DAMAGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.3D).add(Attributes.FOLLOW_RANGE, 32.0D);
    }
}
