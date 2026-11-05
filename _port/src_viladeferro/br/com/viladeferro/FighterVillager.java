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

    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        tag.m_128359_("VilaTier", this.tier.name());
    }

    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        for (Tier t : Tier.values()) {
            if (!t.name().equals(tag.m_128461_("VilaTier"))) continue;
            this.tier = t;
        }
    }

    public static AttributeSupplier.Builder createFighterAttributes() {
        return Villager.m_35503_().m_22268_(Attributes.f_22276_, 30.0).m_22268_(Attributes.f_22281_, 5.0).m_22268_(Attributes.f_22277_, 32.0);
    }

    protected Brain<?> m_8075_(Dynamic<?> dynamic) {
        return this.m_5490_().m_22073_(dynamic);
    }

    public void m_35483_(ServerLevel level) {
    }

    public InteractionResult m_6071_(Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    protected void m_8099_() {
        this.f_21345_.m_25352_(0, (Goal)new CombatGoal(this));
        this.f_21345_.m_25352_(1, (Goal)new OpenDoorGoal((Mob)this, true));
        this.f_21345_.m_25352_(5, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.6));
        this.f_21345_.m_25352_(6, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 8.0f));
        this.f_21345_.m_25352_(7, (Goal)new RandomLookAroundGoal((Mob)this));
        this.f_21346_.m_25352_(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
        this.f_21346_.m_25352_(2, (Goal)new NearestAttackableTargetGoal((Mob)this, Monster.class, 10, true, false, e -> !VilaDeFerro.isNeverTarget((Entity)e)));
        this.f_21346_.m_25352_(3, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, 10, true, false, e -> this.tier == Tier.AGGRESSIVE));
    }

    public void m_6710_(@Nullable LivingEntity target) {
        if (target != null && VilaDeFerro.isNeverTarget((Entity)target)) {
            return;
        }
        if (target instanceof Player && this.tier == Tier.PASSIVE) {
            return;
        }
        super.m_6710_(target);
    }

    public void m_6504_(LivingEntity target, float velocity) {
        ItemStack ammo = new ItemStack((ItemLike)Items.f_42412_);
        AbstractArrow arrow = ProjectileUtil.m_37300_((LivingEntity)this, (ItemStack)ammo, (float)velocity);
        double dx = target.m_20185_() - this.m_20185_();
        double dz = target.m_20189_() - this.m_20189_();
        double dy = target.m_20227_(0.3333333333333333) - arrow.m_20186_();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        arrow.m_6686_(dx, dy + horizontal * 0.2, dz, 1.6f, (float)(14 - this.m_9236_().m_46791_().m_19028_() * 4));
        this.m_5496_(SoundEvents.f_12382_, 1.0f, 1.0f / (this.m_217043_().m_188501_() * 0.4f + 0.8f));
        this.m_9236_().m_7967_((Entity)arrow);
    }

    public SpawnGroupData m_6518_(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.m_6518_(level, difficulty, reason, data, tag);
        RandomSource random = this.m_217043_();
        this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)(random.m_188499_() ? Items.f_42383_ : Items.f_42411_)));
        this.m_8061_(EquipmentSlot.HEAD, new ItemStack((ItemLike)(random.m_188503_(3) == 0 ? Items.f_42468_ : Items.f_42407_)));
        this.m_8061_(EquipmentSlot.CHEST, new ItemStack((ItemLike)(random.m_188503_(3) == 0 ? Items.f_42469_ : Items.f_42408_)));
        this.m_8061_(EquipmentSlot.LEGS, new ItemStack((ItemLike)Items.f_42462_));
        this.m_8061_(EquipmentSlot.FEET, new ItemStack((ItemLike)Items.f_42463_));
        this.m_21409_(EquipmentSlot.MAINHAND, 0.0f);
        return result;
    }

    private static class CombatGoal
    extends Goal {
        private final FighterVillager mob;
        private LivingEntity target;

        CombatGoal(FighterVillager mob) {
            this.mob = mob;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean m_8036_() {
            this.target = this.mob.m_5448_();
            return this.target != null && this.target.m_6084_();
        }

        public boolean m_8045_() {
            return this.m_8036_();
        }

        public void m_8037_() {
            this.target = this.mob.m_5448_();
            if (this.target == null) {
                return;
            }
            this.mob.m_21563_().m_24960_((Entity)this.target, 30.0f, 30.0f);
            double d2 = this.mob.m_20280_((Entity)this.target);
            boolean ranged = this.mob.m_21205_().m_150930_(Items.f_42411_);
            if (ranged) {
                if (d2 > 100.0) {
                    this.mob.m_21573_().m_5624_((Entity)this.target, 1.0);
                } else {
                    this.mob.m_21573_().m_26573_();
                }
                if (this.mob.bowCooldown-- <= 0 && d2 < 484.0) {
                    this.mob.m_6504_(this.target, 1.0f);
                    this.mob.bowCooldown = 35;
                }
            } else {
                if (this.mob.meleeCooldown > 0) {
                    --this.mob.meleeCooldown;
                }
                this.mob.m_21573_().m_5624_((Entity)this.target, 1.15);
                if (d2 < 10.240000000000002 && this.mob.meleeCooldown <= 0) {
                    this.mob.m_6674_(InteractionHand.MAIN_HAND);
                    this.mob.m_7327_((Entity)this.target);
                    this.mob.meleeCooldown = 20;
                }
            }
        }
    }
}

