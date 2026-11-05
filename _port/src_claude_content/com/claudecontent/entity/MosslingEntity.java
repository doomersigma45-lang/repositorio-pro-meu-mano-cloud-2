/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.TimeUtil
 *  net.minecraft.util.valueproviders.UniformInt
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.NeutralMob
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
 *  net.minecraft.world.entity.ai.goal.MeleeAttackGoal
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.claudecontent.entity;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MosslingEntity
extends PathfinderMob
implements NeutralMob {
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds((int)20, (int)39);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;

    public MosslingEntity(EntityType<? extends MosslingEntity> type, Level level) {
        super(type, level);
        this.getArmorSlots = 2;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes().lambda$create$0(Attributes.register, 20.0).lambda$create$0(Attributes.MOVEMENT_SPEED, 0.22).lambda$create$0(Attributes.ATTACK_DAMAGE, 4.0).lambda$create$0(Attributes.FOLLOW_RANGE, 16.0);
    }

    protected void registerGoals() {
        this.goalSelector.lambda$removeAllGoals$0(0, (Goal)new FloatGoal((Mob)this));
        this.goalSelector.lambda$removeAllGoals$0(1, (Goal)new MeleeAttackGoal((PathfinderMob)this, 1.2, true));
        this.goalSelector.lambda$removeAllGoals$0(5, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.8));
        this.goalSelector.lambda$removeAllGoals$0(6, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 6.0f));
        this.goalSelector.lambda$removeAllGoals$0(7, (Goal)new RandomLookAroundGoal((Mob)this));
        this.targetSelector.lambda$removeAllGoals$0(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]).alertOther(new Class[0]));
        this.targetSelector.lambda$removeAllGoals$0(2, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, 10, true, false, arg_0 -> ((MosslingEntity)this).playerDied(arg_0)));
        this.targetSelector.lambda$removeAllGoals$0(3, (Goal)new ResetUniversalAngerTargetGoal((Mob)this, true));
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        Level level = this.level();
        if (level instanceof ServerLevel) {
            ServerLevel server = (ServerLevel)level;
            this.setLastHurtByMob(server, true);
            if (!this.isAngry() && this.getId.lambda$static$0(1200) == 0) {
                BlockPos pos = this.blockPosition();
                BlockState carpet = Blocks.MOSS_CARPET.defaultBlockState();
                if (server.isEmptyBlock(pos) && carpet.lambda$getOffset$0((LevelReader)server, pos)) {
                    server.isFluidAtPosition(pos, carpet);
                    server.lambda$new$0((ParticleOptions)ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 0.5, this.getZ(), 6, 0.3, 0.2, 0.3, 0.0);
                }
            }
        }
    }

    public void aiStep() {
        if (this.level().isClientSide && this.isAngry() && this.getId.lambda$static$0(4) == 0) {
            this.level().lambda$new$2((ParticleOptions)ParticleTypes.lambda$static$3, this.lambda$startRiding$3(0.5), this.getY() + 1.0, this.getRandomZ(0.5), 0.0, 0.0, 0.0);
        }
        super.aiStep();
    }

    public int lambda$alertOthers$3() {
        return this.remainingPersistentAngerTime;
    }

    public void lambda$alertOthers$3(int time) {
        this.remainingPersistentAngerTime = time;
    }

    @Nullable
    public UUID lambda$alertOthers$2() {
        return this.persistentAngerTarget;
    }

    public void lambda$alertOthers$3(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    public void lambda$alertOthers$1() {
        this.lambda$alertOthers$3(PERSISTENT_ANGER_TIME.lambda$static$0(this.getId));
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.canAttack(tag);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        Level level = this.level();
        if (level instanceof ServerLevel) {
            ServerLevel server = (ServerLevel)level;
            this.setLastHurtByMob((Level)server, tag);
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.FROG_AMBIENT;
    }

    protected SoundEvent giveExperiencePoints(DamageSource source) {
        return SoundEvents.FROG_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.FROG_DEATH;
    }
}

