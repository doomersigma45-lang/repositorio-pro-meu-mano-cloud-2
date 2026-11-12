package com.netheritesupremo.mob;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Mosquito gigante: ataca qualquer mob (jogadores so se o atacarem), grudando no alvo; enquanto gruda,
 * chupa 7 de dano por segundo ate o alvo morrer. Domesticavel com carne podre; o dono monta (voo rapido)
 * e abre o inventario proprio (27 espacos) agachado + clique direito.
 */
public class GiantMosquito extends TamableAnimal {
    public static final double MAX_HEALTH = 120.0D;
    public static final double BITE_DAMAGE = 5.0D;
    public static final float SUCK_DAMAGE = 7.0F;
    public static final int SUCK_INTERVAL = 20;

    private final SimpleContainer inventory = new SimpleContainer(27);
    @Nullable
    private UUID attachedId;
    private int suckTimer;

    public GiantMosquito(EntityType<? extends GiantMosquito> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoGravity(true);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BITE_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FLYING_SPEED, 1.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.2D, 10.0F, 3.0F, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                e -> !this.isTame() && !(e instanceof GiantMosquito) && !(e instanceof Player)
                        && !(e instanceof ArmorStand) && e.isAlive()));
    }

    // ------------------------------------------------------------------ interacao
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.isTame()) {
            if (stack.is(Items.ROTTEN_FLESH)) {
                if (!this.level().isClientSide) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    if (this.random.nextInt(3) == 0) {
                        this.tame(player);
                        this.navigation.stop();
                        this.setTarget(null);
                        this.attachedId = null;
                        this.level().broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            return InteractionResult.PASS;
        }
        if (!this.isOwnedBy(player) || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        if (stack.is(Items.ROTTEN_FLESH) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.heal(6.0F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (!this.level().isClientSide) {
            if (player.isSecondaryUseActive()) {
                player.openMenu(new SimpleMenuProvider((id, inv, p) -> ChestMenu.threeRows(id, inv, this.inventory),
                        this.getDisplayName()));
            } else if (!this.isVehicle()) {
                this.attachedId = null;
                player.startRiding(this);
            }
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.isTame() && this.getFirstPassenger() instanceof Player p && this.isOwnedBy(p) ? p : null;
    }

    @Override
    public void travel(Vec3 input) {
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player rider) {
            this.setYRot(rider.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(rider.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;
            if (this.isControlledByLocalInstance()) {
                float forward = rider.zza;
                Vec3 look = rider.getLookAngle();
                Vec3 motion = this.getDeltaMovement().scale(0.9D);
                if (forward > 0.0F) {
                    motion = motion.add(look.scale(0.1D * forward));
                } else if (forward < 0.0F) {
                    motion = motion.scale(0.8D);
                }
                if (rider.xxa != 0.0F) {
                    Vec3 right = look.cross(new Vec3(0.0D, 1.0D, 0.0D)).normalize();
                    motion = motion.add(right.scale(-rider.xxa * 0.05D));
                }
                this.setDeltaMovement(motion);
                this.move(MoverType.SELF, motion);
            }
            this.calculateEntityAnimation(true);
            return;
        }
        super.travel(input);
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

    // ------------------------------------------------------------------ ataque e sugar
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity le && le.isAlive() && !this.isVehicle()
                && !(this.isTame() && this.isOwnedBy(le))) {
            this.attachedId = le.getUUID();
            this.suckTimer = 0;
        }
        return hit;
    }

    @Nullable
    private LivingEntity attached() {
        if (this.attachedId != null && this.level() instanceof ServerLevel sl
                && sl.getEntity(this.attachedId) instanceof LivingEntity e && e.isAlive()) {
            return e;
        }
        return null;
    }

    public boolean isAttached() {
        return this.attachedId != null;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.tickCount % 20 == 0 && !this.isVehicle()) {
            this.playSound(SoundEvents.BEE_LOOP, 0.5F, 0.5F);
        }
        if (this.attachedId == null) {
            return;
        }
        LivingEntity victim = this.attached();
        if (victim == null || this.isVehicle()) {
            this.attachedId = null;
            this.setTarget(null);
            return;
        }
        double behind = victim.getBbWidth() * 0.5D + 0.5D;
        double yaw = Math.toRadians(victim.getYRot());
        this.moveTo(victim.getX() + Math.sin(yaw) * behind, victim.getY() + victim.getBbHeight() * 0.6D,
                victim.getZ() - Math.cos(yaw) * behind, victim.getYRot(), 0.0F);
        this.setDeltaMovement(Vec3.ZERO);
        this.getNavigation().stop();
        this.setTarget(victim);
        if (++this.suckTimer % SUCK_INTERVAL == 0) {
            victim.invulnerableTime = 0;
            victim.hurt(this.damageSources().mobAttack(this), SUCK_DAMAGE);
            this.heal(2.0F);
            this.playSound(SoundEvents.HONEY_DRINK, 1.0F, 0.6F);
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    // ------------------------------------------------------------------ dados
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("MosquitoInventory", this.inventory.createTag());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.inventory.fromTag(tag.getList("MosquitoInventory", 10));
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        Containers.dropContents(this.level(), this, this.inventory);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BEE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BEE_DEATH;
    }
}
