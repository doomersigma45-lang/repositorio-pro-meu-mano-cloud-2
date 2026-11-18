package com.netheritesupremo.parasite;

import com.netheritesupremo.NetheriteSupremo;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Eventos gerais dos parasitas: nenhum nasce filhote, pocao da Bruxa Parasita (infecta os mobs atingidos) e Enderman
 * Parasita que leva outros parasitas junto quando se teletransporta.
 */
@Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID)
public final class ParasiteGeneralEvents {
    private ParasiteGeneralEvents() {
    }

    /** Parasitas nunca sao filhotes (nem os que carregam do disco, nem os de ovo, nem os de zumbi/animal bebe). */
    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof Mob mob) || !(mob instanceof ParasiteMob)) {
            return;
        }
        if (mob.isBaby()) {
            mob.setBaby(false);
        }
    }

    /** Impacto da pocao da bruxa: infecta mobs ate 4 blocos, enfraquece/envenena quem nao e parasita. */
    @SubscribeEvent
    public static void onPotionImpact(ProjectileImpactEvent event) {
        if (!(event.getProjectile() instanceof ThrownPotion potion) || potion.level().isClientSide
                || !potion.getPersistentData().getBoolean(ParasiteWitch.POTION_TAG) || !(potion.level() instanceof ServerLevel level)) {
            return;
        }
        event.setCanceled(true);
        double x = event.getRayTraceResult().getLocation().x;
        double y = event.getRayTraceResult().getLocation().y;
        double z = event.getRayTraceResult().getLocation().z;
        AABB box = new AABB(x - 4.0D, y - 2.0D, z - 4.0D, x + 4.0D, y + 2.0D, z + 4.0D);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, le -> le.isAlive() && !(le instanceof ParasiteMob))) {
            if (e instanceof Mob m) {
                ParasiteBehavior.infect(m);
            }
            e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0));
            if (e instanceof Player) {
                e.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
            }
        }
        level.sendParticles(ParticleTypes.WITCH, x, y + 0.3D, z, 40, 1.2D, 0.5D, 1.2D, 0.05D);
        level.sendParticles(ParticleTypes.SNEEZE, x, y + 0.3D, z, 25, 1.2D, 0.5D, 1.2D, 0.05D);
        level.playSound(null, x, y, z, SoundEvents.SPLASH_POTION_BREAK, SoundSource.HOSTILE, 1.0F, 0.7F);
        potion.discard();
    }

    /** Parasitas num raio de 6 blocos que podem acompanhar o Enderman Parasita (ate 4, sem endermen nem bosses). */
    public static List<Mob> mates(EnderMan leader) {
        return leader.level().getEntitiesOfClass(Mob.class, leader.getBoundingBox().inflate(6.0D),
                e -> e != leader && e instanceof ParasiteMob && !(e instanceof EnderMan) && !(e instanceof AirborneParasite)
                        && e.getMaxHealth() <= 300.0F && e.isAlive()).stream().limit(4).toList();
    }

    public static void moveMates(List<Mob> mates, double x, double y, double z) {
        for (Mob m : mates) {
            double tx = x + m.getRandom().nextDouble() * 4.0D - 2.0D;
            double tz = z + m.getRandom().nextDouble() * 4.0D - 2.0D;
            m.getNavigation().stop();
            if (m.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.PORTAL, m.getX(), m.getY() + 1.0D, m.getZ(), 15, 0.4D, 0.8D, 0.4D, 0.2D);
            }
            if (!m.randomTeleport(tx, y, tz, true)) {
                continue;
            }
            if (m.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.PORTAL, m.getX(), m.getY() + 1.0D, m.getZ(), 15, 0.4D, 0.8D, 0.4D, 0.2D);
            }
        }
    }

    /** Teletransporte normal do Enderman Parasita (levado por dano, etc.): os parasitas por perto vao junto. */
    @SubscribeEvent
    public static void onEnderTeleport(EntityTeleportEvent.EnderEntity event) {
        if (event.getEntity() instanceof ParasiteEnderman em && !em.level().isClientSide) {
            moveMates(mates(em), event.getTargetX(), event.getTargetY(), event.getTargetZ());
        }
    }
}
