package com.netheritesupremo;

import com.netheritesupremo.item.GauntletItem;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Compatibilidade com Avaritia / Re-Avaritia (sem dependencia de compilacao, so por nome registrado "avaritia"):
 * - a armadura de netherite completa anula o dano das espadas desse mod (inclusive o tipo de dano avaritia:infinity);
 * - a espada de netherite e a Manopla Suprema conseguem ferir quem veste armadura desse mod, mesmo com a imunidade dela.
 */
public class AvaritiaCompat {
    private static final String NS = "avaritia";
    private static final ResourceLocation INFINITY_TYPE = new ResourceLocation(NS, "infinity");
    /** dano original do golpe (antes da armadura), por vitima, para a armadura do outro mod nao zerar o dano. */
    private static final Map<UUID, Float> RAW = new HashMap<>();

    private static boolean isAvaritia(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return id != null && NS.equals(id.getNamespace());
    }

    /** Dano de espada do mod: tipo avaritia:infinity ou atacante com espada do mod na mao. */
    static boolean isAvaritiaSword(DamageSource source) {
        if (source.typeHolder().unwrapKey().map(k -> INFINITY_TYPE.equals(k.location())).orElse(false)) {
            return true;
        }
        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity living) {
            ItemStack held = living.getMainHandItem();
            if (isAvaritia(held)) {
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(held.getItem());
                return held.getItem() instanceof SwordItem || id.getPath().contains("sword");
            }
        }
        return false;
    }

    static boolean wearsAvaritiaArmor(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack stack = entity.getItemBySlot(slot);
                if (stack.getItem() instanceof ArmorItem && isAvaritia(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Atacante com espada de netherite ou Manopla Suprema na mao. */
    static boolean usesOurWeapon(DamageSource source) {
        if (source.getEntity() instanceof LivingEntity living) {
            return living.getMainHandItem().is(Items.NETHERITE_SWORD) || !GauntletEvents.held(living).isEmpty();
        }
        return false;
    }

    // ---------------------------------------------- armadura de netherite anula as espadas do mod
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttackHigh(LivingAttackEvent event) {
        if (SupremoEvents.hasSetBonus(event.getEntity()) && isAvaritiaSword(event.getSource())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onHurtHigh(LivingHurtEvent event) {
        if (SupremoEvents.hasSetBonus(event.getEntity()) && isAvaritiaSword(event.getSource())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamageLow(LivingDamageEvent event) {
        if (SupremoEvents.hasSetBonus(event.getEntity()) && isAvaritiaSword(event.getSource())) {
            event.setAmount(0.0F);
            return;
        }
        // ---- espada de netherite / manopla ferem a armadura do mod: dano completo, sem a reducao dela
        if (usesOurWeapon(event.getSource()) && wearsAvaritiaArmor(event.getEntity())) {
            Float raw = RAW.remove(event.getEntity().getUUID());
            if (raw != null && raw > event.getAmount()) {
                event.setAmount(raw);
            }
        }
    }

    // ---------------------------------------------- espada de netherite / manopla furam a imunidade da armadura do mod
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onAttackLow(LivingAttackEvent event) {
        if (event.isCanceled() && usesOurWeapon(event.getSource()) && wearsAvaritiaArmor(event.getEntity())
                && !SupremoEvents.hasSetBonus(event.getEntity())) {
            event.setCanceled(false);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onHurtLow(LivingHurtEvent event) {
        if (usesOurWeapon(event.getSource()) && wearsAvaritiaArmor(event.getEntity())
                && !SupremoEvents.hasSetBonus(event.getEntity())) {
            if (event.isCanceled()) {
                event.setCanceled(false);
            }
            RAW.put(event.getEntity().getUUID(), event.getAmount());
        }
    }
}
