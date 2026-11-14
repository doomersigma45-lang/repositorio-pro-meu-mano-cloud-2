package com.netheritesupremo.mob;

import java.util.EnumSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Cavalo esqueleto do Cavaleiro sem Cabeca: 300 HP, rapido, segue o alvo do cavaleiro e nao pode ser montado. */
public class HorsemanSteed extends SkeletonHorse {
    public HorsemanSteed(EntityType<? extends SkeletonHorse> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createSteedAttributes() {
        return createBaseHorseAttributes()
                .add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.36D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SteedChaseGoal());
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof PumpkinBomb) {
            return false;
        }
        return super.hurt(source, amount);
    }

    private class SteedChaseGoal extends Goal {
        SteedChaseGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        private LivingEntity riderTarget() {
            return HorsemanSteed.this.getFirstPassenger() instanceof HeadlessHorseman h ? h.getTarget() : null;
        }

        @Override
        public boolean canUse() {
            LivingEntity t = this.riderTarget();
            return t != null && t.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void tick() {
            LivingEntity t = this.riderTarget();
            if (t != null && HorsemanSteed.this.tickCount % 5 == 0) {
                HorsemanSteed.this.getNavigation().moveTo(t, 1.5D);
            }
        }
    }
}
