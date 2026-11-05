package com.netheritesupremo.entity;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Nave espacial da invasao: paira sobre o alvo e dispara lasers para baixo. Cada laser explode onde
 * atinge (respeita mobGriefing), destruindo casas aos poucos.
 */
public class AlienShip extends Mob implements Enemy {
    public static final int FIRE_INTERVAL = 50;
    private int fireCooldown = 60;

    public AlienShip(EntityType<? extends AlienShip> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.xpReward = 60;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 160.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 80.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            return;
        }
        // orbita sobre o alvo, a ~16 blocos acima do chao
        double t = this.tickCount / 40.0D;
        double hx = target.getX() + Math.cos(t) * 14.0D;
        double hz = target.getZ() + Math.sin(t) * 14.0D;
        int ground = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) hx, (int) hz);
        double hy = Math.max(target.getY() + 14.0D, ground + 12.0D);
        Vec3 want = new Vec3(hx, hy, hz).subtract(this.position());
        double len = want.length();
        Vec3 vel = len > 0.5D ? want.scale(Math.min(0.45D, len * 0.08D) / len) : Vec3.ZERO;
        this.setDeltaMovement(this.getDeltaMovement().scale(0.8D).add(vel.scale(0.6D)));
        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.setYRot(this.getYRot() + 6.0F);
        if (--this.fireCooldown <= 0 && this.distanceTo(target) < 48.0D) {
            this.fireCooldown = FIRE_INTERVAL + this.random.nextInt(20);
            this.fire(level, target);
        }
    }

    private void fire(ServerLevel level, LivingEntity target) {
        Vec3 from = this.position().add(0.0D, 0.2D, 0.0D);
        Vec3 aim = target.position().add((this.random.nextDouble() - 0.5D) * 10.0D, 0.5D,
                (this.random.nextDouble() - 0.5D) * 10.0D);
        Vec3 dir = aim.subtract(from).normalize();
        Vec3 end = from.add(dir.scale(70.0D));
        HitResult hit = level.clip(new ClipContext(from, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        Vec3 impact = hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();
        DustParticleOptions beam = new DustParticleOptions(new Vector3f(1.0F, 0.15F, 0.2F), 1.8F);
        double length = from.distanceTo(impact);
        for (double d = 0.0D; d < length; d += 0.5D) {
            Vec3 p = from.add(dir.scale(d));
            level.sendParticles(beam, p.x, p.y, p.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        level.sendParticles(ParticleTypes.FLASH, impact.x, impact.y, impact.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.playSound(null, this.blockPosition(), SoundEvents.GUARDIAN_ATTACK, SoundSource.HOSTILE, 3.0F, 0.6F);
        level.explode(this, impact.x, impact.y, impact.z, 3.5F, Level.ExplosionInteraction.MOB);
    }
}
