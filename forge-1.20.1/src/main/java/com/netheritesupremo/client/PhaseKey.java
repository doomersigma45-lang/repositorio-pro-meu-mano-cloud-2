package com.netheritesupremo.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.netheritesupremo.CoreEvents;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.network.ModNetwork;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Tecla K: liga/desliga atravessar paredes (so com o Nucleo do Precursor na mao). */
public final class PhaseKey {
    public static final KeyMapping PHASE = new KeyMapping("key.netheritesupremo.phase",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_K,
            "key.categories.netheritesupremo");

    private PhaseKey() {
    }

    @Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class Register {
        @SubscribeEvent
        public static void onKeys(RegisterKeyMappingsEvent event) {
            event.register(PHASE);
        }
    }

    @Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class Ticker {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            LocalPlayer player = Minecraft.getInstance().player;
            while (PHASE.consumeClick()) {
                if (player == null || !CoreEvents.holdsCore(player)) {
                    continue;
                }
                boolean on = !player.getPersistentData().getBoolean(CoreEvents.TOGGLE);
                player.getPersistentData().putBoolean(CoreEvents.TOGGLE, on);
                ModNetwork.CHANNEL.sendToServer(new ModNetwork.PhasePacket(on));
                player.displayClientMessage(Component.translatable(on
                        ? "message.netheritesupremo.phase_on" : "message.netheritesupremo.phase_off"), true);
            }
        }
    }
}
