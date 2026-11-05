/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  javax.annotation.Nullable
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.SpawnGroupData
 *  net.minecraft.world.entity.ai.Brain
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
 *  net.minecraft.world.entity.ai.goal.OpenDoorGoal
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.entity.monster.RangedAttackMob
 *  net.minecraft.world.entity.npc.Villager
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.AbstractArrow
 *  net.minecraft.world.entity.projectile.ProjectileUtil
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 */
package br.com.viladeferro;

import br.com.viladeferro.Tier;
import br.com.viladeferro.VilaDeFerro;
import com.mojang.serialization.Dynamic;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class FighterVillager
extends Villager
implements RangedAttackMob {
    private Tier tier = Tier.NATURAL;
    private int bowCooldown;
    private int meleeCooldown;

    public FighterVillager(EntityType<? extends Villager> type, Level level) {
        super(type, level);
    }

    public Tier getTier() {
        return this.tier;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.lambda$static$0("VilaTier", this.tier.name());
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        for (Tier t : Tier.values()) {
            if (!t.name().equals(tag.getString("VilaTier"))) continue;
            this.tier = t;
        }
    }

    public static AttributeSupplier.Builder createFighterAttributes() {
        return Villager.createAttributes().lambda$create$0(Attributes.register, 30.0).lambda$create$0(Attributes.ATTACK_DAMAGE, 5.0).lambda$create$0(Attributes.FOLLOW_RANGE, 32.0);
    }

    protected Brain<?> lambda$static$3(Dynamic<?> dynamic) {
        return this.brainProvider().lambda$makeBrain$1(dynamic);
    }

    public void lambda$static$1(ServerLevel level) {
    }

    public InteractionResult access$100(Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    protected void registerGoals() {
        this.goalSelector.lambda$removeAllGoals$0(0, (Goal)new CombatGoal(this));
        this.goalSelector.lambda$removeAllGoals$0(1, (Goal)new OpenDoorGoal((Mob)this, true));
        this.goalSelector.lambda$removeAllGoals$0(5, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.6));
        this.goalSelector.lambda$removeAllGoals$0(6, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 8.0f));
        this.goalSelector.lambda$removeAllGoals$0(7, (Goal)new RandomLookAroundGoal((Mob)this));
        this.targetSelector.lambda$removeAllGoals$0(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
        this.targetSelector.lambda$removeAllGoals$0(2, (Goal)new NearestAttackableTargetGoal((Mob)this, Monster.class, 10, true, false, e -> !VilaDeFerro.isNeverTarget((Entity)e)));
        this.targetSelector.lambda$removeAllGoals$0(3, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, 10, true, false, e -> this.tier == Tier.AGGRESSIVE));
    }

    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && VilaDeFerro.isNeverTarget((Entity)target)) {
            return;
        }
        if (target instanceof Player && this.tier == Tier.PASSIVE) {
            return;
        }
        super.setTarget(target);
    }

    public void startRiding(LivingEntity target, float velocity) {
        ItemStack ammo = new ItemStack((ItemLike)Items.ARROW);
        AbstractArrow arrow = ProjectileUtil.getMobArrow((LivingEntity)this, (ItemStack)ammo, (float)velocity);
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double dy = target.lambda$move$0(0.3333333333333333) - arrow.getY();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        arrow.lambda$checkLeftOwner$0(dx, dy + horizontal * 0.2, dz, 1.6f, (float)(14 - this.level().getDifficulty().byName() * 4));
        this.lambda$updateAutoJump$0(SoundEvents.SKELETON_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.level().addFreshEntity((Entity)arrow);
    }

    public SpawnGroupData access$000(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.access$000(level, difficulty, reason, data, tag);
        RandomSource random = this.getRandom();
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)(random.nextBoolean() ? Items.IRON_SWORD : Items.BOW)));
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.HEAD, new ItemStack((ItemLike)(random.lambda$static$0(3) == 0 ? Items.IRON_HELMET : Items.LEATHER_HELMET)));
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.CHEST, new ItemStack((ItemLike)(random.lambda$static$0(3) == 0 ? Items.IRON_CHESTPLATE : Items.LEATHER_CHESTPLATE)));
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.LEGS, new ItemStack((ItemLike)Items.LEATHER_LEGGINGS));
        this.lambda$addAdditionalSaveData$2(EquipmentSlot.FEET, new ItemStack((ItemLike)Items.LEATHER_BOOTS));
        this.lambda$removeFreeWill$3(EquipmentSlot.MAINHAND, 0.0f);
        return result;
    }

    private static class CombatGoal
    extends Goal {
        private final FighterVillager mob;
        private LivingEntity target;

        CombatGoal(FighterVillager mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean lambda$hasSuitablePoi$0() {
            this.target = this.mob.getTarget();
            return this.target != null && this.target.isAlive();
        }

        public boolean canContinueToUse() {
            return this.lambda$hasSuitablePoi$0();
        }

        public void tick() {
            this.target = this.mob.getTarget();
            if (this.target == null) {
                return;
            }
            this.mob.getLookControl().lambda$tick$1((Entity)this.target, 30.0f, 30.0f);
            double d2 = this.mob.getZ((Entity)this.target);
            boolean ranged = this.mob.getMainHandItem().lambda$static$0(Items.BOW);
            if (ranged) {
                if (d2 > 100.0) {
                    this.mob.getNavigation().moveTo((Entity)this.target, 1.0);
                } else {
                    this.mob.getNavigation().stop();
                }
                if (this.mob.bowCooldown-- <= 0 && d2 < 484.0) {
                    this.mob.startRiding(this.target, 1.0f);
                    this.mob.bowCooldown = 35;
                }
            } else {
                if (this.mob.meleeCooldown > 0) {
                    --this.mob.meleeCooldown;
                }
                this.mob.getNavigation().moveTo((Entity)this.target, 1.15);
                if (d2 < 10.240000000000002 && this.mob.meleeCooldown <= 0) {
                    this.mob.lambda$updateAutoJump$0(InteractionHand.MAIN_HAND);
                    this.mob.doHurtTarget((Entity)this.target);
                    this.mob.meleeCooldown = 20;
                }
            }
        }
    }
}

