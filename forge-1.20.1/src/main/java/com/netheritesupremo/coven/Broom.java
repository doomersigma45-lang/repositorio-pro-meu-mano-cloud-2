package com.netheritesupremo.coven;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;

/**
 * Vassoura voadora: clique direito para montar, W/S aceleram (ou freiam) na direcao em que o jogador olha, agachar
 * desmonta. Bater nela devolve o item.
 */
public class Broom extends Entity {
    private static final double SPEED = 0.55D;

    public Broom(EntityType<? extends Broom> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (!this.level().isClientSide) {
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isRemoved() || this.level().isClientSide) {
            return true;
        }
        if (source.getEntity() instanceof Player) {
            this.ejectPassengers();
            this.spawnAtLocation(new ItemStack(Coven.BROOM_ITEM.get()));
            this.discard();
        }
        return true;
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        Entity first = this.getFirstPassenger();
        return first instanceof LivingEntity living ? living : null;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.25D;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty();
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity rider = this.getControllingPassenger();
        if (rider != null) {
            this.setYRot(rider.getYRot());
            this.yRotO = this.getYRot();
            if (this.isControlledByLocalInstance()) {
                Vec3 look = rider.getLookAngle();
                float forward = rider.zza;
                Vec3 push = forward > 0.0F ? look.scale(SPEED * 0.12D)
                        : forward < 0.0F ? look.scale(-SPEED * 0.05D) : Vec3.ZERO;
                Vec3 motion = this.getDeltaMovement().scale(0.92D).add(push);
                if (rider.isShiftKeyDown()) {
                    motion = motion.add(0.0D, -0.05D, 0.0D);
                }
                double max = SPEED * 1.6D;
                if (motion.length() > max) {
                    motion = motion.normalize().scale(max);
                }
                this.setDeltaMovement(motion);
                this.move(MoverType.SELF, this.getDeltaMovement());
            }
        } else {
            // sem piloto: desce devagar ate o chao e para
            Vec3 motion = this.getDeltaMovement().scale(0.8D);
            if (!this.onGround()) {
                motion = motion.add(0.0D, -0.02D, 0.0D);
            }
            this.setDeltaMovement(motion);
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
    }
}
