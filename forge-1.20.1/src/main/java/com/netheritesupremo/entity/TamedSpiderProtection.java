package com.netheritesupremo.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Aranha do Nether domada deixa de ser alvo dos mobs que cacam monstros hostis
 * (golens, robos, cavaleiros, guerreiros, lobos...). Piglins ainda revidam.
 */
public class TamedSpiderProtection {
    private static boolean protectedFrom(Entity attacker, Entity victim) {
        return victim instanceof NetherSpider spider && spider.isTame()
                && attacker instanceof Mob
                && !(attacker instanceof Enemy)
                && !(attacker instanceof AbstractPiglin);
    }

    @SubscribeEvent
    public void onTarget(LivingChangeTargetEvent event) {
        LivingEntity target = event.getNewTarget();
        if (target != null && protectedFrom(event.getEntity(), target)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        Entity source = event.getSource().getEntity();
        if (source != null && protectedFrom(source, event.getEntity())) {
            event.setCanceled(true);
        }
    }
}
