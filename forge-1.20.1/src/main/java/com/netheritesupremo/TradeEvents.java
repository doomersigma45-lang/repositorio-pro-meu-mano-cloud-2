package com.netheritesupremo;

import com.netheritesupremo.trade.UmbralTrades;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Melhora as trocas do mercador viajante (uma unica vez por mercador). */
public class TradeEvents {
    private static final String KEY = "netheritesupremo_improved";

    @SubscribeEvent
    public void onJoin(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || event.getEntity().getType() != EntityType.WANDERING_TRADER
                || !(event.getEntity() instanceof WanderingTrader trader)) {
            return;
        }
        if (trader.getPersistentData().getBoolean(KEY)) {
            return;
        }
        trader.getPersistentData().putBoolean(KEY, true);
        // Adiado: a busca do mapa pode gerar chunks e nao deve rodar no meio do carregamento
        level.getServer().execute(() -> {
            if (!trader.isRemoved()) {
                UmbralTrades.improveWanderingTrader(trader, level);
            }
        });
    }
}
