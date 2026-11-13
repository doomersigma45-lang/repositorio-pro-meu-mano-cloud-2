package com.netheritesupremo.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * Escavador: vive em cavernas, 180 HP, 15 de dano. Fica parado enquanto nao percebe ninguem; detecta
 * jogadores a 5 blocos e escuta quem se move (andando/correndo, nao agachado) ate 24 blocos, e persegue.
 * Escala paredes.
 */
public class Digger extends Monster {
    public static final double MAX_HEALTH = 180.0D;
    public static final double DAMAGE = 15.0D;
    public static final double HEARING_RANGE = 24.0D;
    public static final double SENSE_RANGE = 5.0D;
    public static final int FORGET_TICKS = 100;

    private static final EntityDataAccessor<Byte> DATA_CLIMBING =
            SynchedEntityData.defineId(Digger.class, EntityDataSerializers.BYTE);

    private int lastHeard = -1000;

    public Digger(EntityType<? extends Digger> type, Level level) {
        super(type, level);
        this.xpReward = 40;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WallClimberNavigation(this, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CLIMBING, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_CLIMBING) & 1) != 0;
    }

    private void setClimbing(boolean climbing) {
        byte b = this.entityData.get(DATA_CLIMBING);
        this.entityData.set(DATA_CLIMBING, climbing ? (byte) (b | 1) : (byte) (b & -2));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.setClimbing(this.horizontalCollision);
            if (this.tickCount % 5 == 0) {
                this.listen();
            }
        }
    }

    private static boolean makesNoise(ServerPlayer p) {
        double dx = p.getX() - p.xo;
        double dz = p.getZ() - p.zo;
        return !p.isCrouching() && dx * dx + dz * dz > 0.01D;
    }

    private void listen() {
        ServerPlayer best = null;
        double bestDist = Double.MAX_VALUE;
        for (ServerPlayer p : ((net.minecraft.server.level.ServerLevel) this.level()).players()) {
            if (p.isCreative() || p.isSpectator() || !p.isAlive()) {
                continue;
            }
            double d = this.distanceTo(p);
            boolean heard = d <= SENSE_RANGE || (d <= HEARING_RANGE && makesNoise(p));
            if (heard && d < bestDist) {
                best = p;
                bestDist = d;
            }
        }
        if (best != null) {
            this.lastHeard = this.tickCount;
            if (this.getTarget() == null || this.getTarget() instanceof ServerPlayer) {
                this.setTarget(best);
            }
        } else if (this.getTarget() instanceof ServerPlayer && this.tickCount - this.lastHeard > FORGET_TICKS) {
            this.setTarget(null);
            this.getNavigation().stop();
        }
    }

    /** Cavernas: abaixo de y=50, sem ceu. */
    public static boolean checkSpawnRules(EntityType<Digger> type, ServerLevelAccessor level, MobSpawnType reason,
            BlockPos pos, RandomSource random) {
        if (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND || reason == MobSpawnType.SPAWNER) {
            return true;
        }
        return pos.getY() < 50 && !level.canSeeSky(pos) && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }
}
