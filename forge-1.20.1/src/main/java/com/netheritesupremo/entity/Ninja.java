package com.netheritesupremo.entity;

import java.util.EnumSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.netheritesupremo.registry.ModItems;

/**
 * Ninja: mob neutro de Cherry Grove. 50 de vida, 3 de dano (fixo, mesmo com a arma na mao). Cada ninja
 * nasce com katana, kunai (mais veloz) ou shurikens (ataca de longe). Revida se for atacado e chama os outros.
 */
public class Ninja extends PathfinderMob {
    public static final float DAMAGE = 3.0F;

    public Ninja(EntityType<? extends Ninja> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createNinjaAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 28.0D);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        int pick = random.nextInt(3);
        ItemStack weapon = switch (pick) {
            case 0 -> new ItemStack(ModItems.KATANA.get());
            case 1 -> new ItemStack(ModItems.KUNAI.get());
            default -> new ItemStack(ModItems.SHURIKEN.get());
        };
        this.setItemSlot(EquipmentSlot.MAINHAND, weapon);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F); // o loot table cuida dos drops
    }

    private boolean holdsShuriken() {
        return this.getMainHandItem().is(ModItems.SHURIKEN.get());
    }

    /** Dano fixo de 3, independente dos atributos da arma que ele segura. */
    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        this.swing(InteractionHand.MAIN_HAND);
        return target.hurt(this.damageSources().mobAttack(this), DAMAGE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ThrowShurikenGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    /** Ninja de shuriken: de 4 a 16 blocos de distancia, arremessa; mais perto, luta de perto. */
    static class ThrowShurikenGoal extends Goal {
        private final Ninja ninja;
        private int cooldown;

        ThrowShurikenGoal(Ninja ninja) {
            this.ninja = ninja;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.ninja.getTarget();
            if (target == null || !target.isAlive() || !this.ninja.holdsShuriken()) {
                return false;
            }
            double dist = this.ninja.distanceTo(target);
            return dist >= 4.0D && dist <= 16.0D && this.ninja.hasLineOfSight(target);
        }

        @Override
        public void tick() {
            LivingEntity target = this.ninja.getTarget();
            if (target == null) {
                return;
            }
            this.ninja.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (--this.cooldown <= 0) {
                this.cooldown = 25;
                this.ninja.swing(InteractionHand.MAIN_HAND);
                ShurikenEntity shuriken = new ShurikenEntity(this.ninja.level(), this.ninja);
                shuriken.setFixedDamage(DAMAGE);
                shuriken.setItem(new ItemStack(ModItems.SHURIKEN.get()));
                double dx = target.getX() - this.ninja.getX();
                double dy = target.getEyeY() - 0.2D - shuriken.getY();
                double dz = target.getZ() - this.ninja.getZ();
                double horizontal = Math.sqrt(dx * dx + dz * dz);
                shuriken.shoot(dx, dy + horizontal * 0.05D, dz, 1.8F, 2.0F);
                this.ninja.level().addFreshEntity(shuriken);
                this.ninja.level().playSound(null, this.ninja.blockPosition(), SoundEvents.SNOWBALL_THROW,
                        SoundSource.HOSTILE, 0.6F, 1.8F);
            }
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }
}
