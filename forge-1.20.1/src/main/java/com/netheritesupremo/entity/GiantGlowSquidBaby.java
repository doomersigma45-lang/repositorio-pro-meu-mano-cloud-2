package com.netheritesupremo.entity;

import java.util.EnumSet;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Filhote domesticado da Lula Brilhante Gigante: tem o triplo das estatisticas do boss (2100 de vida,
 * 32,1 de dano de tinta), segue o dono, ataca monstros e pode ser montado (clique direito).
 */
public class GiantGlowSquidBaby extends AbstractGiantSquid {
    public static final double MAX_HEALTH = GiantGlowSquid.MAX_HEALTH * 3.0D;
    public static final float INK_DAMAGE = GiantGlowSquid.INK_DAMAGE * 3.0F;

    @Nullable
    private UUID ownerId;

    public GiantGlowSquidBaby(EntityType<? extends GlowSquid> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createBabyAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, 60.0D)
                .add(Attributes.ARMOR, 30.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    public float inkDamage() {
        return INK_DAMAGE;
    }

    // ---------------------------------------------------------------------- dono
    public void setOwner(@Nullable Player player) {
        this.ownerId = player == null ? null : player.getUUID();
    }

    public boolean isOwner(Entity entity) {
        return this.ownerId != null && entity.getUUID().equals(this.ownerId);
    }

    @Nullable
    public Player getOwnerPlayer() {
        return this.ownerId == null ? null : this.level().getPlayerByUUID(this.ownerId);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !this.isOwner(target) && super.canAttack(target);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.ownerId != null) {
            tag.putUUID("Owner", this.ownerId);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.ownerId = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
    }

    // ------------------------------------------------------------------ montaria
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.ownerId == null && stack.is(Items.GLOW_INK_SAC)) {
            if (!this.level().isClientSide) {
                this.setOwner(player);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (this.isOwner(player) && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                player.startRiding(this);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getBbHeight() * 0.85D;
    }

    /** Com o dono montado: nada para onde ele olha (W anda, S da re). */
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.getFirstPassenger() instanceof Player rider) {
            this.setTarget(null);
            float forward = rider.zza;
            if (Math.abs(forward) > 0.01F) {
                Vec3 v = rider.getLookAngle().scale(forward * 0.7D);
                this.setMovementVector((float) v.x, (float) v.y, (float) v.z);
            } else {
                this.setMovementVector(0.0F, 0.0F, 0.0F);
            }
            this.setYRot(rider.getYRot());
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, 10, true, false,
                e -> this.ownerId != null && !(e instanceof AbstractGiantSquid)));
    }

    static class FollowOwnerGoal extends Goal {
        private final GiantGlowSquidBaby squid;

        FollowOwnerGoal(GiantGlowSquidBaby squid) {
            this.squid = squid;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            Player owner = this.squid.getOwnerPlayer();
            return owner != null && this.squid.getTarget() == null && !this.squid.isVehicle()
                    && this.squid.distanceToSqr(owner) > 100.0D;
        }

        @Override
        public boolean canContinueToUse() {
            Player owner = this.squid.getOwnerPlayer();
            return owner != null && this.squid.getTarget() == null && !this.squid.isVehicle()
                    && this.squid.distanceToSqr(owner) > 16.0D;
        }

        @Override
        public void tick() {
            Player owner = this.squid.getOwnerPlayer();
            if (owner == null) {
                return;
            }
            Vec3 to = owner.position().add(0.0D, owner.getBbHeight() * 0.5D, 0.0D)
                    .subtract(this.squid.position()).normalize().scale(0.5D);
            this.squid.setMovementVector((float) to.x, (float) to.y, (float) to.z);
        }
    }
}
