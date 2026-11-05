package com.netheritesupremo.claudecontent.entity;

import com.netheritesupremo.claudecontent.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/** Pequeno animal luminoso, pacífico e reproduzível com frutas doces. */
public class LumileuEntity extends Animal {
    public LumileuEntity(EntityType<? extends LumileuEntity> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 14.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.FOLLOW_RANGE, 18.0D);
    }

    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new PanicGoal(this, 1.35D));
        goalSelector.addGoal(1, new TemptGoal(this, 1.1D,
                Ingredient.of(Items.SWEET_BERRIES, Items.APPLE), false));
        goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override public boolean isFood(ItemStack stack) { return stack.is(Items.SWEET_BERRIES) || stack.is(Items.APPLE); }
    @Nullable @Override public AgeableMob getBreedOffspring(net.minecraft.server.level.ServerLevel level, AgeableMob partner) {
        return ModEntities.LUMILEU.get().create(level);
    }

    @Override public void aiStep() {
        if (level().isClientSide && tickCount % 3 == 0) level().addParticle(ParticleTypes.GLOW,
                getRandomX(0.35D), getY() + 0.35D + random.nextDouble() * 0.4D, getRandomZ(0.35D), 0, 0, 0);
        super.aiStep();
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.FOX_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.FOX_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.FOX_DEATH; }
}
