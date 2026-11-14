package com.netheritesupremo.mob;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Filhote do Cavaleiro sem Cabeca: 470 HP, 35 de dano. Age como um lobo domesticado (segue, senta, protege e ataca os
 * inimigos do dono) e troca de armadura sozinho quando acha no chao uma melhor (ou quando o dono da uma na mao).
 */
public class HeadlessPup extends TamableAnimal implements RangedAttackMob {
    public static final double MAX_HEALTH = 470.0D;
    public static final double DAMAGE = 35.0D;

    public HeadlessPup(EntityType<? extends HeadlessPup> type, Level level) {
        super(type, level);
        this.xpReward = 0;
        this.setCanPickUpLoot(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.1D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false,
                e -> this.isTame() && e instanceof Enemy && !(e instanceof HeadlessPup) && !(e instanceof TamableAnimal t && t.isTame())));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
            @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        data = super.finalizeSpawn(level, difficulty, reason, data, tag);
        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.JACK_O_LANTERN));
        }
        this.setDropChance(EquipmentSlot.HEAD, 0.0F);
        for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            this.setDropChance(slot, 1.0F);          // a armadura trocada nao some: cai no chao
        }
        return data;
    }

    /** So pega armaduras do chao, e so se forem melhores que a que ja usa. */
    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getEquipmentSlot() != EquipmentSlot.HEAD
                && super.wantsToPickUp(stack);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.BONE)) {
            if (!this.level().isClientSide) {
                if (!this.isTame()) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    if (this.random.nextInt(3) == 0) {
                        this.tame(player);
                        this.navigation.stop();
                        this.setTarget(null);
                        this.setOrderedToSit(false);
                        this.level().broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    }
                } else if (this.getHealth() < this.getMaxHealth()) {
                    this.heal(40.0F);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (this.isTame() && this.isOwnedBy(player) && stack.getItem() instanceof ArmorItem armor
                && armor.getEquipmentSlot() != EquipmentSlot.HEAD) {
            ItemStack cur = this.getItemBySlot(armor.getEquipmentSlot());
            if (this.canReplaceCurrentItem(stack, cur)) {
                if (!this.level().isClientSide) {
                    this.setItemSlot(armor.getEquipmentSlot(), stack.split(1));
                    if (!cur.isEmpty() && !player.getInventory().add(cur)) {
                        this.spawnAtLocation(cur);
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        if (this.isTame() && this.isOwnedBy(player) && hand == InteractionHand.MAIN_HAND) {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.jumping = false;
                this.navigation.stop();
                this.setTarget(null);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && this.isTame() && this.isOwnedBy(target)) {
            return;
        }
        super.setTarget(target);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof PumpkinBomb) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canMate(net.minecraft.world.entity.animal.Animal other) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return !this.isTame();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SKELETON_STEP, 0.15F, 1.3F);
    }
}
