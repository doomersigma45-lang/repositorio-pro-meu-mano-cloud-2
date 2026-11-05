/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.AgeableMob
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.BreedGoal
 *  net.minecraft.world.entity.ai.goal.FollowParentGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
 *  net.minecraft.world.entity.ai.goal.PanicGoal
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.TemptGoal
 *  net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
 *  net.minecraft.world.entity.animal.Animal
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package com.claudecontent.entity;

import com.claudecontent.registry.ModEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class LumileuEntity
extends Animal {
    public LumileuEntity(EntityType<? extends LumileuEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.m_21552_().m_22268_(Attributes.f_22276_, 14.0).m_22268_(Attributes.f_22279_, 0.25).m_22268_(Attributes.f_22277_, 18.0);
    }

    protected void m_8099_() {
        this.f_21345_.m_25352_(0, (Goal)new PanicGoal((PathfinderMob)this, 1.35));
        this.f_21345_.m_25352_(1, (Goal)new TemptGoal((PathfinderMob)this, 1.1, Ingredient.m_43929_((ItemLike[])new ItemLike[]{Items.f_42780_, Items.f_42410_}), false));
        this.f_21345_.m_25352_(2, (Goal)new BreedGoal((Animal)this, 1.0));
        this.f_21345_.m_25352_(3, (Goal)new FollowParentGoal((Animal)this, 1.1));
        this.f_21345_.m_25352_(5, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.8));
        this.f_21345_.m_25352_(6, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 6.0f));
        this.f_21345_.m_25352_(7, (Goal)new RandomLookAroundGoal((Mob)this));
    }

    public boolean m_6898_(ItemStack stack) {
        return stack.m_150930_(Items.f_42780_) || stack.m_150930_(Items.f_42410_);
    }

    @Nullable
    public AgeableMob m_142606_(ServerLevel level, AgeableMob partner) {
        return (AgeableMob)((EntityType)ModEntities.LUMILEU.get()).m_20615_((Level)level);
    }

    public void m_8107_() {
        if (this.m_9236_().f_46443_ && this.f_19797_ % 3 == 0) {
            this.m_9236_().m_7106_((ParticleOptions)ParticleTypes.f_175827_, this.m_20208_(0.35), this.m_20186_() + 0.35 + this.f_19796_.m_188500_() * 0.4, this.m_20262_(0.35), 0.0, 0.0, 0.0);
        }
        super.m_8107_();
    }

    protected SoundEvent m_7515_() {
        return SoundEvents.f_11944_;
    }

    protected SoundEvent m_7975_(DamageSource source) {
        return SoundEvents.f_11948_;
    }

    protected SoundEvent m_5592_() {
        return SoundEvents.f_11946_;
    }
}

