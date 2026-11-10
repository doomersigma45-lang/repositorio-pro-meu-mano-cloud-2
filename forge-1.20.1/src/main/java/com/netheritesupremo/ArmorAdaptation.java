package com.netheritesupremo;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Adaptacao da armadura de netherite: ao encontrar um jogador ou mob (que nao use o conjunto de netherite)
 * com armadura SUPERIOR, as pecas se adaptam e passam a superar essa armadura (armadura, resistencia da
 * armadura, Resistencia e Forca). Contra armaduras iguais ou mais fracas, nada muda. A adaptacao fica
 * gravada em cada peca (NBT "NetheriteAdapt") e nunca diminui.
 */
@Mod.EventBusSubscriber(modid = "netheritesupremo")
public final class ArmorAdaptation {
    public static final String KEY = "NetheriteAdapt";
    public static final String ARMOR = "Armor";
    public static final String TOUGHNESS = "Toughness";
    public static final String RESIST = "Resist";
    public static final String STRENGTH = "Strength";
    private static final String COOLDOWN = "netheritesupremo_adapt_cd";
    private static final double SCAN_RADIUS = 24.0D;
    private static final int COOLDOWN_TICKS = 100;
    public static final int MAX_RESIST = 4;   // Resistencia V = imune a dano
    private static final int MAX_STRENGTH = 6;
    public static final int EFFECT_CAP_STRENGTH = 127; // limite do byte do amplificador
    private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    private ArmorAdaptation() {
    }

    private static boolean isNetherite(ItemStack stack) {
        Item i = stack.getItem();
        return i == Items.NETHERITE_HELMET || i == Items.NETHERITE_CHESTPLATE || i == Items.NETHERITE_LEGGINGS
                || i == Items.NETHERITE_BOOTS;
    }

    private static UUID id(String what, EquipmentSlot slot) {
        return UUID.nameUUIDFromBytes(("netheritesupremo_adapt_" + what + "_" + slot.getName()).getBytes(StandardCharsets.UTF_8));
    }

    /** Maior multiplicador (2x, 4x, 8x, 16x) entre as pecas de netherite vestidas; 1 sem encantamento. */
    public static int multiplier(LivingEntity entity) {
        int m = 1;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (isNetherite(stack)) {
                m = Math.max(m, com.netheritesupremo.enchant.ModEnchantments.multiplierOf(stack));
            }
        }
        return m;
    }

    /** Amplificador final do efeito: nivel adaptado vezes o multiplicador do encantamento, ate {@code cap}. */
    public static int effectAmp(LivingEntity entity, String key, int base, int cap) {
        long level = (long) (bonusAmp(entity, key, base) + 1) * multiplier(entity);
        return (int) Math.min(cap, level - 1);
    }

    /** Maior valor adaptado (ou {@code base}) entre as pecas vestidas. */
    public static int bonusAmp(LivingEntity entity, String key, int base) {
        int amp = base;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (isNetherite(stack)) {
                CompoundTag tag = stack.getTagElement(KEY);
                if (tag != null && tag.contains(key)) {
                    amp = Math.max(amp, tag.getInt(key));
                }
            }
        }
        return amp;
    }

    /**
     * Roda depois de SupremoEvents (que redefine a armadura base da peca) e ANTES do multiplicador
     * (LOWEST), que assim multiplica tambem a adaptacao: encantamento 2x/4x/8x/16x vale para ela.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onAttributes(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (!isNetherite(stack) || !stack.hasTag() || !(stack.getItem() instanceof ArmorItem armor)
                || armor.getEquipmentSlot() != event.getSlotType()) {
            return;
        }
        CompoundTag tag = stack.getTagElement(KEY);
        if (tag == null) {
            return;
        }
        double a = tag.getDouble(ARMOR);
        double t = tag.getDouble(TOUGHNESS);
        if (a > 0) {
            event.addModifier(Attributes.ARMOR, new AttributeModifier(id("armor", event.getSlotType()),
                    "Netherite adaptation", a, AttributeModifier.Operation.ADDITION));
        }
        if (t > 0) {
            event.addModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id("toughness", event.getSlotType()),
                    "Netherite adaptation", t, AttributeModifier.Operation.ADDITION));
        }
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!isNetherite(stack)) {
            return;
        }
        CompoundTag tag = stack.getTagElement(KEY);
        if (tag == null) {
            return;
        }
        if (tag.contains(RESIST)) {
            event.getToolTip().add(Component.translatable("tooltip.netheritesupremo.adapt_resist", tag.getInt(RESIST) + 1));
        }
        if (tag.contains(STRENGTH)) {
            event.getToolTip().add(Component.translatable("tooltip.netheritesupremo.adapt_strength", tag.getInt(STRENGTH) + 1));
        }
    }

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        LivingEntity wearer = event.getEntity();
        if (wearer.level().isClientSide || (wearer.tickCount + wearer.getId()) % 20 != 0
                || !wearer.getItemBySlot(EquipmentSlot.CHEST).is(Items.NETHERITE_CHESTPLATE)
                || !SupremoEvents.hasSetBonus(wearer)) {
            return;
        }
        long now = wearer.level().getGameTime();
        if (now < wearer.getPersistentData().getLong(COOLDOWN)) {
            return;
        }
        double bestArmor = -1.0D;
        double bestTough = -1.0D;
        int bestResist = -1;
        int bestStrength = -1;
        for (LivingEntity other : wearer.level().getEntitiesOfClass(LivingEntity.class,
                wearer.getBoundingBox().inflate(SCAN_RADIUS),
                o -> o != wearer && o.isAlive() && (o instanceof Player || o instanceof Mob) && !SupremoEvents.hasSetBonus(o))) {
            bestArmor = Math.max(bestArmor, other.getAttributeValue(Attributes.ARMOR));
            bestTough = Math.max(bestTough, other.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
            MobEffectInstance res = other.getEffect(MobEffects.DAMAGE_RESISTANCE);
            if (res != null) {
                bestResist = Math.max(bestResist, res.getAmplifier());
            }
            MobEffectInstance str = other.getEffect(MobEffects.DAMAGE_BOOST);
            if (str != null) {
                bestStrength = Math.max(bestStrength, str.getAmplifier());
            }
        }
        double ownArmor = wearer.getAttributeValue(Attributes.ARMOR);
        double ownTough = wearer.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        int mult = multiplier(wearer);
        int ownResist = effectAmp(wearer, RESIST, 3, MAX_RESIST);
        int ownStrength = effectAmp(wearer, STRENGTH, 1, EFFECT_CAP_STRENGTH);
        // so melhora o que a outra armadura tem de SUPERIOR; o resto fica igual
        double addArmor = bestArmor > ownArmor ? Math.min(bestArmor - ownArmor + 1.0D, 1000.0D) : 0.0D;
        double addTough = bestTough > ownTough ? Math.min(bestTough - ownTough + 1.0D, 1000.0D) : 0.0D;
        // nivel guardado na peca (antes do multiplicador) que, vezes o multiplicador, passa a outra armadura
        int storedResist = -1;
        if (bestResist >= ownResist && ownResist < MAX_RESIST) {
            int target = Math.min(MAX_RESIST, bestResist + 1);
            storedResist = Math.max(3, (target + 1 + mult - 1) / mult - 1);
        }
        int storedStrength = -1;
        if (bestStrength >= ownStrength && ownStrength < EFFECT_CAP_STRENGTH) {
            int target = Math.min(MAX_STRENGTH, bestStrength + 1);
            storedStrength = Math.max(1, (target + 1 + mult - 1) / mult - 1);
        }
        boolean upResist = storedResist > bonusAmp(wearer, RESIST, 3);
        boolean upStrength = storedStrength > bonusAmp(wearer, STRENGTH, 1);
        if (addArmor <= 0.0D && addTough <= 0.0D && !upResist && !upStrength) {
            return;
        }
        List<ItemStack> worn = new ArrayList<>();
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = wearer.getItemBySlot(slot);
            if (isNetherite(stack)) {
                worn.add(stack);
            }
        }
        for (ItemStack stack : worn) {
            CompoundTag tag = stack.getOrCreateTagElement(KEY);
            // o multiplicador da propria peca multiplica o que ela guarda, entao guardamos o valor dividido
            int pieceMult = Math.max(1, com.netheritesupremo.enchant.ModEnchantments.multiplierOf(stack));
            if (addArmor > 0.0D) {
                tag.putDouble(ARMOR, tag.getDouble(ARMOR) + addArmor / worn.size() / pieceMult);
            }
            if (addTough > 0.0D) {
                tag.putDouble(TOUGHNESS, tag.getDouble(TOUGHNESS) + addTough / worn.size() / pieceMult);
            }
            if (upResist) {
                tag.putInt(RESIST, storedResist);
            }
            if (upStrength) {
                tag.putInt(STRENGTH, storedStrength);
            }
        }
        wearer.getPersistentData().putLong(COOLDOWN, now + COOLDOWN_TICKS);
        if (wearer instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable("message.netheritesupremo.armor_adapted"), true);
        }
    }
}
