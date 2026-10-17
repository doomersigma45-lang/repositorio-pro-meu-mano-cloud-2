package com.netheritesupremo.entity;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Robo: estende o golem de ferro (neutro, defende quem o construiu, ataca monstros), mas tem
 * 150 de vida, mais dano que o golem de ferro, e imune a projeteis, voa e atira lasers.
 */
public class Robot extends IronGolem {
    public static final double MAX_HEALTH = 150.0D;
    /** O golem de ferro causa 7 a 21 (15 de atributo); o robo causa 12,5 a 37,5. */
    public static final double ATTACK_DAMAGE = 25.0D;
    public static final float LASER_DAMAGE = 12.0F;
    public static final double LASER_RANGE = 28.0D;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(Robot.class, EntityDataSerializers.INT);

    public static final int VARIANTS = 3;

    public Robot(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoGravity(true);
        if (!level.isClientSide) {
            this.entityData.set(DATA_VARIANT, this.random.nextInt(VARIANTS));
        }
    }

    public static AttributeSupplier.Builder createRobotAttributes() {
        return IronGolem.createAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    // ------------------------------------------------------------- variante de textura
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
    }

    public int getVariant() {
        return Math.floorMod(this.entityData.get(DATA_VARIANT), VARIANTS);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Variant")) {
            this.entityData.set(DATA_VARIANT, tag.getInt("Variant"));
        }
    }

    // ------------------------------------------------------------------ imunidades
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypeTags.IS_PROJECTILE)
                || source.is(DamageTypeTags.IS_FALL)
                || super.isInvulnerableTo(source);
    }

    // ------------------------------------------------------------------------ voo
    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state,
            net.minecraft.core.BlockPos pos) {
    }

    // ------------------------------------------------------------------- objetivos
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LaserGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false,
                e -> e instanceof Enemy && !(e instanceof Creeper) && !(e instanceof NetheriteKnight)));
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof NetheriteKnight) && super.canAttack(target);
    }

    // ------------------------------------------------------------------------ laser
    /** Dispara um laser instantaneo (raio) que atravessa e fere todos os inimigos na linha. */
    static class LaserGoal extends Goal {
        private final Robot robot;
        private int charge;
        private int cooldown = 40;

        LaserGoal(Robot robot) {
            this.robot = robot;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                this.cooldown--;
                return false;
            }
            LivingEntity target = this.robot.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            double dist = this.robot.distanceTo(target);
            return dist > 3.0D && dist < LASER_RANGE && this.robot.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.robot.getTarget();
            return target != null && target.isAlive() && this.charge > 0;
        }

        @Override
        public void start() {
            this.charge = 15;
            this.robot.playSound(SoundEvents.BEACON_POWER_SELECT, 1.5F, 1.6F);
        }

        @Override
        public void tick() {
            LivingEntity target = this.robot.getTarget();
            if (target == null) {
                return;
            }
            this.robot.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (this.robot.level() instanceof ServerLevel level) {
                Vec3 eye = this.robot.getEyePosition();
                level.sendParticles(new DustParticleOptions(new Vector3f(0.2F, 0.9F, 1.0F), 1.0F),
                        eye.x, eye.y - 0.2D, eye.z, 2, 0.15D, 0.15D, 0.15D, 0.0D);
            }
            if (--this.charge <= 0) {
                this.fire(target);
            }
        }

        @Override
        public void stop() {
            this.charge = 0;
            this.cooldown = 40 + this.robot.getRandom().nextInt(30);
        }

        private void fire(LivingEntity target) {
            if (!(this.robot.level() instanceof ServerLevel level)) {
                return;
            }
            Vec3 from = this.robot.getEyePosition().add(0.0D, -0.2D, 0.0D);
            Vec3 dir = target.getBoundingBox().getCenter().subtract(from).normalize();
            Vec3 end = from.add(dir.scale(LASER_RANGE));
            HitResult block = level.clip(new ClipContext(from, end, ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, this.robot));
            if (block.getType() != HitResult.Type.MISS) {
                end = block.getLocation();
            }
            final Vec3 beamEnd = end;
            List<LivingEntity> hit = level.getEntitiesOfClass(LivingEntity.class,
                    new net.minecraft.world.phys.AABB(from, beamEnd).inflate(1.0D),
                    e -> e != this.robot && e.isAlive() && !(e instanceof NetheriteKnight)
                            && (e == target || e instanceof Enemy)
                            && e.getBoundingBox().inflate(0.3D).clip(from, beamEnd).isPresent());
            for (LivingEntity victim : hit) {
                victim.hurt(level.damageSources().indirectMagic(this.robot, this.robot), LASER_DAMAGE);
            }
            double length = from.distanceTo(beamEnd);
            DustParticleOptions beam = new DustParticleOptions(new Vector3f(0.2F, 0.9F, 1.0F), 1.3F);
            for (double d = 0.0D; d < length; d += 0.4D) {
                Vec3 p = from.add(dir.scale(d));
                level.sendParticles(beam, p.x, p.y, p.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            level.playSound(null, this.robot.blockPosition(), SoundEvents.GUARDIAN_ATTACK, SoundSource.HOSTILE,
                    1.5F, 1.8F);
        }
    }
}
