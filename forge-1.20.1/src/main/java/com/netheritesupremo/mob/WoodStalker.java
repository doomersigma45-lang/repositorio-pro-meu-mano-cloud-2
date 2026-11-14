package com.netheritesupremo.mob;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
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
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * Wood Stalker: criatura magra de madeira escura (3 blocos), 125 HP, 25 de dano. Hostil, ataca qualquer mob/jogador.
 * Agarra a presa e suga a vida (cura a si mesma), quebra blocos, escala paredes, se abaixa sob tetos baixos,
 * tem medo de fogo e agua e se regenera perto de arvores/plantas/madeira. Domesticavel com diamante.
 */
public class WoodStalker extends TamableAnimal implements Enemy {
    public static final double MAX_HEALTH = 125.0D;
    public static final double DAMAGE = 25.0D;
    public static final float DRAIN_DAMAGE = 12.0F;
    public static final int GRAB_TICKS = 100;
    public static final int GRAB_COOLDOWN = 160;
    public static final float DIAMOND_HEAL = 30.0F;
    public static final String FED_TAG = "FedUntil";
    private static final EntityDimensions CROUCH = EntityDimensions.scalable(0.8F, 1.6F);

    private static final EntityDataAccessor<Byte> DATA_FLAGS = SynchedEntityData.defineId(WoodStalker.class, EntityDataSerializers.BYTE);

    private int grabbedId = -1;
    private int grabLeft;
    private int grabCooldown;
    private int breakCooldown;
    private int fearLeft;

    public WoodStalker(EntityType<? extends WoodStalker> type, Level level) {
        super(type, level);
        this.xpReward = 25;
        if (this.fearsHazards()) {
            this.setPathfindingMalus(BlockPathTypes.WATER, 16.0F);
            this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 6.0F);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WallClimberNavigation(this, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS, (byte) 0);
    }

    private boolean flag(int bit) {
        return (this.entityData.get(DATA_FLAGS) & bit) != 0;
    }

    private void setFlag(int bit, boolean on) {
        byte b = this.entityData.get(DATA_FLAGS);
        this.entityData.set(DATA_FLAGS, on ? (byte) (b | bit) : (byte) (b & ~bit));
    }

    public boolean isClimbing() {
        return this.flag(1);
    }

    public boolean isGrabbing() {
        return this.flag(2);
    }

    public boolean isCrouched() {
        return this.getPose() == Pose.CROUCHING;
    }

    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }

    // ---- ganchos para variantes (Ancient Wood, Fungus Stalker)
    protected float drainDamage() {
        return DRAIN_DAMAGE;
    }

    protected int grabTicks() {
        return GRAB_TICKS;
    }

    protected int grabCooldownTicks() {
        return GRAB_COOLDOWN;
    }

    protected int grabChanceBound() {
        return 3;
    }

    protected double holdDistance() {
        return 1.1D;
    }

    protected double holdHeight() {
        return 0.9D;
    }

    protected boolean fearsHazards() {
        return true;
    }

    protected float maxBreakHardness() {
        return 5.0F;
    }

    protected boolean breaksBlockEntities() {
        return false;
    }

    protected int breakHalfWidth() {
        return 0;
    }

    protected int breakHeight() {
        return 2;
    }

    protected int breakInterval() {
        return 10;
    }

    protected float regenMultiplier() {
        return 1.0F;
    }

    protected EntityDimensions crouchDimensions() {
        return CROUCH;
    }

    protected boolean isRegenBlock(BlockState s) {
        return s.is(BlockTags.LOGS) || s.is(BlockTags.LEAVES) || s.is(BlockTags.PLANKS) || s.is(BlockTags.SAPLINGS)
                || s.is(BlockTags.FLOWERS) || s.is(BlockTags.CROPS) || s.is(BlockTags.REPLACEABLE_BY_TREES);
    }

    protected void onHitVictim(LivingEntity victim) {
    }

    /** Alvos que o Wood Stalker nunca ataca: a propria especie e os Fungus. */
    protected boolean isFriend(Entity e) {
        return e instanceof WoodStalker || e instanceof Fungus;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return pose == Pose.CROUCHING ? this.crouchDimensions() : super.getDimensions(pose);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FleeHazardGoal());
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.1D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new MountFungusGoal());
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        // selvagem: qualquer mob e jogador; domesticado: so inimigos
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 5, true, false,
                e -> (e instanceof Mob || e instanceof Player) && !this.isFriend(e)
                        && (this.isTame() ? e instanceof Enemy && !(e instanceof TamableAnimal t && t.isTame()) : true)));
    }

    // ------------------------------------------------------------------ domesticacao
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.DIAMOND)) {
            if (!this.level().isClientSide) {
                if (!this.isTame()) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    if (this.random.nextInt(3) == 0) {
                        this.tame(player);
                        this.releaseGrab();
                        this.navigation.stop();
                        this.setTarget(null);
                        this.setOrderedToSit(false);
                        this.level().broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    }
                } else if (this.getHealth() < this.getMaxHealth()) {
                    this.heal(DIAMOND_HEAL);
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
    public boolean removeWhenFarAway(double distance) {
        return !this.isTame();
    }

    @Override
    public boolean shouldDespawnInPeaceful() {
        return !this.isTame();
    }

    // ------------------------------------------------------------------ agarrar e sugar
    @Nullable
    private LivingEntity grabbed() {
        if (this.grabbedId < 0) {
            return null;
        }
        Entity e = this.level().getEntity(this.grabbedId);
        return e instanceof LivingEntity le && le.isAlive() ? le : null;
    }

    private void releaseGrab() {
        this.grabbedId = -1;
        this.grabLeft = 0;
        this.grabCooldown = this.grabCooldownTicks();
        this.setFlag(2, false);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity victim && victim.isAlive() && this.grabbedId < 0 && this.grabCooldown <= 0
                && this.fearLeft <= 0 && this.random.nextInt(this.grabChanceBound()) == 0 && !this.isFriend(victim)) {
            this.grabbedId = victim.getId();
            this.grabLeft = this.grabTicks();
            this.setFlag(2, true);
            this.playSound(SoundEvents.RAVAGER_ROAR, 1.0F, 1.6F);
        }
        if (hit && target instanceof LivingEntity v && !this.isFriend(v)) {
            this.onHitVictim(v);
        }
        return hit;
    }

    private void tickGrab() {
        LivingEntity victim = this.grabbed();
        if (victim == null || --this.grabLeft <= 0 || this.fearLeft > 0 || this.isOrderedToSit()) {
            if (this.grabbedId >= 0) {
                this.releaseGrab();
            }
            return;
        }
        this.getNavigation().stop();
        Vec3 look = Vec3.directionFromRotation(0.0F, this.getYRot()).normalize();
        Vec3 hold = this.position().add(look.scale(this.holdDistance())).add(0.0D, this.holdHeight(), 0.0D);
        victim.setDeltaMovement(Vec3.ZERO);
        victim.fallDistance = 0.0F;
        if (victim.position().distanceToSqr(hold) > 0.25D && this.tickCount % 2 == 0) {
            if (victim instanceof ServerPlayer sp) {
                sp.connection.teleport(hold.x, hold.y, hold.z, sp.getYRot(), sp.getXRot());
            } else {
                victim.setPos(hold.x, hold.y, hold.z);
            }
        }
        this.getLookControl().setLookAt(victim, 30.0F, 30.0F);
        if (this.grabLeft % 20 == 0) {
            float before = victim.getHealth();
            victim.hurt(this.damageSources().mobAttack(this), this.drainDamage());
            float drained = Math.max(0.0F, before - victim.getHealth());
            this.heal(Math.max(drained, this.drainDamage() * 0.5F));
            this.playSound(SoundEvents.PHANTOM_BITE, 1.0F, 0.5F);
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 1.8D, this.getZ(), 6, 0.3D, 0.4D, 0.3D, 0.0D);
            }
        }
    }

    // ------------------------------------------------------------------ tick
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (this.grabCooldown > 0) {
            this.grabCooldown--;
        }
        if (this.fearLeft > 0) {
            this.fearLeft--;
        }
        if (this.breakCooldown > 0) {
            this.breakCooldown--;
        }
        this.setFlag(1, this.horizontalCollision && !this.isInWaterOrBubble());
        this.tickGrab();
        if (this.tickCount % 5 == 0) {
            this.updateCrouch();
        }
        if (this.tickCount % 20 == 0) {
            this.regenerate(level);
            if (this.getClass() == WoodStalker.class) {
                this.tryCross(level);
            }
        }
        if (this.breakCooldown <= 0 && this.getTarget() != null && this.grabbedId < 0 && (this.horizontalCollision || this.getNavigation().isStuck())) {
            this.breakCooldown = this.breakInterval();
            this.breakBlocksTowards(level, this.getTarget());
        }
    }

    private void updateCrouch() {
        EntityDimensions stand = this.getType().getDimensions();
        Vec3 pos = this.position();
        AABB standBox = stand.makeBoundingBox(pos);
        if (this.isCrouched()) {
            if (this.level().noCollision(this, standBox)) {
                this.setPose(Pose.STANDING);
            }
            return;
        }
        Vec3 dir = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D);
        if (dir.lengthSqr() < 1.0E-4D) {
            dir = Vec3.directionFromRotation(0.0F, this.getYRot());
        }
        dir = dir.normalize().scale(0.8D);
        boolean blockedHere = !this.level().noCollision(this, standBox);
        boolean blockedAhead = !this.level().noCollision(this, standBox.move(dir));
        if ((blockedHere || blockedAhead) && this.level().noCollision(this, this.crouchDimensions().makeBoundingBox(pos.add(dir)))) {
            this.setPose(Pose.CROUCHING);
        }
    }

    private void regenerate(ServerLevel level) {
        if (this.getHealth() >= this.getMaxHealth()) {
            return;
        }
        BlockPos c = this.blockPosition();
        int found = 0;
        for (BlockPos p : BlockPos.betweenClosed(c.offset(-4, -1, -4), c.offset(4, 4, 4))) {
            BlockState s = level.getBlockState(p);
            if (this.isRegenBlock(s)) {
                if (++found >= 6) {
                    break;
                }
            }
        }
        if (found > 0) {
            this.heal((found >= 6 ? 6.0F : found >= 3 ? 3.0F : 1.0F) * this.regenMultiplier());
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 1.5D, this.getZ(), 3, 0.3D, 0.6D, 0.3D, 0.0D);
        }
    }

    /** Quebra blocos (dureza ate 5) no caminho em direcao ao alvo. */
    private void breakBlocksTowards(ServerLevel level, LivingEntity target) {
        if (!ForgeEventFactory.getMobGriefingEvent(level, this)) {
            return;
        }
        Vec3 d = target.position().subtract(this.position());
        Direction dir = Math.abs(d.x) > Math.abs(d.z) ? (d.x > 0 ? Direction.EAST : Direction.WEST) : (d.z > 0 ? Direction.SOUTH : Direction.NORTH);
        BlockPos base = this.blockPosition().relative(dir);
        int top = this.isCrouched() ? Math.max(1, this.breakHeight() / 2) : this.breakHeight();
        if (d.y > 1.5D) {
            top += 1;
        }
        Direction side = dir.getClockWise();
        for (int w = -this.breakHalfWidth(); w <= this.breakHalfWidth(); w++) {
            for (int dy = 0; dy <= top; dy++) {
                BlockPos p = base.relative(side, w).above(dy);
                BlockState s = level.getBlockState(p);
                float h = s.getDestroySpeed(level, p);
                if (!s.isAir() && h >= 0.0F && h <= this.maxBreakHardness() && (this.breaksBlockEntities() || !s.hasBlockEntity())
                        && s.getFluidState().isEmpty()) {
                    level.destroyBlock(p, true, this);
                }
            }
        }
    }

    // ------------------------------------------------------------------ cruzamento e montaria
    private void tryCross(ServerLevel level) {
        long now = level.getGameTime();
        if (this.getPersistentData().getLong(FED_TAG) <= now) {
            return;
        }
        Fungus mate = null;
        for (Fungus f : level.getEntitiesOfClass(Fungus.class, this.getBoundingBox().inflate(8.0D),
                f -> f.getPersistentData().getLong(FED_TAG) > now)) {
            mate = f;
            break;
        }
        if (mate == null) {
            return;
        }
        if (this.distanceToSqr(mate) > 9.0D) {
            this.getNavigation().moveTo(mate, 1.0D);
            return;
        }
        FungusStalker child = com.netheritesupremo.registry.ModEntities.FUNGUS_STALKER.get().create(level);
        if (child == null) {
            return;
        }
        child.moveTo((this.getX() + mate.getX()) / 2.0D, this.getY(), (this.getZ() + mate.getZ()) / 2.0D, this.random.nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(child);
        this.getPersistentData().putLong(FED_TAG, 0L);
        mate.getPersistentData().putLong(FED_TAG, 0L);
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.HEART, child.getX(), child.getY() + 1.5D, child.getZ(), 12, 0.6D, 0.8D, 0.6D, 0.0D);
    }

    /** Raramente, um Wood Stalker selvagem sobe nas costas de um Fungus proximo. */
    private class MountFungusGoal extends Goal {
        private Fungus fungus;

        MountFungusGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (WoodStalker.this.getClass() != WoodStalker.class || WoodStalker.this.isPassenger() || WoodStalker.this.isTame()
                    || WoodStalker.this.getTarget() != null || WoodStalker.this.random.nextInt(600) != 0) {
                return false;
            }
            this.fungus = null;
            for (Fungus f : WoodStalker.this.level().getEntitiesOfClass(Fungus.class, WoodStalker.this.getBoundingBox().inflate(10.0D),
                    f -> f.isAlive() && f.getPassengers().isEmpty())) {
                this.fungus = f;
                break;
            }
            return this.fungus != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.fungus != null && this.fungus.isAlive() && this.fungus.getPassengers().isEmpty() && !WoodStalker.this.isPassenger()
                    && WoodStalker.this.getTarget() == null;
        }

        @Override
        public void tick() {
            if (WoodStalker.this.distanceToSqr(this.fungus) < 5.0D) {
                WoodStalker.this.startRiding(this.fungus);
            } else if (WoodStalker.this.tickCount % 10 == 0) {
                WoodStalker.this.getNavigation().moveTo(this.fungus, 1.1D);
            }
        }
    }

    // ------------------------------------------------------------------ medo de fogo e agua
    private boolean isHazard(BlockState s) {
        return s.is(BlockTags.FIRE) || s.is(BlockTags.CAMPFIRES) || s.getFluidState().is(FluidTags.LAVA) || s.getFluidState().is(FluidTags.WATER);
    }

    private class FleeHazardGoal extends Goal {
        private Vec3 away = Vec3.ZERO;

        FleeHazardGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!WoodStalker.this.fearsHazards() || WoodStalker.this.tickCount % 5 != 0) {
                return false;
            }
            if (WoodStalker.this.isOnFire() || WoodStalker.this.isInWaterOrBubble()) {
                this.away = WoodStalker.this.position();
                return true;
            }
            BlockPos c = WoodStalker.this.blockPosition();
            for (BlockPos p : BlockPos.betweenClosed(c.offset(-3, -1, -3), c.offset(3, 2, 3))) {
                if (WoodStalker.this.isHazard(WoodStalker.this.level().getBlockState(p))) {
                    this.away = Vec3.atCenterOf(p);
                    return true;
                }
            }
            return false;
        }

        @Override
        public void start() {
            WoodStalker.this.fearLeft = 40;
            WoodStalker.this.stopRiding();
            WoodStalker.this.setTarget(null);
            Vec3 to = DefaultRandomPos.getPosAway(WoodStalker.this, 12, 6, this.away);
            if (to != null) {
                WoodStalker.this.getNavigation().moveTo(to.x, to.y, to.z, 1.6D);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return WoodStalker.this.fearLeft > 0 && !WoodStalker.this.getNavigation().isDone();
        }
    }

    // ------------------------------------------------------------------ resistencias
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(net.minecraft.world.damagesource.DamageTypes.DROWN)) {
            return false;
        }
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE)) {
            amount *= 0.25F;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return super.causeFallDamage(distance, multiplier * 0.25F, source);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    // ------------------------------------------------------------------ misc
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
        return SoundEvents.WOOD_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOOD_BREAK;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.WOOD_STEP, 0.8F, 0.7F);
    }

    /** Florestas, de qualquer luz (como um monstro comum, mas nunca com o dia aberto em campo aberto longe de arvores). */
    public static <T extends WoodStalker> boolean checkSpawnRules(EntityType<T> type, ServerLevelAccessor level, MobSpawnType reason,
            BlockPos pos, RandomSource random) {
        if (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND || reason == MobSpawnType.SPAWNER) {
            return true;
        }
        return Monster.checkAnyLightMonsterSpawnRules(EntityType.ZOMBIE, level, reason, pos, random);
    }
}
