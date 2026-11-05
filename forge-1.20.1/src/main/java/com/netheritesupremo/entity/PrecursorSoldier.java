package com.netheritesupremo.entity;

import java.util.EnumSet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

/** Soldado Precursor: infantaria da invasao alienigena. Forte, resistente, atira laser e ataca de perto. */
public class PrecursorSoldier extends Monster {
    public PrecursorSoldier(EntityType<? extends PrecursorSoldier> type, Level level) {
        super(type, level);
        this.xpReward = 40;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150.0D)
                .add(Attributes.ATTACK_DAMAGE, 22.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SoldierLaserGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 1.0D, 40.0F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, PrecursorSoldier.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Villager.class, true));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    /** Laser curto: carrega 15 ticks e dispara em quem esta a vista. */
    static class SoldierLaserGoal extends Goal {
        private final PrecursorSoldier soldier;
        private int charge;
        private int cooldown = 30;

        SoldierLaserGoal(PrecursorSoldier soldier) {
            this.soldier = soldier;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                this.cooldown--;
                return false;
            }
            LivingEntity target = this.soldier.getTarget();
            return target != null && target.isAlive() && this.soldier.distanceTo(target) > 4.0D
                    && this.soldier.distanceTo(target) < 26.0D && this.soldier.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            return this.soldier.getTarget() != null && this.charge > 0;
        }

        @Override
        public void start() {
            this.charge = 15;
        }

        @Override
        public void tick() {
            LivingEntity target = this.soldier.getTarget();
            if (target == null) {
                return;
            }
            this.soldier.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (--this.charge <= 0) {
                RobotLaser.fire(this.soldier, target, 28.0D, 14.0F, new Vector3f(1.0F, 0.45F, 0.15F));
            }
        }

        @Override
        public void stop() {
            this.charge = 0;
            this.cooldown = 50 + this.soldier.getRandom().nextInt(30);
        }
    }
}
