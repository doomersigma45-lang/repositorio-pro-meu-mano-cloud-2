package com.netheritesupremo.skeleton;

import com.netheritesupremo.registry.ModItems;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;

/**
 * Esqueleto villager: esqueleto de terno que negocia (moeda: pepitas de ferro ou ouro). E pacifico, mas
 * revida com a Old Gun se for atacado, e os outros esqueletos vem ajuda-lo.
 */
public class SkeletonVillager extends AbstractVillager implements RangedAttackMob {

    public SkeletonVillager(EntityType<? extends SkeletonVillager> type, Level level) {
        super(type, level);
        this.xpReward = 5;
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.OLD_GUN.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        if (this.getNavigation() instanceof GroundPathNavigation nav) {
            nav.setCanOpenDoors(true);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(2, new GunAttackGoal(this, 1.0D, 4.0D, 20.0D));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(4, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 0.6D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        SkeletonFaction.fireGun(this, target);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    // ------------------------------------------------------------------ trocas
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof SpawnEggItem || !this.isAlive() || this.isTrading()
                || this.getTarget() != null || this.getOffers().isEmpty()) {
            return super.mobInteract(player, hand);
        }
        if (!this.level().isClientSide) {
            this.setTradingPlayer(player);
            this.openTradingScreen(player, this.getDisplayName(), 1);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    protected void updateTrades() {
        SkeletonTrades.addTrades(this.getOffers());
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5D, this.getZ(),
                    3 + this.random.nextInt(4)));
        }
    }

    /** Fica perto de onde nasceu e reabastece as trocas a cada ~5 minutos. */
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        if (this.tickCount == 40 && !this.hasRestriction()) {
            this.restrictTo(this.blockPosition(), 24);
        }
        if (this.tickCount % 6000 == 0 && !this.isTrading()) {
            for (MerchantOffer offer : this.getOffers()) {
                offer.resetUses();
            }
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return null;
    }

    @Override
    public boolean removeWhenFarAway(double distanceSqr) {
        return false;
    }

    // ------------------------------------------------------------------ sons
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }
}
