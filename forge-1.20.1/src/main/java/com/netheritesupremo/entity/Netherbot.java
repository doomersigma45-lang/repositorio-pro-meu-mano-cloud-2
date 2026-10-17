package com.netheritesupremo.entity;

import com.netheritesupremo.registry.ModEntities;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

/**
 * Netherbot: mini boss voador, imune a fogo, explosoes e projeteis. 666 de vida, 66 de dano,
 * lasers de 45, invoca bomb-robots e tem um totem da imortalidade: na primeira "morte"
 * recupera toda a vida e fica mais forte e resistente.
 */
public class Netherbot extends FlyingRobot {
    public static final double MAX_HEALTH = 666.0D;
    public static final double ATTACK_DAMAGE = 66.0D;
    public static final float LASER_DAMAGE = 45.0F;
    public static final double LASER_RANGE = 40.0D;
    private static final Vector3f LASER_COLOR = new Vector3f(1.0F, 0.35F, 0.1F);

    private static final UUID PHASE2_DAMAGE = UUID.fromString("7b0c1c2e-5a55-4f0e-9d0b-6a1f3b0a6601");
    private static final UUID PHASE2_ARMOR = UUID.fromString("7b0c1c2e-5a55-4f0e-9d0b-6a1f3b0a6602");
    private static final UUID PHASE2_TOUGHNESS = UUID.fromString("7b0c1c2e-5a55-4f0e-9d0b-6a1f3b0a6603");
    private static final UUID PHASE2_SPEED = UUID.fromString("7b0c1c2e-5a55-4f0e-9d0b-6a1f3b0a6604");

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS);
    private boolean revived;

    public Netherbot(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 500;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createNetherbotAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FLYING_SPEED, 0.8D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    public boolean isRevived() {
        return this.revived;
    }

    @Override
    protected ParticleOptions backFlame() {
        return ParticleTypes.FLAME; // aura de fogo normal
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

    private void useTotem() {
        this.revived = true;
        this.setHealth(this.getMaxHealth());
        this.removeAllEffects();
        this.applyPhaseTwo();
        if (this.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, this.getX(), this.getY(0.5D), this.getZ(),
                    80, 0.6D, 1.2D, 0.6D, 0.5D);
            level.playSound(null, this.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.HOSTILE, 2.0F, 1.0F);
        }
    }

    /** Mais forte (dano +50%, velocidade +20%) e mais resistente (armadura, Resistencia II). */
    private void applyPhaseTwo() {
        this.addPhaseModifier(Attributes.ATTACK_DAMAGE, PHASE2_DAMAGE, 0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addPhaseModifier(Attributes.ARMOR, PHASE2_ARMOR, 15.0D, AttributeModifier.Operation.ADDITION);
        this.addPhaseModifier(Attributes.ARMOR_TOUGHNESS, PHASE2_TOUGHNESS, 10.0D, AttributeModifier.Operation.ADDITION);
        this.addPhaseModifier(Attributes.MOVEMENT_SPEED, PHASE2_SPEED, 0.2D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, MobEffectInstance.INFINITE_DURATION, 1, false, true));
    }

    private void addPhaseModifier(Attribute attribute, UUID id, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = this.getAttribute(attribute);
        if (instance != null && instance.getModifier(id) == null) {
            instance.addPermanentModifier(new AttributeModifier(id, "Netherbot totem", amount, operation));
        }
    }

    public float getLaserDamage() {
        return this.revived ? LASER_DAMAGE * 1.5F : LASER_DAMAGE;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Revived", this.revived);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.revived = tag.getBoolean("Revived");
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    // ------------------------------------------------------------------ objetivos
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SummonBombRobotsGoal(this));
        this.goalSelector.addGoal(2, new LaserGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 0.9D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    static class LaserGoal extends Goal {
        private final Netherbot boss;
        private int charge;
        private int cooldown = 60;

        LaserGoal(Netherbot boss) {
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
            return dist > 4.0D && dist < LASER_RANGE && this.boss.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.boss.getTarget();
            return target != null && target.isAlive() && this.charge > 0;
        }

        @Override
        public void start() {
            this.charge = 20;
            this.boss.playSound(SoundEvents.BEACON_POWER_SELECT, 2.0F, 0.8F);
        }

        @Override
        public void tick() {
            LivingEntity target = this.boss.getTarget();
            if (target == null) {
                return;
            }
            this.boss.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (this.boss.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.FLAME, this.boss.getX(),
                        this.boss.getEyeY() - 0.2D, this.boss.getZ(), 3, 0.3D, 0.3D, 0.3D, 0.02D);
            }
            if (--this.charge <= 0) {
                RobotLaser.fire(this.boss, target, LASER_RANGE, this.boss.getLaserDamage(), LASER_COLOR);
            }
        }

        @Override
        public void stop() {
            this.charge = 0;
            this.cooldown = (this.boss.revived ? 40 : 60) + this.boss.getRandom().nextInt(40);
        }
    }

    /** Durante a batalha invoca bomb-robots (ate 3, ou 5 depois do totem). */
    static class SummonBombRobotsGoal extends Goal {
        private final Netherbot boss;
        private int cooldown = 100;

        SummonBombRobotsGoal(Netherbot boss) {
            this.boss = boss;
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                this.cooldown--;
                return false;
            }
            LivingEntity target = this.boss.getTarget();
            return target != null && target.isAlive() && this.bots().size() < (this.boss.revived ? 5 : 3);
        }

        private List<NetheriteBombRobot> bots() {
            return this.boss.level().getEntitiesOfClass(NetheriteBombRobot.class,
                    this.boss.getBoundingBox().inflate(48.0D));
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            this.cooldown = this.boss.revived ? 200 : 300;
            if (!(this.boss.level() instanceof ServerLevel level)) {
                return;
            }
            int count = this.boss.revived ? 3 : 2;
            for (int i = 0; i < count; i++) {
                NetheriteBombRobot bot = ModEntities.BOMB_ROBOT.get().create(level);
                if (bot == null) {
                    continue;
                }
                bot.moveTo(this.boss.getX() + (this.boss.getRandom().nextDouble() - 0.5D) * 6.0D,
                        this.boss.getY() + 1.0D + this.boss.getRandom().nextDouble() * 2.0D,
                        this.boss.getZ() + (this.boss.getRandom().nextDouble() - 0.5D) * 6.0D,
                        this.boss.getYRot(), 0.0F);
                bot.setTarget(this.boss.getTarget());
                level.addFreshEntity(bot);
            }
            level.playSound(null, this.boss.blockPosition(), SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE,
                    2.0F, 0.8F);
        }
    }

    // --------------------------------------------------------------- barra de boss
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        this.bossEvent.setColor(this.revived ? BossEvent.BossBarColor.RED : BossEvent.BossBarColor.YELLOW);
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
