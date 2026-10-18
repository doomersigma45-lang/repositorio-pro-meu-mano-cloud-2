package com.netheritesupremo.entity;

import com.netheritesupremo.SupremoConfig;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.joml.Vector3f;

/**
 * Precursor: boss muito forte. 20.000 de vida, 1.500 de dano, voa e atravessa paredes, nada,
 * quebra qualquer bloco, imune a explosoes e projeteis, solta varios lasers, explosoes e a
 * maldicao dos guardioes (Fraqueza + Fadiga), invoca guardioes e guardioes anciaos e tem cinco
 * totens da imortalidade (cada um recupera toda a vida).
 */
public class Precursor extends FlyingRobot {
    public static final double MAX_HEALTH = 20000.0D;
    public static final double ATTACK_DAMAGE = 1500.0D;
    public static final float LASER_DAMAGE = 500.0F;
    public static final double LASER_RANGE = 64.0D;
    public static final float BARRAGE_EXPLOSION_POWER = 10.0F;
    public static final int TOTEMS = 5;
    private static final Vector3f LASER_COLOR = new Vector3f(0.3F, 1.0F, 0.8F);

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_20);
    private int totems = TOTEMS;

    public Precursor(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new PrecursorMoveControl(this);
        this.xpReward = 20000;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createPrecursorAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.FLYING_SPEED, 1.6D)
                .add(Attributes.ARMOR, 30.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 20.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 96.0D);
    }

    public int getTotems() {
        return this.totems;
    }

    @Override
    protected ParticleOptions backFlame() {
        return ParticleTypes.ELECTRIC_SPARK;
    }

    /** Voce pediu imunidade so a explosoes e projeteis (alem de queda e sufocamento): fogo o fere. */
    @Override
    protected boolean immuneToFire() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.DROWN) || super.isInvulnerableTo(source);
    }

    // ---------------------------------------------------- nadar, voar e atravessar paredes
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void tick() {
        this.noPhysics = true; // atravessa blocos
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
    }

    /** Move-se em linha reta ate o ponto desejado, sem se importar com paredes. */
    static class PrecursorMoveControl extends MoveControl {
        PrecursorMoveControl(Mob mob) {
            super(mob);
        }

        @Override
        public void tick() {
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                return;
            }
            Vec3 d = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(),
                    this.wantedZ - this.mob.getZ());
            double dist = d.length();
            if (dist < 1.0D) {
                this.operation = MoveControl.Operation.WAIT;
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.5D));
                return;
            }
            Vec3 dir = d.scale(1.0D / dist);
            double speed = this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED);
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.8D).add(dir.scale(speed * 0.05D)));
            if (Math.abs(dir.x) > 1.0E-3D || Math.abs(dir.z) > 1.0E-3D) {
                float yaw = (float) (Mth.atan2(dir.z, dir.x) * (180.0D / Math.PI)) - 90.0F;
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), yaw, 20.0F));
                this.mob.yBodyRot = this.mob.getYRot();
            }
        }
    }

    // --------------------------------------------------------------------- totens
    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide && this.totems > 0 && !source.is(DamageTypes.GENERIC_KILL)) {
            this.totems--;
            this.setHealth(this.getMaxHealth());
            this.removeAllEffects();
            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, this.getX(), this.getY(0.5D), this.getZ(),
                        200, 1.5D, 2.5D, 1.5D, 0.6D);
                level.playSound(null, this.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.HOSTILE, 4.0F, 0.6F);
            }
            return;
        }
        super.die(source);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Totems", this.totems);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Totems")) {
            this.totems = tag.getInt("Totems");
        }
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    // ----------------------------------------------------- quebrar qualquer bloco
    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.tickCount % 2 == 0 && this.getTarget() != null) {
            this.breakBlocks();
        }
    }

    private void breakBlocks() {
        if (!(this.level() instanceof ServerLevel level) || !SupremoConfig.PRECURSOR_DESTROYS_BLOCKS.get()
                || !ForgeEventFactory.getMobGriefingEvent(level, this)) {
            return;
        }
        AABB box = this.getBoundingBox().inflate(1.5D);
        for (BlockPos pos : BlockPos.betweenClosed(Mth.floor(box.minX), Mth.floor(box.minY), Mth.floor(box.minZ),
                Mth.floor(box.maxX), Mth.floor(box.maxY), Mth.floor(box.maxZ))) {
            BlockState state = level.getBlockState(pos);
            // Qualquer bloco (ate bedrock e obsidiana); sem drops para nao encher o mundo de itens
            if (!state.isAir() && state.getFluidState().isEmpty() && level.isInWorldBounds(pos)) {
                level.destroyBlock(pos, false, this);
            }
        }
    }

    private Level.ExplosionInteraction explosionMode() {
        return SupremoConfig.PRECURSOR_DESTROYS_BLOCKS.get()
                ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE;
    }

    // ------------------------------------------------------------------ objetivos
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LaserVolleyGoal(this));
        this.goalSelector.addGoal(1, new ExplosionBarrageGoal(this));
        this.goalSelector.addGoal(1, new GuardianCurseGoal(this));
        this.goalSelector.addGoal(1, new SummonGuardiansGoal(this));
        this.goalSelector.addGoal(2, new ChaseGoal(this));
        this.goalSelector.addGoal(5, new WanderGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 24.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /** Persegue o alvo (atravessando paredes) e o golpeia de perto. */
    static class ChaseGoal extends Goal {
        private final Precursor boss;
        private int attackCooldown;

        ChaseGoal(Precursor boss) {
            this.boss = boss;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.boss.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = this.boss.getTarget();
            if (target == null) {
                return;
            }
            double dist = this.boss.distanceTo(target);
            this.boss.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (dist > 3.5D) {
                this.boss.getMoveControl().setWantedPosition(target.getX(), target.getY(0.5D), target.getZ(), 1.0D);
            } else {
                this.boss.setDeltaMovement(this.boss.getDeltaMovement().scale(0.5D));
            }
            if (--this.attackCooldown <= 0 && dist < 4.5D) {
                this.attackCooldown = 15;
                this.boss.swing(InteractionHand.MAIN_HAND);
                this.boss.doHurtTarget(target);
            }
        }
    }

    static class WanderGoal extends Goal {
        private final Precursor boss;

        WanderGoal(Precursor boss) {
            this.boss = boss;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.boss.getTarget() == null && !this.boss.getMoveControl().hasWanted();
        }

        @Override
        public boolean canContinueToUse() {
            return this.boss.getTarget() == null && this.boss.getMoveControl().hasWanted();
        }

        @Override
        public void start() {
            this.boss.getMoveControl().setWantedPosition(
                    this.boss.getX() + (this.boss.getRandom().nextDouble() - 0.5D) * 32.0D,
                    this.boss.getY() + (this.boss.getRandom().nextDouble() - 0.5D) * 10.0D,
                    this.boss.getZ() + (this.boss.getRandom().nextDouble() - 0.5D) * 32.0D, 0.5D);
        }
    }

    /** Salva de lasers: um no alvo, varios espalhados e um em cada outro jogador por perto. */
    static class LaserVolleyGoal extends Goal {
        private final Precursor boss;
        private int cooldown = 60;

        LaserVolleyGoal(Precursor boss) {
            this.boss = boss;
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                this.cooldown--;
                return false;
            }
            LivingEntity target = this.boss.getTarget();
            return target != null && target.isAlive() && this.boss.distanceTo(target) < LASER_RANGE
                    && this.boss.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            this.cooldown = 40;
            LivingEntity target = this.boss.getTarget();
            if (target == null) {
                return;
            }
            Vec3 center = target.getBoundingBox().getCenter();
            RobotLaser.fireTowards(this.boss, target, center, LASER_RANGE, LASER_DAMAGE, LASER_COLOR);
            for (int i = 0; i < 4; i++) {
                Vec3 aim = center.add((this.boss.getRandom().nextDouble() - 0.5D) * 6.0D,
                        (this.boss.getRandom().nextDouble() - 0.5D) * 3.0D,
                        (this.boss.getRandom().nextDouble() - 0.5D) * 6.0D);
                RobotLaser.fireTowards(this.boss, target, aim, LASER_RANGE, LASER_DAMAGE, LASER_COLOR);
            }
            List<Player> others = this.boss.level().getEntitiesOfClass(Player.class,
                    this.boss.getBoundingBox().inflate(LASER_RANGE),
                    p -> p != target && p.isAlive() && !p.isCreative() && !p.isSpectator()
                            && this.boss.hasLineOfSight(p));
            for (Player p : others) {
                RobotLaser.fireTowards(this.boss, p, p.getBoundingBox().getCenter(), LASER_RANGE, LASER_DAMAGE,
                        LASER_COLOR);
            }
        }
    }

    /** Tres explosoes fortes sobre o alvo e ao redor dele. */
    static class ExplosionBarrageGoal extends Goal {
        private final Precursor boss;
        private int cooldown = 100;

        ExplosionBarrageGoal(Precursor boss) {
            this.boss = boss;
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
            return false;
        }

        @Override
        public void start() {
            this.cooldown = 80;
            LivingEntity target = this.boss.getTarget();
            if (target == null) {
                return;
            }
            Level.ExplosionInteraction mode = this.boss.explosionMode();
            this.boss.level().explode(this.boss, target.getX(), target.getY(0.5D), target.getZ(),
                    BARRAGE_EXPLOSION_POWER, mode);
            for (int i = 0; i < 2; i++) {
                this.boss.level().explode(this.boss,
                        target.getX() + (this.boss.getRandom().nextDouble() - 0.5D) * 20.0D,
                        target.getY(0.5D) + (this.boss.getRandom().nextDouble() - 0.5D) * 6.0D,
                        target.getZ() + (this.boss.getRandom().nextDouble() - 0.5D) * 20.0D,
                        BARRAGE_EXPLOSION_POWER, mode);
            }
        }
    }

    /** Maldicao dos guardioes: Fraqueza e Fadiga de Mineracao nos inimigos (e o efeito do guardiao ancião). */
    static class GuardianCurseGoal extends Goal {
        private final Precursor boss;
        private int cooldown = 200;

        GuardianCurseGoal(Precursor boss) {
            this.boss = boss;
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                this.cooldown--;
                return false;
            }
            LivingEntity target = this.boss.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            this.cooldown = 300;
            List<LivingEntity> victims = this.boss.level().getEntitiesOfClass(LivingEntity.class,
                    this.boss.getBoundingBox().inflate(64.0D),
                    e -> e.isAlive() && (e == this.boss.getTarget()
                            || (e instanceof Player p && !p.isCreative() && !p.isSpectator())));
            for (LivingEntity victim : victims) {
                victim.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1200, 2), this.boss);
                victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1200, 1), this.boss);
                if (victim instanceof ServerPlayer player) {
                    player.connection.send(new ClientboundGameEventPacket(
                            ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT, 1.0F));
                }
            }
        }
    }

    /** Invoca guardioes e, de vez em quando, guardioes anciaos. */
    static class SummonGuardiansGoal extends Goal {
        private final Precursor boss;
        private int cooldown = 120;

        SummonGuardiansGoal(Precursor boss) {
            this.boss = boss;
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                this.cooldown--;
                return false;
            }
            LivingEntity target = this.boss.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            this.cooldown = 200;
            if (!(this.boss.level() instanceof ServerLevel level)) {
                return;
            }
            AABB area = this.boss.getBoundingBox().inflate(64.0D);
            int guardians = level.getEntitiesOfClass(Guardian.class, area).size();
            int elders = level.getEntitiesOfClass(ElderGuardian.class, area).size();
            if (guardians < 8) {
                for (int i = 0; i < 3; i++) {
                    this.place(level, EntityType.GUARDIAN.create(level));
                }
            }
            if (elders < 2 && this.boss.getRandom().nextInt(3) == 0) {
                this.place(level, EntityType.ELDER_GUARDIAN.create(level));
            }
            level.playSound(null, this.boss.blockPosition(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE,
                    3.0F, 0.8F);
        }

        private void place(ServerLevel level, @Nullable Guardian guardian) {
            if (guardian == null) {
                return;
            }
            guardian.moveTo(this.boss.getX() + (this.boss.getRandom().nextDouble() - 0.5D) * 12.0D,
                    this.boss.getY() + this.boss.getRandom().nextDouble() * 3.0D,
                    this.boss.getZ() + (this.boss.getRandom().nextDouble() - 0.5D) * 12.0D, this.boss.getYRot(), 0.0F);
            guardian.setTarget(this.boss.getTarget());
            guardian.setPersistenceRequired();
            level.addFreshEntity(guardian);
        }
    }

    // --------------------------------------------------------------- barra de boss
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
