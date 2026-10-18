package com.netheritesupremo.item;

import com.netheritesupremo.SupremoConfig;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Joia colorida. Segurando: Resistencia V e Visao Noturna (ver GemEvents). Botao direito:
 * explosao 75x a de um creeper onde o jogador esta olhando (recarga de 30 s).
 */
public class RainbowGemItem extends Item {
    public static final int COOLDOWN_TICKS = 600;
    public static final double RANGE = 100.0D;

    public RainbowGemItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide) {
            // Garante a Resistencia V antes da explosao (o usuario fica imune ao proprio estouro)
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 4, true, false, true));
            HitResult hit = player.pick(RANGE, 1.0F, false);
            Vec3 at = hit.getLocation();
            level.explode(player, at.x, at.y, at.z, SupremoConfig.RAINBOW_EXPLOSION_POWER.get().floatValue(),
                    SupremoConfig.RAINBOW_DESTROYS_BLOCKS.get()
                            ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE);
            level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 2.0F, 1.5F);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
