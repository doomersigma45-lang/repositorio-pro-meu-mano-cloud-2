package com.netheritesupremo.skeleton;

import com.netheritesupremo.entity.SkeletonKing;
import com.netheritesupremo.registry.ModItems;
import java.util.ArrayList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Faccao dos esqueletos: todo esqueleto (comum, stray, wither), o esqueleto villager e o Rei. Os esqueletos
 * ficam neutros (so reagem se forem atacados) e usam a Old Gun no lugar do arco. Excecoes hostis: os
 * esqueletos invocados pelo Rei durante a luta e os atacantes de uma guerra.
 */
@Mod.EventBusSubscriber(modid = "netheritesupremo")
public final class SkeletonFaction {
    public static final String WAR_TAG = "netheritesupremo_war_skeleton";
    public static final double ALERT_RADIUS = 40.0D;

    private SkeletonFaction() {
    }

    public static boolean isFaction(Entity e) {
        return e instanceof AbstractSkeleton || e instanceof SkeletonVillager || e instanceof SkeletonKing;
    }

    /** Esqueleto que ataca por conta propria (minion do Rei ou atacante de guerra). */
    public static boolean isHostile(Entity e) {
        return isBossMinion(e) || e.getTags().contains(WAR_TAG);
    }

    /** Esqueletos invocados pelo Rei Esqueleto ou pelo Necromante: ficam como bosses os fizeram. */
    public static boolean isBossMinion(Entity e) {
        return e.getTags().contains(SkeletonKing.MINION_TAG)
                || e.getTags().contains(com.netheritesupremo.entity.Necromancer.MINION_TAG);
    }

    /** Dispara a Old Gun do mob no alvo (50 a 150 de dano, definido no projetil). */
    public static void fireGun(Mob shooter, LivingEntity target) {
        if (shooter.level().isClientSide) {
            return;
        }
        OldGunBullet bullet = new OldGunBullet(shooter.level(), shooter);
        double dx = target.getX() - shooter.getX();
        double dz = target.getZ() - shooter.getZ();
        double dy = target.getEyeY() - bullet.getY();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        bullet.shoot(dx, dy + horizontal * 0.02D, dz, 3.0F, 1.0F);
        shooter.level().addFreshEntity(bullet);
        shooter.level().playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.HOSTILE, 1.6F, 0.6F);
    }

    private static boolean hasGunGoal(Mob mob) {
        for (WrappedGoal w : mob.goalSelector.getAvailableGoals()) {
            if (w.getGoal() instanceof GunAttackGoal) {
                return true;
            }
        }
        return false;
    }

    /** Esqueletos sem o proprio comportamento: tira o alvo automatico em jogador e poe a Old Gun na mao. */
    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof AbstractSkeleton skeleton) || skeleton.level().isClientSide) {
            return;
        }
        if ((skeleton.tickCount + skeleton.getId()) % 20 != 0) {
            return;
        }
        prepare(skeleton);
    }

    /** Aplica o comportamento da faccao (neutro + Old Gun); idempotente. */
    public static void prepare(AbstractSkeleton skeleton) {
        if (isBossMinion(skeleton)) {
            return; // esqueletos invocados por bosses continuam com a arma e hostis
        }
        boolean war = skeleton.getTags().contains(WAR_TAG);
        if (!hasGunGoal(skeleton)) {
            if (!war) {
                for (WrappedGoal w : new ArrayList<>(skeleton.targetSelector.getAvailableGoals())) {
                    if (w.getGoal() instanceof NearestAttackableTargetGoal) {
                        skeleton.targetSelector.removeGoal(w.getGoal());
                    }
                }
                LivingEntity t = skeleton.getTarget();
                if (t != null && t != skeleton.getLastHurtByMob()) {
                    skeleton.setTarget(null);
                }
            }
            skeleton.goalSelector.addGoal(2, new GunAttackGoal(skeleton, 1.0D, 4.0D, 22.0D));
        }
        if (!skeleton.getMainHandItem().is(ModItems.OLD_GUN.get())) {
            skeleton.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.OLD_GUN.get()));
            skeleton.setDropChance(EquipmentSlot.MAINHAND, 0.05F);
        }
    }

    /** Esqueletos nao se ferem entre si (tiros da Old Gun, flechas perdidas...). */
    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (attacker != null && isFaction(attacker) && isFaction(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    /** Se um da faccao e ferido por alguem de fora, os esqueletos por perto (e o Rei) defendem. */
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide || !isFaction(victim)) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker) || isFaction(attacker)) {
            return;
        }
        alert(victim, attacker);
    }

    public static void alert(LivingEntity victim, LivingEntity attacker) {
        if (attacker instanceof Player p && (p.isCreative() || p.isSpectator())) {
            return;
        }
        for (Mob ally : victim.level().getEntitiesOfClass(Mob.class, victim.getBoundingBox().inflate(ALERT_RADIUS),
                m -> m != victim && m.isAlive() && isFaction(m) && !(m instanceof SkeletonKing && isHostile(m)))) {
            if (ally.getTarget() == null && ally.getRandom().nextFloat() < 0.85F) {
                ally.setTarget(attacker);
            }
        }
    }


}
