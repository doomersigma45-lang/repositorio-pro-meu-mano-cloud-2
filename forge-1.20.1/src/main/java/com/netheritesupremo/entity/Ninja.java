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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import com.netheritesupremo.registry.ModItems;

/**
 * Ninja: mob neutro de Cherry Grove. 50 de vida, 7 ou 12 de dano (sorteado; fixo, mesmo com a arma na mao), armadura de ferro invisivel encantada. Cada ninja
 * nasce com katana, kunai (mais veloz) ou shurikens (ataca de longe). Revida se for atacado e chama os outros.
 */
public class Ninja extends PathfinderMob {
    public static final float DAMAGE_LOW = 7.0F;
    public static final float DAMAGE_HIGH = 12.0F;
    /** Dano deste ninja: 7 ou 12, sorteado ao nascer. */
    private float hitDamage = DAMAGE_LOW;

    public Ninja(EntityType<? extends Ninja> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createNinjaAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE_LOW)
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
        // Armadura de ferro encantada (nao aparece: o renderer nao desenha armadura)
        this.setItemSlot(EquipmentSlot.HEAD, enchanted(Items.IRON_HELMET, false));
        this.setItemSlot(EquipmentSlot.CHEST, enchanted(Items.IRON_CHESTPLATE, false));
        this.setItemSlot(EquipmentSlot.LEGS, enchanted(Items.IRON_LEGGINGS, false));
        this.setItemSlot(EquipmentSlot.FEET, enchanted(Items.IRON_BOOTS, true));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.setDropChance(slot, 0.0F); // o loot table cuida dos drops
        }
    }

    private static ItemStack enchanted(Item item, boolean boots) {
        ItemStack stack = new ItemStack(item);
        stack.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 3);
        stack.enchant(Enchantments.UNBREAKING, 3);
        if (boots) {
            stack.enchant(Enchantments.FALL_PROTECTION, 4);
        }
        return stack;
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("HitDamage", this.hitDamage);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HitDamage")) {
            this.hitDamage = tag.getFloat("HitDamage");
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.hitDamage);
        }
    }

    @javax.annotation.Nullable
    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level,
            DifficultyInstance difficulty, net.minecraft.world.entity.MobSpawnType reason,
            @javax.annotation.Nullable net.minecraft.world.entity.SpawnGroupData data,
            @javax.annotation.Nullable net.minecraft.nbt.CompoundTag tag) {
        data = super.finalizeSpawn(level, difficulty, reason, data, tag);
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        this.hitDamage = level.getRandom().nextBoolean() ? DAMAGE_LOW : DAMAGE_HIGH;
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.hitDamage);
        return data;
    }

    private boolean holdsShuriken() {
        return this.getMainHandItem().is(ModItems.SHURIKEN.get());
    }

    /** Dano fixo (7 ou 12), independente dos atributos da arma que ele segura. */
    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        this.swing(InteractionHand.MAIN_HAND);
        return target.hurt(this.damageSources().mobAttack(this), this.hitDamage);
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
                shuriken.setFixedDamage(this.ninja.hitDamage);
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
