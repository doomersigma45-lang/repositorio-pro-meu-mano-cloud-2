package com.netheritesupremo.mob;

import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import com.netheritesupremo.registry.ModEntities;
import com.netheritesupremo.registry.ModItems;

/**
 * Cavaleiro sem Cabeca (boss): esqueleto preto e alto com abobora no lugar da cabeca, armadura de obsidiana (sem
 * capacete), espada propria (57 de dano, Veneno II 5 s). Montado num cavalo esqueleto: investe, agarra, solta aboboras
 * explosivas com veneno, quebra blocos e puxa quem esta longe com uma corrente. Sem o cavalo, monta qualquer mob proximo;
 * sem mobs por perto, continua a pe. 2500 HP. Ao morrer deixa a espada e um filhote (que age como lobo domesticado).
 */
public class HeadlessHorseman extends Monster implements RangedAttackMob {
    public static final double MAX_HEALTH = 2500.0D;
    public static final float MAX_BREAK_HARDNESS = 10.0F;

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_10);
    private int grabbedId = -1;
    private int grabLeft;
    private int chargeLeft;
    private int chargeCooldown = 100;
    private int bombCooldown = 60;
    private int chainCooldown = 120;
    private int breakCooldown;
    private int mountCooldown;
    private int attackCooldown;

    public HeadlessHorseman(EntityType<? extends HeadlessHorseman> type, Level level) {
        super(type, level);
        this.xpReward = 700;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        // dano base 1 + 56 da espada na mao = 57
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 0, false, false, null));
    }

    // ------------------------------------------------------------------ equipamento e montaria
    private void equip() {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.HORSEMAN_SWORD.get()));
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.JACK_O_LANTERN));
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.OBSIDIAN_CHESTPLATE.get()));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.OBSIDIAN_LEGGINGS.get()));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.OBSIDIAN_BOOTS.get()));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.setDropChance(slot, 0.0F);       // o drop vem do loot table
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
            @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        data = super.finalizeSpawn(level, difficulty, reason, data, tag);
        this.equip();
        HorsemanSteed steed = ModEntities.HORSEMAN_STEED.get().create(level.getLevel());
        if (steed != null) {
            steed.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            this.startRiding(steed);
            level.addFreshEntity(steed);
        }
        return data;
    }

    private boolean isMountCandidate(Mob m) {
        return m != this && m.isAlive() && !m.isPassenger() && m.getPassengers().isEmpty()
                && !(m instanceof HeadlessHorseman) && !(m instanceof HeadlessPup) && !(m instanceof WitherBoss)
                && !(m instanceof EnderDragon) && m.getBbHeight() >= 0.9F && m.getBbWidth() <= 3.0F;
    }

    private void tryFindMount(ServerLevel level) {
        List<Mob> list = level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(24.0D), this::isMountCandidate);
        if (list.isEmpty()) {
            this.mountCooldown = 60;         // sem mob por perto: segue atacando a pe
            return;
        }
        Mob nearest = list.stream().min(Comparator.comparingDouble(this::distanceToSqr)).get();
        if (this.distanceToSqr(nearest) < 6.0D) {
            this.startRiding(nearest, true);
            this.playSound(SoundEvents.SKELETON_HORSE_AMBIENT, 1.0F, 0.6F);
        } else {
            this.getNavigation().moveTo(nearest, 1.3D);
        }
    }

    // ------------------------------------------------------------------ vitimas
    private boolean isVictim(LivingEntity e) {
        if (e == this || e == this.getVehicle() || !e.isAlive() || e instanceof HeadlessHorseman || e instanceof HeadlessPup
                || e instanceof HorsemanSteed) {
            return false;
        }
        return !(e instanceof Player p) || (!p.isCreative() && !p.isSpectator());
    }

    @Nullable
    private LivingEntity grabbed() {
        if (this.grabbedId < 0) {
            return null;
        }
        Entity e = this.level().getEntity(this.grabbedId);
        return e instanceof LivingEntity le && le.isAlive() ? le : null;
    }

    private void grab(LivingEntity v) {
        this.grabbedId = v.getId();
        this.grabLeft = 70;
        this.playSound(SoundEvents.RAVAGER_ROAR, 1.2F, 0.6F);
    }

    private void throwVictim(@Nullable LivingEntity v) {
        if (v != null) {
            Vec3 look = Vec3.directionFromRotation(0.0F, this.getYRot());
            v.setDeltaMovement(look.x * 1.8D, 0.7D, look.z * 1.8D);
            v.hurtMarked = true;
        }
        this.grabbedId = -1;
        this.grabLeft = 0;
    }

    private void tickGrab() {
        if (this.grabbedId < 0) {
            return;
        }
        LivingEntity v = this.grabbed();
        if (v == null || --this.grabLeft <= 0) {
            this.throwVictim(v);
            return;
        }
        Vec3 look = Vec3.directionFromRotation(0.0F, this.getYRot()).normalize();
        Vec3 hold = this.position().add(look.scale(1.8D)).add(0.0D, this.isPassenger() ? 1.4D : 1.0D, 0.0D);
        v.setDeltaMovement(Vec3.ZERO);
        v.fallDistance = 0.0F;
        if (v.position().distanceToSqr(hold) > 0.25D && this.tickCount % 2 == 0) {
            if (v instanceof ServerPlayer sp) {
                sp.connection.teleport(hold.x, hold.y, hold.z, sp.getYRot(), sp.getXRot());
            } else {
                v.setPos(hold.x, hold.y, hold.z);
            }
        }
        if (this.grabLeft % 20 == 0) {
            v.hurt(this.damageSources().mobAttack(this), 10.0F);
            v.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
        }
    }

    // ------------------------------------------------------------------ habilidades
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity le) {
            le.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1), this);
        }
        return hit;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        PumpkinBomb bomb = new PumpkinBomb(this.level(), this);
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double dy = target.getY(0.33D) - bomb.getY();
        double h = Math.sqrt(dx * dx + dz * dz);
        bomb.shoot(dx, dy + h * 0.2D, dz, 1.4F, 3.0F);
        this.level().addFreshEntity(bomb);
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.5F);
    }

    private void pullWithChain(ServerLevel level, LivingEntity target) {
        Vec3 to = this.position().add(0.0D, 1.5D, 0.0D);
        Vec3 from = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        Vec3 diff = to.subtract(from);
        double len = diff.length();
        BlockParticleOption chain = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CHAIN.defaultBlockState());
        for (double d = 0.0D; d < len; d += 0.6D) {
            Vec3 p = from.add(diff.scale(d / len));
            level.sendParticles(chain, p.x, p.y, p.z, 2, 0.05D, 0.05D, 0.05D, 0.0D);
        }
        Vec3 dir = diff.multiply(1.0D, 0.0D, 1.0D).normalize();
        double force = Math.min(2.2D, len * 0.12D);
        target.setDeltaMovement(dir.x * force, 0.4D, dir.z * force);
        target.hurtMarked = true;
        target.hurt(this.damageSources().mobAttack(this), 4.0F);
        this.playSound(SoundEvents.CHAIN_BREAK, 2.0F, 0.6F);
        this.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
    }

    private void tickCharge(ServerLevel level, LivingEntity target) {
        Entity mount = this.getVehicle();
        if (mount == null || !target.isAlive()) {
            this.chargeLeft = 0;
            return;
        }
        Vec3 d = target.position().subtract(mount.position());
        Vec3 flat = new Vec3(d.x, 0.0D, d.z);
        if (flat.lengthSqr() < 0.25D) {
            this.chargeLeft = 0;
            return;
        }
        Vec3 v = flat.normalize().scale(mount instanceof HorsemanSteed ? 1.0D : 0.7D);
        mount.setDeltaMovement(v.x, mount.getDeltaMovement().y, v.z);
        float yaw = (float) (Mth.atan2(-v.x, v.z) * (180.0D / Math.PI));
        mount.setYRot(yaw);
        if (mount instanceof LivingEntity lm) {
            lm.yBodyRot = yaw;
            lm.yHeadRot = yaw;
        }
        this.chargeLeft--;
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, mount.getBoundingBox().inflate(1.0D, 0.5D, 1.0D), this::isVictim)) {
            e.hurt(this.damageSources().mobAttack(this), 25.0F);
            if (e == target && this.grabbedId < 0) {
                this.grab(e);
            } else {
                e.setDeltaMovement(v.x * 1.2D, 0.5D, v.z * 1.2D);
                e.hurtMarked = true;
            }
            this.chargeLeft = 0;
            break;
        }
    }

    private void breakBlocks(ServerLevel level, LivingEntity target) {
        if (!ForgeEventFactory.getMobGriefingEvent(level, this)) {
            return;
        }
        Entity base = this.getVehicle() != null ? this.getVehicle() : this;
        Vec3 d = target.position().subtract(base.position());
        Direction dir = Math.abs(d.x) > Math.abs(d.z) ? (d.x > 0 ? Direction.EAST : Direction.WEST) : (d.z > 0 ? Direction.SOUTH : Direction.NORTH);
        Direction side = dir.getClockWise();
        for (int k = 1; k <= 2; k++) {
            for (int w = -1; w <= 1; w++) {
                for (int dy = 0; dy <= 3; dy++) {
                    BlockPos p = base.blockPosition().relative(dir, k).relative(side, w).above(dy);
                    BlockState s = level.getBlockState(p);
                    float h = s.getDestroySpeed(level, p);
                    if (!s.isAir() && h >= 0.0F && h <= MAX_BREAK_HARDNESS && s.getFluidState().isEmpty()) {
                        level.destroyBlock(p, true, this);
                    }
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof PumpkinBomb) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (this.getMainHandItem().isEmpty()) {
            this.equip();
        }
        if (this.chargeCooldown > 0) {
            this.chargeCooldown--;
        }
        if (this.bombCooldown > 0) {
            this.bombCooldown--;
        }
        if (this.chainCooldown > 0) {
            this.chainCooldown--;
        }
        if (this.breakCooldown > 0) {
            this.breakCooldown--;
        }
        if (this.mountCooldown > 0) {
            this.mountCooldown--;
        }
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
        this.tickGrab();
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        if (!this.isPassenger() && this.mountCooldown <= 0 && this.tickCount % 5 == 0) {
            this.tryFindMount(level);
        }
        Entity mount = this.getVehicle();
        double d = this.distanceTo(target);
        boolean sees = this.getSensing().hasLineOfSight(target);
        if (mount instanceof Mob m && !(mount instanceof HorsemanSteed)) {
            m.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.4D);
        }
        if (mount != null && this.grabbedId < 0 && this.attackCooldown <= 0 && d <= 3.8D && this.chargeLeft <= 0) {
            this.attackCooldown = 20;
            this.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            this.doHurtTarget(target);
            if (this.random.nextInt(4) == 0 && target.isAlive()) {
                this.grab(target);
            }
        }
        if (mount != null && this.chargeCooldown <= 0 && this.chargeLeft <= 0 && d > 6.0D && d < 30.0D && sees && this.grabbedId < 0) {
            this.chargeLeft = 40;
            this.chargeCooldown = 240;
            this.playSound(SoundEvents.SKELETON_HORSE_AMBIENT, 2.0F, 0.5F);
        }
        if (this.chargeLeft > 0) {
            this.tickCharge(level, target);
        }
        if (this.bombCooldown <= 0 && d >= 7.0D && d <= 30.0D && sees && this.grabbedId < 0 && this.chargeLeft <= 0) {
            this.bombCooldown = 70 + this.random.nextInt(40);
            this.performRangedAttack(target, 1.0F);
        }
        if (this.chainCooldown <= 0 && d > 12.0D && d <= 36.0D && sees && this.grabbedId < 0) {
            this.chainCooldown = 160;
            this.pullWithChain(level, target);
        }
        if (this.breakCooldown <= 0 && (this.horizontalCollision || (mount != null && mount.horizontalCollision) || this.getNavigation().isStuck())) {
            this.breakCooldown = 8;
            this.breakBlocks(level, target);
        }
    }

    // ------------------------------------------------------------------ morte: o filhote
    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.throwVictim(this.grabbed());
        if (this.level() instanceof ServerLevel level) {
            HeadlessPup pup = ModEntities.HEADLESS_PUP.get().create(level);
            if (pup != null) {
                pup.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                LivingEntity credit = this.getKillCredit();
                Player owner = source.getEntity() instanceof Player p ? p : (credit instanceof Player cp ? cp : null);
                if (owner != null) {
                    pup.tame(owner);
                }
                pup.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.JACK_O_LANTERN));
                level.addFreshEntity(pup);
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean shouldDespawnInPeaceful() {
        return false;
    }

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

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SKELETON_STEP, 0.15F, 1.0F);
    }

    /** Raro, so a noite, um por regiao, nunca durante a geracao do mundo. */
    public static boolean checkBossSpawn(EntityType<HeadlessHorseman> type, ServerLevelAccessor level, MobSpawnType reason,
            BlockPos pos, RandomSource random) {
        if (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND || reason == MobSpawnType.SPAWNER) {
            return true;
        }
        if (reason != MobSpawnType.NATURAL || random.nextInt(3) != 0 || level.getLevel().isDay()) {
            return false;
        }
        return level.getLevel().getEntitiesOfClass(HeadlessHorseman.class, new AABB(pos).inflate(128.0D)).isEmpty()
                && Monster.checkAnyLightMonsterSpawnRules(EntityType.ZOMBIE, level, reason, pos, random);
    }

    // ------------------------------------------------------------------ barra de chefe
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }
}
