package com.netheritesupremo.parasite;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.skeleton.ModEffects;
import com.netheritesupremo.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Propagacao dos parasitas: infeccao ao ferir, conversao ao morrer e comportamento de parasita nos mobs Parasitados. */
@Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID)
public final class ParasiteEvents {
    private ParasiteEvents() {
    }

    /** Os efeitos de parasita so funcionam em mobs (nunca em jogadores) e nunca em parasitas. */
    @SubscribeEvent
    public static void onApplicable(MobEffectEvent.Applicable event) {
        MobEffect effect = event.getEffectInstance().getEffect();
        if (effect == ModEffects.INFESTATION.get() || effect == ModEffects.PARASITIZED.get()) {
            LivingEntity entity = event.getEntity();
            if (!(entity instanceof Mob) || entity instanceof ParasiteMob) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    /** Quem e ferido por um parasita (ou por um mob Parasitado) e infectado. */
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide) {
            return;
        }
        DamageSource source = event.getSource();
        if (ParasiteBehavior.isParasitic(source.getEntity()) || ParasiteBehavior.isParasitic(source.getDirectEntity())) {
            ParasiteBehavior.infect(victim);
        }
    }

    /** Sem fogo amigo: parasita nao machuca parasita (skulls do Wither, explosoes, mordidas). */
    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        Entity source = event.getSource().getEntity();
        if (source != null && ParasiteBehavior.isParasitic(source) && ParasiteBehavior.isParasitic(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide) {
            return;
        }
        if (victim instanceof ParasiteMob && !(victim instanceof ParasiteWorm) && victim.level() instanceof ServerLevel sl) {
            for (int i = 0; i < 3; i++) {
                ParasiteWorm worm = ModEntities.PARASITE_WORM.get().create(sl);
                if (worm != null) {
                    worm.moveTo(victim.getX() + (sl.random.nextDouble() - 0.5D), victim.getY() + 0.2D,
                            victim.getZ() + (sl.random.nextDouble() - 0.5D), sl.random.nextFloat() * 360.0F, 0.0F);
                    sl.addFreshEntity(worm);
                }
            }
        }
        if (victim instanceof Mob mob && !(mob instanceof ParasiteMob) && mob.hasEffect(ModEffects.INFESTATION.get())
                && ParasiteBehavior.versionFor(mob.getType()) != null) {
            event.setCanceled(true);
            ParasiteBehavior.spawnVersion(mob);
            return;
        }
        if (victim instanceof ServerPlayer player) {
            DamageSource source = event.getSource();
            if (ParasiteBehavior.isParasitic(source.getEntity()) || ParasiteBehavior.isParasitic(source.getDirectEntity())) {
                ParasiteBehavior.spawnPlayerParasite(player);
            }
        }
    }

    /** Mobs Parasitados ganham o objetivo de cacar e morder qualquer nao parasita. */
    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || mob.level().isClientSide) {
            return;
        }
        if (mob.tickCount % 10 == 0 && !(mob instanceof ParasiteMob) && mob.isInFluidType(ParasiteBlood.FLUID_TYPE.get())) {
            ParasiteBehavior.infect(mob);
        }
        if (mob.tickCount % 20 != 0) {
            return;
        }
        if (mob instanceof ParasiteMob) {
            ParasiteBehavior.updatePhase(mob);
            return;
        }
        if (!mob.hasEffect(ModEffects.PARASITIZED.get())) {
            return;
        }
        boolean chase = false;
        for (WrappedGoal w : mob.goalSelector.getAvailableGoals()) {
            if (w.getGoal() instanceof ParasiteChaseGoal) {
                chase = true;
                break;
            }
        }
        if (!chase) {
            mob.goalSelector.addGoal(1, new ParasiteChaseGoal(mob));
            mob.targetSelector.addGoal(1, new ParasiteTargetGoal(mob));
        }
        Entity target = mob.getTarget();
        if (target != null && ParasiteBehavior.isParasitic(target)) {
            mob.setTarget(null);
        }
    }
}
