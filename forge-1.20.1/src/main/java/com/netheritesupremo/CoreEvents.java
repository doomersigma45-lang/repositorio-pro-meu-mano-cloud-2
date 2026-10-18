package com.netheritesupremo;

import com.netheritesupremo.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Nucleo do Precursor. Segurando (qualquer mao): Forca VI e Health Boost 40. Tecla K (com o item na mao)
 * liga/desliga "atravessar paredes": o jogador fica sem colisao e voa (como no espectador, mas sem sair do modo).
 */
public class CoreEvents {
    public static final String TOGGLE = "netheritesupremo_phase_toggle";
    private static final String PHASING = "netheritesupremo_phasing";
    private static final String NOGRAV = "netheritesupremo_phase_nograv";
    private static final String FLY = "netheritesupremo_phase_fly";
    private static final String FX = "netheritesupremo_core_effects";
    public static final int STRENGTH_AMP = 5;      // Forca VI
    public static final int HEALTH_BOOST_AMP = 39; // Health Boost 40

    public static boolean holdsCore(Player player) {
        return player.getMainHandItem().is(ModItems.PRECURSOR_CORE.get())
                || player.getOffhandItem().is(ModItems.PRECURSOR_CORE.get());
    }

    /** Roda no cliente e no servidor: a fisica do jogador precisa concordar nos dois lados. */
    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        CompoundTag data = player.getPersistentData();
        boolean holding = holdsCore(player);
        if (!holding) {
            data.putBoolean(TOGGLE, false);
        }
        boolean phasing = data.getBoolean(PHASING);
        if (holding && data.getBoolean(TOGGLE)) {
            phasing = true;
        } else if (phasing && !player.level().noCollision(player, player.getBoundingBox())) {
            // Desligou dentro de um bloco: continua sem colisao ate sair, para nao sufocar
        } else {
            phasing = false;
        }
        data.putBoolean(PHASING, phasing);
        if (phasing) {
            player.noPhysics = true;
            player.setNoGravity(true);
            data.putBoolean(NOGRAV, true);
        } else if (data.getBoolean(NOGRAV)) {
            data.putBoolean(NOGRAV, false);
            player.setNoGravity(false);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        CompoundTag data = player.getPersistentData();
        // ---- voo enquanto atravessa paredes
        if (!player.isCreative() && !player.isSpectator()) {
            if (data.getBoolean(PHASING)) {
                if (!player.getAbilities().mayfly || !player.getAbilities().flying) {
                    player.getAbilities().mayfly = true;
                    player.getAbilities().flying = true;
                    data.putBoolean(FLY, true);
                    player.onUpdateAbilities();
                }
            } else if (data.getBoolean(FLY)) {
                data.putBoolean(FLY, false);
                player.getAbilities().flying = false;
                // O bonus de voo da armadura (SupremoEvents) recoloca mayfly sozinho se ele valer
                player.getAbilities().mayfly = SupremoEvents.hasSetBonus(player);
                player.onUpdateAbilities();
            }
        }
        // ---- efeitos
        if (player.tickCount % 10 != 0) {
            return;
        }
        if (holdsCore(player)) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, STRENGTH_AMP, true, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 40, HEALTH_BOOST_AMP, true, false, true));
            data.putBoolean(FX, true);
        } else if (data.getBoolean(FX)) {
            data.putBoolean(FX, false);
            MobEffectInstance str = player.getEffect(MobEffects.DAMAGE_BOOST);
            if (str != null && str.isAmbient() && str.getAmplifier() == STRENGTH_AMP) {
                player.removeEffect(MobEffects.DAMAGE_BOOST);
            }
            MobEffectInstance hb = player.getEffect(MobEffects.HEALTH_BOOST);
            if (hb != null && hb.isAmbient() && hb.getAmplifier() == HEALTH_BOOST_AMP) {
                player.removeEffect(MobEffects.HEALTH_BOOST);
            }
        }
    }
}
