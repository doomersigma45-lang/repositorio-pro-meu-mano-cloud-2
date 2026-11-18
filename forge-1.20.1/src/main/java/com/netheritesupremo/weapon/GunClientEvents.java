package com.netheritesupremo.weapon;

import com.netheritesupremo.NetheriteSupremo;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Segurar Uzi/AK atirando nao deixa o jogador lento (o jogo reduz a velocidade de quem usa um item; aqui desfazemos). */
@Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID, value = Dist.CLIENT)
public final class GunClientEvents {
    private GunClientEvents() {
    }

    @SubscribeEvent
    public static void onMovement(MovementInputUpdateEvent event) {
        Player p = event.getEntity();
        if (p.isUsingItem() && p.getUseItem().getItem() instanceof AutoGunItem) {
            event.getInput().leftImpulse *= 5.0F;
            event.getInput().forwardImpulse *= 5.0F;
        }
    }
}
