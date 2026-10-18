package com.netheritesupremo.entity;

import com.netheritesupremo.SupremoConfig;
import com.netheritesupremo.registry.ModEntities;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.joml.Vector3f;

/**
 * Netherite Robot Rainbow Boss: 4500 de vida, 150 de dano, voa (aura de fogo), imune a projeteis,
 * fogo e explosoes. Lasers de 150, bolas explosivas, explosao gigante (75x creeper), quebra blocos,
 * agarra e golpeia quem ataca, invoca 2 netherbots + 10 bomb-robots e tem um totem da imortalidade.
 */
public class NetheriteRobotRainbowBoss extends FlyingRobot {
    public static final double MAX_HEALTH = 4500.0D;
    public static final double ATTACK_DAMAGE = 150.0D;
    public static final float LASER_DAMAGE = 150.0F;
    public static final double LASER_RANGE = 48.0D;
    public static final int PROJECTILE_EXPLOSION_POWER = 6;
    public static final int MINION_NETHERBOTS = 2;
    public static final int MINION_BOMB_ROBOTS = 10;

    private static final BossEvent.BossBarColor[] RAINBOW = {
            BossEvent.BossBarColor.RED, BossEvent.BossBarColor.YELLOW, BossEvent.BossBarColor.GREEN,
            BossEvent.BossBarColor.BLUE, BossEvent.BossBarColor.PURPLE, BossEvent.BossBarColor.PINK};

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS);
    private boolean revived;
    private int waves;

    public NetheriteRobotRainbowBoss(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 5000;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createBossAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FLYING_SPEED, 0.7D)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected ParticleOptions backFlame() {
        return ParticleTypes.FLAME; // aura de fogo
    }

    /** Alem de fogo, explosoes, projeteis e queda (base), nao sufoca dentro de blocos. */
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
    }

    // ---------------------------------------------------------------- totem
    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide && !this.revived && !source.is(DamageTypes.GENERIC_KILL)) {
            this.useTotem();
            return;
        }
        super.die(source);
    }

    /** Regenera toda a vida e ganha Forca III e Resistencia II. */
    private void useTotem() {
        this.revived = true;
        this.setHealth(this.getMaxHealth());
        this.removeAllEffects();
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, MobEffectInstance.INFINITE_DURATION, 2, false, true));
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, MobEffectInstance.INFINITE_DURATION, 1, false, true));
        if (this.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, this.getX(), this.getY(0.5D), this.getZ(),
                    150, 1.0D, 2.0D, 1.0D, 0.6D);
            level.playSound(null, this.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.HOSTILE, 3.0F, 1.0F);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Revived", this.revived);
        tag.putInt("Waves", this.waves);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.revived = tag.getBoolean("Revived");
        this.waves = tag.getInt("Waves");
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    // ------------------------------------------------------- quebra blocos + barra
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        this.bossEvent.setColor(RAINBOW[(this.tickCount / 10) % RAINBOW.length]);
        if (this.tickCount % 4 == 0 && this.getTarget() != null) {
            this.breakBlocks();
        }
    }

    private void breakBlocks() {
        if (!(this.level() instanceof ServerLevel level) || !ForgeEventFactory.getMobGriefingEvent(level, this)) {
            return;
        }
        AABB box = this.getBoundingBox().inflate(1.0D, 0.0D, 1.0D);
        for (BlockPos pos : BlockPos.betweenClosed(Mth.floor(box.minX), Mth.floor(box.minY + 0.1D),
                Mth.floor(box.minZ), Mth.floor(box.maxX), Mth.floor(box.maxY), Mth.floor(box.maxZ))) {
            BlockState state = level.getBlockState(pos);
            if (!state.isAir() && !state.is(BlockTags.WITHER_IMMUNE) && state.getFluidState().isEmpty()
                    && state.getDestroySpeed(level, pos) >= 0.0F) {
                level.destroyBlock(pos, true, this);
            }
        }
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

    // ----------------------------------------------- quem esta agarrado fica na frente
    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction move) {
        float yaw = this.getYRot() * ((float) Math.PI / 180.0F);
        move.accept(passenger, this.getX() - Mth.sin(yaw) * 1.8D, this.getY() + this.getBbHeight() * 0.45D,
                this.getZ() + Mth.cos(yaw) * 1.8D);
    }

    // ------------------------------------------------------------------ objetivos
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RainbowBlastGoal(this));
        this.goalSelector.addGoal(1, new SummonMinionsGoal(this));
        this.goalSelector.addGoal(2, new GrabGoal(this));
        this.goalSelector.addGoal(2, new LaserGoal(this));
        this.goalSelector.addGoal(2, new BombShootGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 0.9D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    private Vector3f rainbowColor() {
        int rgb = Mth.hsvToRgb((this.tickCount % 60) / 60.0F, 1.0F, 1.0F);
        return new Vector3f((rgb >> 16 & 255) / 255.0F, (rgb >> 8 & 255) / 255.0F, (rgb & 255) / 255.0F);
    }

    /** Laser de 150 de dano (arco-iris). */
    static class LaserGoal extends Goal {
        private final NetheriteRobotRainbowBoss boss;
        private int charge;
        private int cooldown = 60;

        LaserGoal(NetheriteRobotRainbowBoss boss) {
            this.boss = boss;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                this.cooldown--;
                return false;
            }
            LivingEntity target = this.boss.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            double dist = this.boss.distanceTo(target);
            return dist > 5.0D && dist < LASER_RANGE && this.boss.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.boss.getTarget();
            return target != null && target.isAlive() && this.charge > 0;
        }

        @Override
        public void start() {
            this.charge = 20;
            this.boss.playSound(SoundEvents.BEACON_POWER_SELECT, 3.0F, 0.7F);
        }

        @Override
        public void tick() {
            LivingEntity target = this.boss.getTarget();
            if (target == null) {
                return;
            }
            this.boss.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (--this.charge <= 0) {
                RobotLaser.fire(this.boss, target, LASER_RANGE, LASER_DAMAGE, this.boss.rainbowColor());
            }
        }

        @Override
        public void stop() {
            this.charge = 0;
            this.cooldown = (this.boss.revived ? 25 : 40) + this.boss.getRandom().nextInt(30);
        }
    }

    /** Bolas de fogo explosivas. */
    static class BombShootGoal extends Goal {
        private final NetheriteRobotRainbowBoss boss;
        private int cooldown = 40;

        BombShootGoal(NetheriteRobotRainbowBoss boss) {
            this.boss = boss;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.boss.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            double dist = this.boss.distanceTo(target);
            return dist > 6.0D && dist < 40.0D && this.boss.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void tick() {
            LivingEntity target = this.boss.getTarget();
            if (target == null) {
                return;
            }
            this.boss.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (--this.cooldown > 0) {
                return;
            }
            this.cooldown = 30 + this.boss.getRandom().nextInt(20);
            double dx = target.getX() - this.boss.getX();
            double dy = target.getY(0.5D) - this.boss.getY(0.6D);
            double dz = target.getZ() - this.boss.getZ();
            LargeFireball bomb = new LargeFireball(this.boss.level(), this.boss, dx, dy, dz, PROJECTILE_EXPLOSION_POWER);
            bomb.setPos(this.boss.getX(), this.boss.getY(0.6D), this.boss.getZ());
            this.boss.level().addFreshEntity(bomb);
            this.boss.playSound(SoundEvents.BLAZE_SHOOT, 2.0F, 0.6F);
        }
    }

    /** Explosao gigante (75x a de um creeper), com carga de 3 segundos. */
    static class RainbowBlastGoal extends Goal {
        private final NetheriteRobotRainbowBoss boss;
        private int charge;
        private int cooldown = 300;

        RainbowBlastGoal(NetheriteRobotRainbowBoss boss) {
            this.boss = boss;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                this.cooldown--;
                return false;
            }
            LivingEntity target = this.boss.getTarget();
            return target != null && target.isAlive() && this.boss.distanceTo(target) < 64.0D;
        }

        @Override
        public boolean canContinueToUse() {
            return this.boss.getTarget() != null && this.charge > 0;
        }

        @Override
        public void start() {
            this.charge = 60;
            this.boss.getNavigation().stop();
            this.boss.playSound(SoundEvents.WITHER_SPAWN, 4.0F, 1.5F);
        }

        @Override
        public void tick() {
            LivingEntity target = this.boss.getTarget();
            if (target != null) {
                this.boss.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }
            if (this.boss.level() instanceof ServerLevel level && this.charge % 3 == 0) {
                DustParticleOptions dust = new DustParticleOptions(this.boss.rainbowColor(), 2.0F);
                level.sendParticles(dust, this.boss.getX(), this.boss.getY(0.5D), this.boss.getZ(), 25,
                        2.5D, 2.5D, 2.5D, 0.0D);
            }
            if (--this.charge <= 0) {
                Level.ExplosionInteraction mode = SupremoConfig.RAINBOW_DESTROYS_BLOCKS.get()
                        ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE;
                this.boss.level().explode(this.boss, this.boss.getX(), this.boss.getY(0.5D), this.boss.getZ(),
                        SupremoConfig.RAINBOW_EXPLOSION_POWER.get().floatValue(), false, mode);
            }
        }

        @Override
        public void stop() {
            this.charge = 0;
            this.cooldown = this.boss.revived ? 300 : 450;
        }
    }

    /** Invoca 2 netherbots e 10 bomb-robots: uma onda no inicio da luta e outra depois do totem. */
    static class SummonMinionsGoal extends Goal {
        private final NetheriteRobotRainbowBoss boss;
        private int cooldown = 40;

        SummonMinionsGoal(NetheriteRobotRainbowBoss boss) {
            this.boss = boss;
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                this.cooldown--;
                return false;
            }
            LivingEntity target = this.boss.getTarget();
            return target != null && target.isAlive() && this.boss.waves < (this.boss.revived ? 2 : 1);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            this.boss.waves++;
            if (!(this.boss.level() instanceof ServerLevel level)) {
                return;
            }
            LivingEntity target = this.boss.getTarget();
            for (int i = 0; i < MINION_NETHERBOTS; i++) {
                Netherbot bot = ModEntities.NETHERBOT.get().create(level);
                this.place(level, bot, target);
            }
            for (int i = 0; i < MINION_BOMB_ROBOTS; i++) {
                NetheriteBombRobot bot = ModEntities.BOMB_ROBOT.get().create(level);
                this.place(level, bot, target);
            }
            level.playSound(null, this.boss.blockPosition(), SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE,
                    4.0F, 0.6F);
        }

        private void place(ServerLevel level, net.minecraft.world.entity.Mob minion, LivingEntity target) {
            if (minion == null) {
                return;
            }
            minion.moveTo(this.boss.getX() + (this.boss.getRandom().nextDouble() - 0.5D) * 12.0D,
                    this.boss.getY() + this.boss.getRandom().nextDouble() * 4.0D,
                    this.boss.getZ() + (this.boss.getRandom().nextDouble() - 0.5D) * 12.0D,
                    this.boss.getYRot(), 0.0F);
            minion.setTarget(target);
            minion.setPersistenceRequired();
            level.addFreshEntity(minion);
        }

        @Override
        public void stop() {
            this.cooldown = 40;
        }
    }

    /** Agarra quem esta atacando e desfere varios golpes, depois o arremessa. */
    static class GrabGoal extends Goal {
        private static final int GRAB_TICKS = 45;
        private final NetheriteRobotRainbowBoss boss;
        private LivingEntity victim;
        private int ticks;
        private int cooldown = 60;

        GrabGoal(NetheriteRobotRainbowBoss boss) {
            this.boss = boss;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                this.cooldown--;
                return false;
            }
            LivingEntity target = this.boss.getTarget();
            return target != null && target.isAlive() && !(target instanceof FlyingRobot)
                    && this.boss.distanceToSqr(target) < 25.0D && this.boss.getPassengers().isEmpty();
        }

        @Override
        public boolean canContinueToUse() {
            return this.victim != null && this.victim.isAlive() && this.ticks < GRAB_TICKS;
        }

        @Override
        public void start() {
            LivingEntity target = this.boss.getTarget();
            this.ticks = 0;
            this.victim = null;
            if (target != null && target.startRiding(this.boss, true)) {
                this.victim = target;
                this.boss.playSound(SoundEvents.IRON_GOLEM_ATTACK, 3.0F, 0.6F);
            }
        }

        @Override
        public void tick() {
            if (this.victim == null) {
                return;
            }
            this.ticks++;
            if (this.victim.getVehicle() != this.boss) {
                this.victim.startRiding(this.boss, true);
            }
            if (this.ticks % 5 == 0) {
                float damage = (float) (this.boss.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5D);
                this.victim.hurt(this.boss.damageSources().mobAttack(this.boss), damage);
                if (this.boss.level() instanceof ServerLevel level) {
                    level.sendParticles(ParticleTypes.CRIT, this.victim.getX(), this.victim.getY(0.5D),
                            this.victim.getZ(), 12, 0.4D, 0.4D, 0.4D, 0.3D);
                }
                this.boss.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 2.0F, 0.7F);
            }
        }

        @Override
        public void stop() {
            if (this.victim != null) {
                this.victim.stopRiding();
                Vec3 look = this.boss.getLookAngle();
                this.victim.setDeltaMovement(look.x * 1.8D, 0.7D, look.z * 1.8D);
                this.victim.hurtMarked = true;
                this.victim = null;
            }
            this.cooldown = 120;
        }
    }
}
