package com.netheritesupremo.parasite;

import com.netheritesupremo.registry.ModEntities;
import com.netheritesupremo.skeleton.ModEffects;
import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;

/** Regras comuns do sistema de parasitas: quem e presa, quem pode ser infectado e quem vira o que. */
public final class ParasiteBehavior {
    public static final int INFESTATION_TICKS = 6000;
    public static final int PARASITIZED_TICKS = 2400;

    /** Presa: qualquer ser vivo que nao seja parasita (nem parasitado), armor stand ou jogador criativo/espectador. */
    public static final Predicate<LivingEntity> PREY = e -> e.isAlive() && !isParasitic(e)
            && !(e instanceof ArmorStand)
            && !(e instanceof Player p && (p.isCreative() || p.isSpectator()));

    public static final String SINCE_TAG = "ParasiteSince";
    public static final String APPLIED_TAG = "ParasitePhaseApplied";
    public static final long DAY = 24000L;

    private ParasiteBehavior() {
    }

    /** Marca o dia (tempo do mundo) em que o mob foi infectado, se ainda nao tiver marca. */
    public static void markInfected(Mob mob) {
        if (!mob.getPersistentData().contains(SINCE_TAG)) {
            mob.getPersistentData().putLong(SINCE_TAG, mob.level().getDayTime());
        }
    }

    /** Fase da infeccao: dias 0-5 = 1, 5-11 = 2, 11 em diante = 3. */
    public static int phaseOf(Mob mob) {
        markInfected(mob);
        long since = mob.getPersistentData().getLong(SINCE_TAG);
        long now = mob.level().getDayTime();
        if (now < since) {
            mob.getPersistentData().putLong(SINCE_TAG, now);
            return 1;
        }
        double days = (now - since) / (double) DAY;
        return days < 5.0D ? 1 : (days < 11.0D ? 2 : 3);
    }

    /** Aplica (ou remove) o efeito escondido da fase e cura o parasita quando a fase muda. */
    public static void updatePhase(Mob mob) {
        int phase = phaseOf(mob);
        if (phase >= 2) {
            mob.addEffect(new MobEffectInstance(ModEffects.PARASITE_PHASE.get(), 100, phase - 2, false, false, false));
        } else {
            mob.removeEffect(ModEffects.PARASITE_PHASE.get());
        }
        if (mob.getPersistentData().getInt(APPLIED_TAG) != phase) {
            mob.getPersistentData().putInt(APPLIED_TAG, phase);
            if (phase >= 2) {
                mob.setHealth(mob.getMaxHealth());
            }
        }
    }

    public static boolean isParasitic(Entity e) {
        if (e instanceof ParasiteMob) {
            return true;
        }
        return e instanceof LivingEntity l && l.hasEffect(ModEffects.PARASITIZED.get());
    }

    public static void addMeleeGoals(PathfinderMob mob, double speed) {
        mob.goalSelector.addGoal(0, new FloatGoal(mob));
        mob.goalSelector.addGoal(2, new MeleeAttackGoal(mob, speed, true));
        mob.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(mob, 0.9D));
        mob.goalSelector.addGoal(6, new LookAtPlayerGoal(mob, Player.class, 8.0F));
        mob.goalSelector.addGoal(7, new RandomLookAroundGoal(mob));
    }

    public static void addTargets(PathfinderMob mob) {
        mob.targetSelector.addGoal(1, new HurtByTargetGoal(mob, ParasiteMob.class));
        mob.targetSelector.addGoal(2, new ParasiteTargetGoal(mob));
    }

    /** Versao parasita do tipo, ou null se o tipo nao tiver uma. */
    public static EntityType<? extends Mob> versionFor(EntityType<?> t) {
        if (t == EntityType.COW) {
            return ModEntities.PARASITE_COW.get();
        }
        if (t == EntityType.PIG) {
            return ModEntities.PARASITE_PIG.get();
        }
        if (t == EntityType.CHICKEN) {
            return ModEntities.PARASITE_CHICKEN.get();
        }
        if (t == EntityType.ZOMBIE) {
            return ModEntities.PARASITE_ZOMBIE.get();
        }
        if (t == EntityType.SKELETON) {
            return ModEntities.PARASITE_SKELETON.get();
        }
        if (t == EntityType.CREEPER) {
            return ModEntities.PARASITE_CREEPER.get();
        }
        if (t == EntityType.SPIDER) {
            return ModEntities.PARASITE_SPIDER.get();
        }
        if (t == EntityType.ENDERMAN) {
            return ModEntities.PARASITE_ENDERMAN.get();
        }
        return null;
    }

    /**
     * Infecta um mob. Com versao parasita: Infestacao (ao morrer vira a versao parasita).
     * Sem versao: Parasitado (passa a agir como parasita). Nunca afeta jogadores.
     */
    public static boolean infect(LivingEntity victim) {
        if (!(victim instanceof Mob mob) || mob.level().isClientSide) {
            return false;
        }
        if (isParasitic(mob) || mob.hasEffect(ModEffects.INFESTATION.get())
                || mob instanceof EnderDragon || mob instanceof WitherBoss || mob.getMaxHealth() > 400.0F) {
            return false;
        }
        markInfected(mob);
        boolean hasVersion = versionFor(mob.getType()) != null;
        mob.addEffect(new MobEffectInstance(hasVersion ? ModEffects.INFESTATION.get() : ModEffects.PARASITIZED.get(),
                hasVersion ? INFESTATION_TICKS : PARASITIZED_TICKS, 0, false, true, true));
        if (mob.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SNEEZE, mob.getX(), mob.getY() + mob.getBbHeight() * 0.6D, mob.getZ(),
                    12, mob.getBbWidth() * 0.4D, mob.getBbHeight() * 0.3D, mob.getBbWidth() * 0.4D, 0.02D);
        }
        return true;
    }

    /** Troca o mob infectado pela sua versao parasita. */
    public static void spawnVersion(Mob victim) {
        EntityType<? extends Mob> type = versionFor(victim.getType());
        if (type == null || !(victim.level() instanceof ServerLevel sl)) {
            return;
        }
        Mob p = type.create(sl);
        if (p == null) {
            return;
        }
        p.moveTo(victim.getX(), victim.getY(), victim.getZ(), victim.getYRot(), victim.getXRot());
        p.setYHeadRot(victim.getYHeadRot());
        if (victim.getPersistentData().contains(SINCE_TAG)) {
            p.getPersistentData().putLong(SINCE_TAG, victim.getPersistentData().getLong(SINCE_TAG));
        }
        if (victim.hasCustomName()) {
            p.setCustomName(victim.getCustomName());
        }
        for (EquipmentSlot s : EquipmentSlot.values()) {
            if (s.getType() == EquipmentSlot.Type.ARMOR) {
                p.setItemSlot(s, victim.getItemBySlot(s).copy());
            }
        }
        sl.addFreshEntity(p);
        burst(sl, p);
        victim.discard();
    }

    /** O jogador morto por um parasita levanta como Jogador Parasita. */
    public static void spawnPlayerParasite(Player player) {
        if (!(player.level() instanceof ServerLevel sl)) {
            return;
        }
        Mob p = ModEntities.PARASITE_PLAYER.get().create(sl);
        if (p == null) {
            return;
        }
        p.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0.0F);
        markInfected(p);
        sl.addFreshEntity(p);
        burst(sl, p);
    }

    private static void burst(ServerLevel sl, Mob p) {
        sl.sendParticles(ParticleTypes.SNEEZE, p.getX(), p.getY() + p.getBbHeight() * 0.5D, p.getZ(),
                30, p.getBbWidth() * 0.6D, p.getBbHeight() * 0.4D, p.getBbWidth() * 0.6D, 0.05D);
        sl.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.SLIME_SQUISH, SoundSource.HOSTILE, 1.3F, 0.55F);
    }
}
