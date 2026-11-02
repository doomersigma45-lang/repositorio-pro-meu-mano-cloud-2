package com.netheritesupremo;

import com.netheritesupremo.item.GauntletItem;
import com.netheritesupremo.registry.ModItems;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Manopla Suprema, segurando: poderes dos quatro itens x10.
 * Nucleo: Forca VI (+18 dano) e Health Boost 40 (+160 vida) x10; Olho do Dragao: +30 vida e dano x2 -> x10,
 * voo 10x mais rapido que o do Olho (ver DragonEyeEvents); Joia: Resistencia V, visao noturna e imunidade a explosoes;
 * Rubi x10 no botao direito. Tambem conta abates para o dano infinito.
 */
public class GauntletEvents {
    private static final UUID HEALTH_ID = UUID.fromString("7a1c2e55-3b0d-4f8e-8f11-2c9a6d0b5e01");
    private static final UUID DAMAGE_ID = UUID.fromString("7a1c2e55-3b0d-4f8e-8f11-2c9a6d0b5e02");
    private static final UUID MULT_ID = UUID.fromString("7a1c2e55-3b0d-4f8e-8f11-2c9a6d0b5e03");
    public static final float GAUNTLET_FLYING_SPEED = 0.5F; // 10x o voo normal (~109 blocos/s)

    public static ItemStack held(LivingEntity entity) {
        if (entity.getMainHandItem().getItem() instanceof GauntletItem) {
            return entity.getMainHandItem();
        }
        if (entity.getOffhandItem().getItem() instanceof GauntletItem) {
            return entity.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }

    public static boolean holdsGauntlet(Player player) {
        return !held(player).isEmpty();
    }

    private static void setModifier(Player player, Attribute attribute, UUID id, String name, double amount,
            AttributeModifier.Operation op, boolean on) {
        AttributeInstance inst = player.getAttribute(attribute);
        if (inst == null) {
            return;
        }
        AttributeModifier existing = inst.getModifier(id);
        if (on && existing == null) {
            inst.addTransientModifier(new AttributeModifier(id, name, amount, op));
        } else if (!on && existing != null) {
            inst.removeModifier(id);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        boolean on = holdsGauntlet(player);
        setModifier(player, Attributes.MAX_HEALTH, HEALTH_ID, "Gauntlet health", 1900.0D,
                AttributeModifier.Operation.ADDITION, on);
        setModifier(player, Attributes.ATTACK_DAMAGE, DAMAGE_ID, "Gauntlet strength", 180.0D,
                AttributeModifier.Operation.ADDITION, on);
        setModifier(player, Attributes.ATTACK_DAMAGE, MULT_ID, "Gauntlet dragon eye", 19.0D,
                AttributeModifier.Operation.MULTIPLY_TOTAL, on);
        if (on && player.tickCount % 10 == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 4, true, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 260, 0, true, false, true));
            if (player.getHealth() < player.getMaxHealth() && player.tickCount % 20 == 0) {
                player.heal(100.0F);
            }
        }
    }

    /** Imunidade a explosoes enquanto segura (efeito da joia). */
    @SubscribeEvent
    public void onAttacked(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player player && holdsGauntlet(player)
                && event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
            event.setCanceled(true);
        }
    }

    /** Conta abates de mobs/jogadores feitos com a manopla na mao. */
    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }
        LivingEntity victim = event.getEntity();
        if (!(victim instanceof Mob) && !(victim instanceof Player)) {
            return;
        }
        ItemStack stack = held(player);
        if (stack.isEmpty()) {
            return;
        }
        CompoundTag tag = stack.getOrCreateTag();
        int before = tag.getInt(GauntletItem.KILLS);
        tag.putInt(GauntletItem.KILLS, before + 1);
        if (before + 1 == GauntletItem.INFINITE_KILLS) {
            player.displayClientMessage(net.minecraft.network.chat.Component
                    .translatable("message.netheritesupremo.gauntlet.infinite"), false);
        }
    }

    /** Com 100+ abates o dano e infinito: passa por armadura, resistencia e absorcao. */
    @SubscribeEvent
    public void onDamage(LivingDamageEvent event) {
        if (event.getSource().getEntity() instanceof Player player && GauntletItem.isInfinite(held(player))
                && event.getEntity() != player && !SupremoEvents.hasSetBonus(event.getEntity())) {
            // Quem veste a armadura de netherite completa resiste ao dano infinito (leva so o dano normal reduzido)
            event.setAmount(Float.MAX_VALUE);
        }
    }
}
