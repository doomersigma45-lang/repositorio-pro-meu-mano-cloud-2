package com.netheritesupremo.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.netheritesupremo.DragonEyePortals;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.network.ModNetwork;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Tecla R (Olho do Dragao ou Manopla Suprema na mao): tocar abre o portal; segurar e girar o scroll do mouse
 * troca a dimensao de destino (o scroll nao troca o item da hotbar enquanto R esta apertada).
 */
public final class PortalKey {
    public static final KeyMapping PORTAL = new KeyMapping("key.netheritesupremo.portal",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_R,
            "key.categories.netheritesupremo");
    private static boolean wasDown = false;
    private static boolean scrolled = false;

    private PortalKey() {
    }

    @Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class Register {
        @SubscribeEvent
        public static void onKeys(RegisterKeyMappingsEvent event) {
            event.register(PORTAL);
        }
    }

    @Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class Events {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            LocalPlayer player = Minecraft.getInstance().player;
            boolean down = player != null && PORTAL.isDown();
            if (wasDown && !down) {
                // soltou a tecla: se nao girou o scroll, e um toque -> abre o portal
                if (!scrolled && player != null && DragonEyePortals.canUse(player)) {
                    ModNetwork.CHANNEL.sendToServer(new ModNetwork.PortalPacket(0));
                }
                scrolled = false;
            }
            if (down && !wasDown) {
                scrolled = false;
            }
            wasDown = down;
        }

        @SubscribeEvent
        public static void onScroll(InputEvent.MouseScrollingEvent event) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || !PORTAL.isDown() || !DragonEyePortals.canUse(player)) {
                return;
            }
            event.setCanceled(true);
            scrolled = true;
            ModNetwork.CHANNEL.sendToServer(new ModNetwork.PortalPacket(event.getScrollDelta() > 0 ? -1 : 1));
        }
    }
}
