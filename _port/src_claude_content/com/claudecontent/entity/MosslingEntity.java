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
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.m_145020_((int)20, (int)39);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;

    public MosslingEntity(EntityType<? extends MosslingEntity> type, Level level) {
        super(type, level);
        this.f_21364_ = 2;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.m_21552_().m_22268_(Attributes.f_22276_, 20.0).m_22268_(Attributes.f_22279_, 0.22).m_22268_(Attributes.f_22281_, 4.0).m_22268_(Attributes.f_22277_, 16.0);
    }

    protected void m_8099_() {
        this.f_21345_.m_25352_(0, (Goal)new FloatGoal((Mob)this));
        this.f_21345_.m_25352_(1, (Goal)new MeleeAttackGoal((PathfinderMob)this, 1.2, true));
        this.f_21345_.m_25352_(5, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.8));
        this.f_21345_.m_25352_(6, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 6.0f));
        this.f_21345_.m_25352_(7, (Goal)new RandomLookAroundGoal((Mob)this));
        this.f_21346_.m_25352_(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]).m_26044_(new Class[0]));
        this.f_21346_.m_25352_(2, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, 10, true, false, arg_0 -> ((MosslingEntity)this).m_21674_(arg_0)));
        this.f_21346_.m_25352_(3, (Goal)new ResetUniversalAngerTargetGoal((Mob)this, true));
    }

    protected void m_8024_() {
        super.m_8024_();
        Level level = this.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel server = (ServerLevel)level;
            this.m_21666_(server, true);
            if (!this.m_21660_() && this.f_19796_.m_188503_(1200) == 0) {
                BlockPos pos = this.m_20183_();
                BlockState carpet = Blocks.f_152543_.m_49966_();
                if (server.m_46859_(pos) && carpet.m_60710_((LevelReader)server, pos)) {
                    server.m_46597_(pos, carpet);
                    server.m_8767_((ParticleOptions)ParticleTypes.f_123748_, this.m_20185_(), this.m_20186_() + 0.5, this.m_20189_(), 6, 0.3, 0.2, 0.3, 0.0);
                }
            }
        }
    }

    public void m_8107_() {
        if (this.m_9236_().f_46443_ && this.m_21660_() && this.f_19796_.m_188503_(4) == 0) {
            this.m_9236_().m_7106_((ParticleOptions)ParticleTypes.f_123792_, this.m_20208_(0.5), this.m_20186_() + 1.0, this.m_20262_(0.5), 0.0, 0.0, 0.0);
        }
        super.m_8107_();
    }

    public int m_6784_() {
        return this.remainingPersistentAngerTime;
    }

    public void m_7870_(int time) {
        this.remainingPersistentAngerTime = time;
    }

    @Nullable
    public UUID m_6120_() {
        return this.persistentAngerTarget;
    }

    public void m_6925_(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    public void m_6825_() {
        this.m_7870_(PERSISTENT_ANGER_TIME.m_214085_(this.f_19796_));
    }

    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        this.m_21678_(tag);
    }

    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        Level level = this.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel server = (ServerLevel)level;
            this.m_147285_((Level)server, tag);
        }
    }

    protected SoundEvent m_7515_() {
        return SoundEvents.f_215690_;
    }

    protected SoundEvent m_7975_(DamageSource source) {
        return SoundEvents.f_215693_;
    }

    protected SoundEvent m_5592_() {
        return SoundEvents.f_215691_;
    }
}

