package com.netheritesupremo.mob;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

/**
 * Zumbi mais forte: grande, 150 HP, 10 de dano, corre e nada rapido, da grandes saltos, arremessa quem
 * acerta e invoca zumbis para defende-lo. Dropa a propria cabeca (usavel como capacete).
 */
public class StrongestZombie extends Zombie {
    public static final double MAX_HEALTH = 150.0D;
    public static final double DAMAGE = 10.0D;
    public static final String MINION_TAG = "netheritesupremo_sz_minion";

    private int leapCooldown = 60;
    private int summonCooldown = 100;
    private int throwCooldown = 0;

    public StrongestZombie(EntityType<? extends StrongestZombie> type, Level level) {
        super(type, level);
        this.xpReward = 80;
        AttributeInstance swim = this.getAttribute(ForgeMod.SWIM_SPEED.get());
        if (swim != null) {
            swim.setBaseValue(3.0D);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.36D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity le && this.throwCooldown <= 0) {
            this.throwCooldown = 60;
            Vec3 dir = le.position().subtract(this.position()).multiply(1.0D, 0.0D, 1.0D).normalize();
            le.setDeltaMovement(dir.x * 1.6D, 0.9D, dir.z * 1.6D);
            le.hurtMarked = true;
        }
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.leapCooldown > 0) {
            this.leapCooldown--;
        }
        if (this.summonCooldown > 0) {
            this.summonCooldown--;
        }
        if (this.throwCooldown > 0) {
            this.throwCooldown--;
        }
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        if (this.isInWater()) { // nada muito rapido
            Vec3 m = this.getDeltaMovement();
            this.setDeltaMovement(m.x * 1.12D, m.y, m.z * 1.12D);
            if (target.getY() > this.getY() + 0.5D) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.04D, 0.0D));
            }
        }
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (this.leapCooldown <= 0 && this.onGround() && dist > 5.0D && dist < 18.0D && this.hasLineOfSight(target)) {
            this.leapCooldown = 80 + this.random.nextInt(40);
            this.setDeltaMovement(dx / dist * 1.1D, 0.9D, dz / dist * 1.1D);
            this.hasImpulse = true;
        }
        if (this.summonCooldown <= 0 && dist < 20.0D) {
            this.summonCooldown = 300;
            this.summonMinions(target);
        }
    }

    private void summonMinions(LivingEntity target) {
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        int alive = level.getEntitiesOfClass(Zombie.class, this.getBoundingBox().inflate(28.0D),
                z -> z.getTags().contains(MINION_TAG)).size();
        for (int i = 0; i < 4 && alive < 8; i++, alive++) {
            Zombie z = EntityType.ZOMBIE.create(level);
            if (z == null) {
                return;
            }
            z.moveTo(this.getX() + this.random.nextInt(5) - 2, this.getY(), this.getZ() + this.random.nextInt(5) - 2,
                    this.random.nextFloat() * 360.0F, 0.0F);
            z.addTag(MINION_TAG);
            z.finalizeSpawn(level, level.getCurrentDifficultyAt(z.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            z.setTarget(target);
            level.addFreshEntity(z);
            level.sendParticles(ParticleTypes.SOUL, z.getX(), z.getY() + 1.0D, z.getZ(), 6, 0.3D, 0.5D, 0.3D, 0.02D);
        }
    }
}
