package com.netheritesupremo;

import com.netheritesupremo.entity.KnightSpawnData;
import com.netheritesupremo.entity.NetheriteKnight;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KnightEvents {
    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof NetheriteKnight knight && !knight.level().isClientSide) {
            MinecraftServer server = knight.level().getServer();
            if (server != null) {
                KnightSpawnData.get(server).onKnightDied(knight.getUUID());
            }
        }
    }
}
