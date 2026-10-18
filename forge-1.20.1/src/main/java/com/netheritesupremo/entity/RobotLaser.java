package com.netheritesupremo.entity;

import java.util.List;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/** Laser instantaneo: raio que para no primeiro bloco e fere quem estiver na linha. */
public final class RobotLaser {
    private RobotLaser() {
    }

    public static void fire(LivingEntity shooter, LivingEntity target, double range, float damage, Vector3f color) {
        fireTowards(shooter, target, target.getBoundingBox().getCenter(), range, damage, color);
    }

    /** Dispara na direcao de um ponto qualquer; so fere o alvo e inimigos que estiverem na linha. */
    public static void fireTowards(LivingEntity shooter, LivingEntity target, Vec3 aim, double range, float damage,
            Vector3f color) {
        if (!(shooter.level() instanceof ServerLevel level)) {
            return;
        }
        Vec3 from = shooter.getEyePosition().add(0.0D, -0.2D, 0.0D);
        Vec3 dir = aim.subtract(from).normalize();
        Vec3 end = from.add(dir.scale(range));
        HitResult block = level.clip(new ClipContext(from, end, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, shooter));
        if (block.getType() != HitResult.Type.MISS) {
            end = block.getLocation();
        }
        final Vec3 beamEnd = end;
        List<LivingEntity> hit = level.getEntitiesOfClass(LivingEntity.class, new AABB(from, beamEnd).inflate(1.0D),
                e -> e != shooter && e.isAlive() && !(e instanceof FlyingRobot) && !(e instanceof NetheriteKnight)
                        && !(e instanceof net.minecraft.world.entity.monster.Guardian)
                        && (e == target || e instanceof Enemy)
                        && e.getBoundingBox().inflate(0.3D).clip(from, beamEnd).isPresent());
        for (LivingEntity victim : hit) {
            victim.hurt(level.damageSources().indirectMagic(shooter, shooter), damage);
        }
        double length = from.distanceTo(beamEnd);
        DustParticleOptions beam = new DustParticleOptions(color, 1.4F);
        for (double d = 0.0D; d < length; d += 0.4D) {
            Vec3 p = from.add(dir.scale(d));
            level.sendParticles(beam, p.x, p.y, p.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        level.playSound(null, shooter.blockPosition(), SoundEvents.GUARDIAN_ATTACK, SoundSource.HOSTILE, 1.5F, 1.6F);
    }
}
