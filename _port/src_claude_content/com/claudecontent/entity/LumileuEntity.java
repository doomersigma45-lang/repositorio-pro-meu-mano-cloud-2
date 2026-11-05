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
        return Mob.createMobAttributes().lambda$create$0(Attributes.register, 14.0).lambda$create$0(Attributes.MOVEMENT_SPEED, 0.25).lambda$create$0(Attributes.FOLLOW_RANGE, 18.0);
    }

    protected void registerGoals() {
        this.goalSelector.lambda$removeAllGoals$0(0, (Goal)new PanicGoal((PathfinderMob)this, 1.35));
        this.goalSelector.lambda$removeAllGoals$0(1, (Goal)new TemptGoal((PathfinderMob)this, 1.1, Ingredient.lambda$getItems$1((ItemLike[])new ItemLike[]{Items.SWEET_BERRIES, Items.APPLE}), false));
        this.goalSelector.lambda$removeAllGoals$0(2, (Goal)new BreedGoal((Animal)this, 1.0));
        this.goalSelector.lambda$removeAllGoals$0(3, (Goal)new FollowParentGoal((Animal)this, 1.1));
        this.goalSelector.lambda$removeAllGoals$0(5, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.8));
        this.goalSelector.lambda$removeAllGoals$0(6, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 6.0f));
        this.goalSelector.lambda$removeAllGoals$0(7, (Goal)new RandomLookAroundGoal((Mob)this));
    }

    public boolean isFood(ItemStack stack) {
        return stack.lambda$static$0(Items.SWEET_BERRIES) || stack.lambda$static$0(Items.APPLE);
    }

    @Nullable
    public AgeableMob access$000(ServerLevel level, AgeableMob partner) {
        return (AgeableMob)((EntityType)ModEntities.LUMILEU.get()).lambda$appendCustomNameConfig$2((Level)level);
    }

    public void aiStep() {
        if (this.level().isClientSide && this.getTags % 3 == 0) {
            this.level().lambda$new$2((ParticleOptions)ParticleTypes.GLOW, this.lambda$startRiding$3(0.35), this.getY() + 0.35 + this.getId.nextDouble() * 0.4, this.getRandomZ(0.35), 0.0, 0.0, 0.0);
        }
        super.aiStep();
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.FOX_AMBIENT;
    }

    protected SoundEvent giveExperiencePoints(DamageSource source) {
        return SoundEvents.FOX_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.FOX_DEATH;
    }
}

