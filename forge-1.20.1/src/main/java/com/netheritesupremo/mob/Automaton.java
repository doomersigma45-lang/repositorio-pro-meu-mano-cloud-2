package com.netheritesupremo.mob;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Automato: neutro, 200 HP, 14 de dano. Achado em estruturas abandonadas (minas, portais em ruinas);
 * domesticavel com ferro. Domesticado segue e protege o dono, atacando esqueletos, zumbis e outros monstros.
 */
public class Automaton extends TamableAnimal {
    public static final double MAX_HEALTH = 200.0D;
    public static final double DAMAGE = 14.0D;
    public static final float IRON_HEAL = 25.0F;

    public Automaton(EntityType<? extends Automaton> type, Level level) {
        super(type, level);
        this.xpReward = 30;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.1D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false,
                e -> this.isTame() && e instanceof Enemy && !(e instanceof Automaton)));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.IRON_INGOT)) {
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
                    this.heal(IRON_HEAL);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
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
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    /** Naturalmente so dentro de minas abandonadas ou portais em ruinas. */
    public static boolean checkSpawnRules(EntityType<Automaton> type, ServerLevelAccessor level, MobSpawnType reason,
            BlockPos pos, RandomSource random) {
        if (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND || reason == MobSpawnType.SPAWNER) {
            return true;
        }
        if (reason != MobSpawnType.NATURAL || random.nextInt(4) != 0
                || !Monster.checkAnyLightMonsterSpawnRules(EntityType.ZOMBIE, level, reason, pos, random)) {
            return false;
        }
        ServerLevel sl = level.getLevel();
        return sl.structureManager().getStructureWithPieceAt(pos, StructureTags.MINESHAFT).isValid()
                || sl.structureManager().getStructureWithPieceAt(pos, StructureTags.RUINED_PORTAL).isValid();
    }
}
