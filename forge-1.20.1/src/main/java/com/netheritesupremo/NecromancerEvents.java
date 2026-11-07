package com.netheritesupremo;

import com.netheritesupremo.entity.NecromancerSkull;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NecromancerEvents {
    /** As cabecas de wither do necromante explodem, mas sem destruir blocos. */
    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (event.getExplosion().getDirectSourceEntity() instanceof NecromancerSkull
                || event.getExplosion().getDirectSourceEntity() instanceof com.netheritesupremo.entity.KingFireball) {
            event.getAffectedBlocks().clear();
        }
    }

    /** O que o Rei Esqueleto invoca nao dropa nem da experiencia (evita fazenda de loot). */
    @SubscribeEvent
    public void onDrops(net.minecraftforge.event.entity.living.LivingDropsEvent event) {
        var e = event.getEntity();
        if (e.getTags().contains(com.netheritesupremo.entity.SkeletonKing.MINION_TAG)
                || e.getTags().contains(com.netheritesupremo.entity.SkeletonKing.NECRO_TAG)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onXp(net.minecraftforge.event.entity.living.LivingExperienceDropEvent event) {
        var e = event.getEntity();
        if (e.getTags().contains(com.netheritesupremo.entity.SkeletonKing.MINION_TAG)
                || e.getTags().contains(com.netheritesupremo.entity.SkeletonKing.NECRO_TAG)) {
            event.setCanceled(true);
        }
    }
}
