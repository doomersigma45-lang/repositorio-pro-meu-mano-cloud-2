package com.netheritesupremo.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Aliados alienigenas: Robos (e variantes), Robos Bomba, Netherbots, Rainbow Bot, Precursor, Soldados Precursores,
 * Naves e Aldeoes Precursores (inclusive hibridos). Nunca escolhem uns aos outros como alvo nem se ferem
 * (lasers, explosoes, golpes).
 */
public class AlienAllies {
    public static boolean isAlien(Entity e) {
        return e instanceof Robot || e instanceof FlyingRobot || e instanceof PrecursorSoldier
                || e instanceof AlienShip || e instanceof PrecursorVillager;
    }

    @SubscribeEvent
    public void onTarget(LivingChangeTargetEvent event) {
        LivingEntity target = event.getNewTarget();
        if (target != null && isAlien(event.getEntity()) && isAlien(target)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (attacker != null && attacker != event.getEntity() && isAlien(attacker) && isAlien(event.getEntity())) {
            event.setCanceled(true);
        }
    }
}
