/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.AgeableMob
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.SpawnGroupData
 *  net.minecraft.world.entity.TamableAnimal
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.FollowOwnerGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
 *  net.minecraft.world.entity.ai.goal.MeleeAttackGoal
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal
 *  net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal
 *  net.minecraft.world.entity.animal.IronGolem
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 */
package br.com.viladeferro;

import br.com.viladeferro.ChiefVillager;
import br.com.viladeferro.FighterVillager;
import br.com.viladeferro.Tier;
import br.com.viladeferro.VilaDeFerro;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class ChiefCub
extends TamableAnimal {
    private static final int GROW_TICKS = 12000;
    private static final int DROP_INTERVAL = 6000;
    private int summonCooldown = 100;
    private int dropTimer;
    private boolean geared;

    public ChiefCub(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createCubAttributes() {
        return Mob.m_21552_().m_22268_(Attributes.f_22276_, 20.0).m_22268_(Attributes.f_22279_, 0.32).m_22268_(Attributes.f_22281_, 3.0).m_22268_(Attributes.f_22277_, 32.0);
    }

    protected void m_8099_() {
        this.f_21345_.m_25352_(0, (Goal)new FloatGoal((Mob)this));
        this.f_21345_.m_25352_(1, (Goal)new SitWhenOrderedToGoal((TamableAnimal)this));
        this.f_21345_.m_25352_(2, (Goal)new MeleeAttackGoal((PathfinderMob)this, 1.2, true));
        this.f_21345_.m_25352_(3, (Goal)new FollowOwnerGoal((TamableAnimal)this, 1.1, 8.0f, 2.0f, false));
        this.f_21345_.m_25352_(4, (Goal)new WaterAvoidingRandomStrollGoal((PathfinderMob)this, 0.8));
        this.f_21345_.m_25352_(5, (Goal)new LookAtPlayerGoal((Mob)this, Player.class, 8.0f));
        this.f_21345_.m_25352_(6, (Goal)new RandomLookAroundGoal((Mob)this));
        this.f_21346_.m_25352_(1, (Goal)new OwnerHurtByTargetGoal((TamableAnimal)this));
        this.f_21346_.m_25352_(2, (Goal)new OwnerHurtTargetGoal((TamableAnimal)this));
        this.f_21346_.m_25352_(3, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]).m_26044_(new Class[0]));
    }

    public void becomeBaby() {
        this.equipGear();
        this.m_146762_(-12000);
        this.applyStage(true);
    }

    protected void m_30232_() {
        super.m_30232_();
        this.applyStage(true);
    }

    private void applyStage(boolean heal) {
        boolean baby = this.m_6162_();
        this.m_21051_(Attributes.f_22276_).m_22100_(baby ? 20.0 : 60.0);
        this.m_21051_(Attributes.f_22281_).m_22100_(baby ? 1.0 : 4.0);
        if (heal) {
            this.m_21153_(this.m_21233_());
        }
    }

    public InteractionResult m_6071_(Player player, InteractionHand hand) {
        if (this.m_21830_((LivingEntity)player) && hand == InteractionHand.MAIN_HAND) {
            if (!this.m_9236_().f_46443_) {
                this.m_21839_(!this.m_21827_());
                this.f_20899_ = false;
                this.f_21344_.m_26573_();
                this.m_6710_(null);
            }
            return InteractionResult.m_19078_((boolean)this.m_9236_().f_46443_);
        }
        return super.m_6071_(player, hand);
    }

    public SpawnGroupData m_6518_(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.m_6518_(level, difficulty, reason, data, tag);
        this.equipGear();
        return result;
    }

    public void equipGear() {
        if (this.geared) {
            return;
        }
        this.geared = true;
        ItemStack helmet = ChiefCub.protect(new ItemStack((ItemLike)Items.f_42480_));
        helmet.m_41663_(Enchantments.f_44970_, 3);
        helmet.m_41663_(Enchantments.f_44971_, 1);
        ItemStack chest = ChiefCub.protect(new ItemStack((ItemLike)Items.f_42481_));
        ItemStack legs = ChiefCub.protect(new ItemStack((ItemLike)Items.f_42482_));
        legs.m_41663_(Enchantments.f_220304_, 3);
        ItemStack boots = ChiefCub.protect(new ItemStack((ItemLike)Items.f_42483_));
        boots.m_41663_(Enchantments.f_44967_, 4);
        boots.m_41663_(Enchantments.f_44973_, 3);
        ItemStack axe = new ItemStack((ItemLike)Items.f_42396_);
        axe.m_41663_(Enchantments.f_44977_, 5);
        axe.m_41663_(Enchantments.f_44981_, 2);
        axe.m_41663_(Enchantments.f_44982_, 3);
        axe.m_41663_(Enchantments.f_44984_, 5);
        axe.m_41663_(Enchantments.f_44986_, 3);
        axe.m_41663_(Enchantments.f_44962_, 1);
        this.m_8061_(EquipmentSlot.HEAD, helmet);
        this.m_8061_(EquipmentSlot.CHEST, chest);
        this.m_8061_(EquipmentSlot.LEGS, legs);
        this.m_8061_(EquipmentSlot.FEET, boots);
        this.m_8061_(EquipmentSlot.MAINHAND, axe);
        this.m_8061_(EquipmentSlot.OFFHAND, new ItemStack((ItemLike)Items.f_42747_));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.m_21409_(slot, 0.0f);
        }
    }

    private static ItemStack protect(ItemStack stack) {
        stack.m_41663_(Enchantments.f_44965_, 4);
        stack.m_41663_(Enchantments.f_44966_, 4);
        stack.m_41663_(Enchantments.f_44968_, 4);
        stack.m_41663_(Enchantments.f_44969_, 4);
        stack.m_41663_(Enchantments.f_44986_, 3);
        stack.m_41663_(Enchantments.f_44962_, 1);
        return stack;
    }

    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        tag.m_128379_("Geared", this.geared);
        tag.m_128405_("DropTimer", this.dropTimer);
    }

    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        this.dropTimer = tag.m_128451_("DropTimer");
        this.geared = tag.m_128471_("Geared");
        this.applyStage(false);
    }

    public EntityDimensions m_6972_(Pose pose) {
        EntityDimensions base = super.m_6972_(pose);
        return this.m_6162_() ? base.m_20388_(0.5f) : base;
    }

    public void m_6710_(@Nullable LivingEntity target) {
        if (target != null && (target == this.m_269323_() || VilaDeFerro.isNeverTarget((Entity)target))) {
            return;
        }
        super.m_6710_(target);
    }

    public boolean m_7757_(LivingEntity target, LivingEntity owner) {
        TamableAnimal pet;
        if (VilaDeFerro.isNeverTarget((Entity)target)) {
            return false;
        }
        if (target instanceof TamableAnimal && (pet = (TamableAnimal)target).m_21824_() && pet.m_269323_() == owner) {
            return false;
        }
        if (target instanceof Player) {
            Player ownerPlayer;
            Player other = (Player)target;
            if (owner instanceof Player && !(ownerPlayer = (Player)owner).m_7099_(other)) {
                return false;
            }
        }
        return true;
    }

    public void m_8119_() {
        IronGolem golem;
        Level level;
        super.m_8119_();
        if (this.m_9236_().f_46443_ || !((level = this.m_9236_()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        if (++this.dropTimer >= 6000) {
            this.dropTimer = 0;
            this.dropGift(serverLevel);
        }
        if (this.m_21827_()) {
            return;
        }
        LivingEntity target = this.m_5448_();
        if (target == null || !target.m_6084_()) {
            return;
        }
        if (--this.summonCooldown > 0) {
            return;
        }
        boolean baby = this.m_6162_();
        this.summonCooldown = baby ? 240 : 160;
        int warriorCap = baby ? 3 : 7;
        int golemCap = baby ? 1 : 2;
        long warriors = serverLevel.m_6443_(FighterVillager.class, this.m_20191_().m_82400_(24.0), f -> !(f instanceof ChiefVillager)).size();
        long golems = serverLevel.m_45976_(IronGolem.class, this.m_20191_().m_82400_(24.0)).size();
        int newWarriors = (int)Math.max(0L, Math.min(baby ? 1L : 3L, (long)warriorCap - warriors));
        for (int i = 0; i < newWarriors; ++i) {
            FighterVillager ally = (FighterVillager)((EntityType)VilaDeFerro.FIGHTER.get()).m_20615_((Level)serverLevel);
            if (ally == null) continue;
            this.place((Mob)ally, i, newWarriors);
            ally.m_6518_((ServerLevelAccessor)serverLevel, serverLevel.m_6436_(ally.m_20183_()), MobSpawnType.EVENT, null, null);
            ally.setTier(Tier.PASSIVE);
            ally.m_6710_(target);
            serverLevel.m_7967_((Entity)ally);
        }
        if (golems < (long)golemCap && (golem = (IronGolem)EntityType.f_20460_.m_20615_((Level)serverLevel)) != null) {
            golem.m_28887_(true);
            Tier.set(golem, Tier.PASSIVE);
            this.place((Mob)golem, 0, 1);
            golem.m_6710_(target);
            serverLevel.m_7967_((Entity)golem);
        }
    }

    private void dropGift(ServerLevel serverLevel) {
        int count = 1 + this.f_19796_.m_188503_(2);
        for (int i = 0; i < count; ++i) {
            int roll = this.f_19796_.m_188503_(100);
            ItemStack gift = roll < 30 ? new ItemStack((ItemLike)Items.f_42436_, 1 + this.f_19796_.m_188503_(2)) : (roll < 55 ? new ItemStack((ItemLike)Items.f_42415_, 1 + this.f_19796_.m_188503_(3)) : (roll < 75 ? EnchantmentHelper.m_220292_((RandomSource)this.f_19796_, (ItemStack)new ItemStack((ItemLike)Items.f_42517_), (int)(15 + this.f_19796_.m_188503_(16)), (boolean)true) : (roll < 90 ? new ItemStack((ItemLike)Items.f_41959_) : (roll < 97 ? new ItemStack((ItemLike)Items.f_42612_, 4 + this.f_19796_.m_188503_(5)) : new ItemStack((ItemLike)Items.f_42437_)))));
            this.m_19983_(gift);
        }
        serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123748_, this.m_20185_(), this.m_20186_() + 1.0, this.m_20189_(), 8, 0.4, 0.5, 0.4, 0.0);
    }

    private void place(Mob mob, int index, int total) {
        double angle = Math.PI * 2 * (double)index / (double)Math.max(1, total) + this.f_19796_.m_188500_() * 0.5;
        mob.m_7678_(this.m_20185_() + Math.cos(angle) * 2.5, this.m_20186_(), this.m_20189_() + Math.sin(angle) * 2.5, this.f_19796_.m_188501_() * 360.0f, 0.0f);
    }

    public boolean m_6898_(ItemStack stack) {
        return false;
    }

    @Nullable
    public AgeableMob m_142606_(ServerLevel level, AgeableMob other) {
        return null;
    }
}

