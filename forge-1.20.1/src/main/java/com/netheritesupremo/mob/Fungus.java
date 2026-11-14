package com.netheritesupremo.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Fungus: 90 HP, 7 de dano. Libera esporos (lentidao e fraqueza), regenera perto de cogumelos gigantes e
 * ataca principalmente a noite.
 */
public class Fungus extends Monster {
    public static final double MAX_HEALTH = 90.0D;
    public static final double DAMAGE = 7.0D;
    public static final int SPORE_RADIUS = 6;
    public static final int MUSHROOM_RADIUS = 6;

    public Fungus(EntityType<? extends Fungus> type, Level level) {
        super(type, level);
        this.xpReward = 25;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                p -> !this.level().isDay()));
    }

    private static boolean isSporeVictim(LivingEntity e) {
        if (e instanceof Fungus || e instanceof WoodStalker || !e.isAlive()) {
            return false;
        }
        return e instanceof Player p ? !p.isCreative() && !p.isSpectator() : !(e instanceof Enemy);
    }

    private static void spore(LivingEntity e) {
        e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
        e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity le && isSporeVictim(le)) {
            spore(le);
        }
        return hit;
    }

    private boolean nearGiantMushroom() {
        BlockPos c = this.blockPosition();
        for (BlockPos p : BlockPos.betweenClosed(c.offset(-MUSHROOM_RADIUS, -2, -MUSHROOM_RADIUS),
                c.offset(MUSHROOM_RADIUS, 4, MUSHROOM_RADIUS))) {
            BlockState s = this.level().getBlockState(p);
            if (s.is(Blocks.RED_MUSHROOM_BLOCK) || s.is(Blocks.BROWN_MUSHROOM_BLOCK) || s.is(Blocks.MUSHROOM_STEM)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel level)) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (this.tickCount % 60 == 0 && target != null && this.distanceTo(target) < 8.0F) {
            level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, this.getX(), this.getY() + 1.2D, this.getZ(), 40, 2.5D, 0.8D, 2.5D, 0.0D);
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(SPORE_RADIUS),
                    Fungus::isSporeVictim)) {
                spore(e);
            }
        }
        if (this.tickCount % 20 == 0 && this.getHealth() < this.getMaxHealth() && this.nearGiantMushroom()) {
            this.heal(4.0F);
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 1.5D, this.getZ(), 5, 0.4D, 0.4D, 0.4D, 0.0D);
        }
    }
}
