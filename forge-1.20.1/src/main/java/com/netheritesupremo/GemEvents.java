package com.netheritesupremo;

import com.netheritesupremo.registry.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Segurando a joia colorida (em qualquer mao): Resistencia V e Visao Noturna. */
public class GemEvents {
    private static final String KEY = "netheritesupremo_gem_effects";

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.END || player.level().isClientSide || player.tickCount % 10 != 0) {
            return;
        }
        boolean holding = player.getMainHandItem().is(ModItems.RAINBOW_GEM.get())
                || player.getOffhandItem().is(ModItems.RAINBOW_GEM.get());
        if (holding) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 4, true, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 260, 0, true, false, true));
            player.getPersistentData().putBoolean(KEY, true);
        } else if (player.getPersistentData().getBoolean(KEY)) {
            // Largou a joia: tira so os efeitos dela (marcados como "ambient")
            player.getPersistentData().putBoolean(KEY, false);
            MobEffectInstance res = player.getEffect(MobEffects.DAMAGE_RESISTANCE);
            if (res != null && res.isAmbient() && res.getAmplifier() == 4) {
                player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
            }
            MobEffectInstance nv = player.getEffect(MobEffects.NIGHT_VISION);
            if (nv != null && nv.isAmbient()) {
                player.removeEffect(MobEffects.NIGHT_VISION);
            }
        }
    }
}
