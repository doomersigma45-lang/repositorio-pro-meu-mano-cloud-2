package com.netheritesupremo;

import com.netheritesupremo.entity.NecromancerSkull;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NecromancerEvents {
    /** As cabecas de wither do necromante explodem, mas sem destruir blocos. */
    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (event.getExplosion().getDirectSourceEntity() instanceof NecromancerSkull) {
            event.getAffectedBlocks().clear();
        }
    }
}
