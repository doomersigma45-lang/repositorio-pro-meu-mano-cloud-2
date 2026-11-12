package com.netheritesupremo.mob;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

/**
 * Besouro colossal: vive em cavernas profundas, 250 HP, 18 de dano, carapaca que reduz 50% do dano, ataca
 * qualquer criatura que entre no territorio dele e empurra com investidas do chifre.
 */
public class ColossalBeetle extends Monster {
    public static final double MAX_HEALTH = 250.0D;
    public static final double DAMAGE = 18.0D;
    public static final double TERRITORY = 18.0D;

    private BlockPos home;
    private int chargeCooldown = 80;
    private int chargeState; // 0 nada, 1 preparando, 2 investindo
    private int chargeTicks;
    private Vec3 chargeDir = Vec3.ZERO;
    private final Set<UUID> chargeHit = new HashSet<>();

    public ColossalBeetle(EntityType<? extends ColossalBeetle> type, Level level) {
        super(type, level);
        this.xpReward = 60;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                this::isIntruder));
    }

    private boolean isValidVictim(LivingEntity e) {
        if (e == this || !e.isAlive() || e instanceof ColossalBeetle || e instanceof ArmorStand) {
            return false;
        }
        return !(e instanceof Player p) || (!p.isCreative() && !p.isSpectator());
    }

    private boolean isIntruder(LivingEntity e) {
        return this.isValidVictim(e) && this.home != null && e.blockPosition().distSqr(this.home) < TERRITORY * TERRITORY;
    }

    /** Carapaca: metade do dano (menos dano que ignora invulnerabilidade, como o void). */
    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        super.actuallyHurt(source, source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) ? amount : amount * 0.5F);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.home == null) {
            this.home = this.blockPosition();
        }
        LivingEntity target = this.getTarget();
        if (this.chargeState == 0) {
            if (this.chargeCooldown > 0) {
                this.chargeCooldown--;
            } else if (target != null && this.onGround() && this.hasLineOfSight(target)
                    && this.distanceTo(target) > 4.0F && this.distanceTo(target) < 14.0F) {
                this.chargeState = 1;
                this.chargeTicks = 0;
                this.getNavigation().stop();
                this.playSound(SoundEvents.RAVAGER_ROAR, 1.0F, 0.6F);
            }
        } else if (this.chargeState == 1) {
            if (target == null) {
                this.chargeState = 0;
                return;
            }
            this.getNavigation().stop();
            this.getLookControl().setLookAt(target, 60.0F, 60.0F);
            if (++this.chargeTicks >= 15) {
                Vec3 d = target.position().subtract(this.position()).multiply(1.0D, 0.0D, 1.0D).normalize();
                this.chargeDir = d;
                this.chargeState = 2;
                this.chargeTicks = 0;
                this.chargeHit.clear();
            }
        } else {
            this.chargeTicks++;
            this.setDeltaMovement(this.chargeDir.x * 0.9D, this.getDeltaMovement().y, this.chargeDir.z * 0.9D);
            this.hasImpulse = true;
            for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.4D),
                    this::isValidVictim)) {
                if (this.chargeHit.add(e.getUUID())) {
                    e.hurt(this.damageSources().mobAttack(this), (float) DAMAGE);
                    e.setDeltaMovement(this.chargeDir.x * 2.0D, 0.6D, this.chargeDir.z * 2.0D);
                    e.hurtMarked = true;
                    this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 0.6F);
                }
            }
            if (this.chargeTicks >= 14 || this.horizontalCollision) {
                this.chargeState = 0;
                this.chargeCooldown = 100 + this.random.nextInt(60);
            }
        }
    }

    /** So em cavernas profundas (abaixo de y=0) e no escuro. */
    public static boolean checkSpawnRules(EntityType<ColossalBeetle> type, ServerLevelAccessor level, MobSpawnType reason,
            BlockPos pos, RandomSource random) {
        if (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND || reason == MobSpawnType.SPAWNER) {
            return true;
        }
        return pos.getY() < 0 && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.home != null) {
            tag.putLong("Home", this.home.asLong());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Home")) {
            this.home = BlockPos.of(tag.getLong("Home"));
        }
    }
}
