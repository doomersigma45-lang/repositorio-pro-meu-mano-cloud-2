package com.netheritesupremo.item;

import com.netheritesupremo.SupremoConfig;
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
 * Joia colorida: segurando, da Resistencia 5 e Visao Noturna (ver GemEvents). Com o botao direito
 * solta uma explosao 75x maior que a de um creeper onde o jogador mira (ate 100 blocos).
 */
public class RainbowGemItem extends Item {
    public static final int COOLDOWN_TICKS = 600;

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
            // Garante a Resistencia 5 antes da explosao (o usuario fica imune a ela)
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 4, true, false, true));
            HitResult hit = player.pick(100.0D, 1.0F, false);
            Vec3 pos = hit.getLocation();
            Level.ExplosionInteraction mode = SupremoConfig.RAINBOW_DESTROYS_BLOCKS.get()
                    ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE;
            level.explode(player, pos.x, pos.y, pos.z, SupremoConfig.RAINBOW_EXPLOSION_POWER.get().floatValue(), false, mode);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
