package com.netheritesupremo.trade;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.item.MapItem;

public final class UmbralTrades {
    public static final String MODID = "netheritesupremo";
    public static final TagKey<Structure> UMBRAL_MAP_TARGET =
            TagKey.create(Registries.STRUCTURE, new ResourceLocation(MODID, "umbral_map"));

    private UmbralTrades() {
    }

    private static void add(MerchantOffers offers, int emeralds, ItemStack result, int uses) {
        offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, emeralds), result, uses, 30, 0.05F));
    }

    private static ItemStack book(Enchantment e, int level) {
        return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(e, level));
    }

    /** Item com encantamentos: pares (Enchantment, nivel). */
    private static ItemStack gear(Item item, Object... pairs) {
        ItemStack stack = new ItemStack(item);
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            stack.enchant((Enchantment) pairs[i], (Integer) pairs[i + 1]);
        }
        return stack;
    }

    /** Trocas do Mercador Umbral: as melhores possiveis, sem netherite (so o peitoral). */
    public static void addMerchantTrades(MerchantOffers o) {
        // Livros encantados
        add(o, 32, book(Enchantments.MENDING, 1), 8);
        add(o, 16, book(Enchantments.UNBREAKING, 3), 16);
        add(o, 24, book(Enchantments.ALL_DAMAGE_PROTECTION, 4), 16);
        add(o, 24, book(Enchantments.SHARPNESS, 5), 16);
        add(o, 24, book(Enchantments.BLOCK_EFFICIENCY, 5), 16);
        add(o, 24, book(Enchantments.BLOCK_FORTUNE, 3), 16);
        add(o, 24, book(Enchantments.MOB_LOOTING, 3), 16);
        add(o, 16, book(Enchantments.SILK_TOUCH, 1), 16);
        add(o, 24, book(Enchantments.POWER_ARROWS, 5), 16);
        add(o, 24, book(Enchantments.INFINITY_ARROWS, 1), 16);
        add(o, 16, book(Enchantments.FALL_PROTECTION, 4), 16);
        add(o, 16, book(Enchantments.THORNS, 3), 16);
        add(o, 16, book(Enchantments.DEPTH_STRIDER, 3), 16);
        add(o, 16, book(Enchantments.SWIFT_SNEAK, 3), 16);
        add(o, 16, book(Enchantments.SOUL_SPEED, 3), 16);
        add(o, 16, book(Enchantments.FIRE_ASPECT, 2), 16);
        add(o, 16, book(Enchantments.SWEEPING_EDGE, 3), 16);
        add(o, 16, book(Enchantments.QUICK_CHARGE, 3), 16);
        add(o, 16, book(Enchantments.PIERCING, 4), 16);

        // Equipamentos de diamante totalmente encantados
        add(o, 40, gear(Items.DIAMOND_HELMET, Enchantments.ALL_DAMAGE_PROTECTION, 4, Enchantments.UNBREAKING, 3,
                Enchantments.MENDING, 1, Enchantments.RESPIRATION, 3, Enchantments.AQUA_AFFINITY, 1), 8);
        add(o, 40, gear(Items.DIAMOND_CHESTPLATE, Enchantments.ALL_DAMAGE_PROTECTION, 4, Enchantments.UNBREAKING, 3,
                Enchantments.MENDING, 1, Enchantments.THORNS, 3), 8);
        add(o, 40, gear(Items.DIAMOND_LEGGINGS, Enchantments.ALL_DAMAGE_PROTECTION, 4, Enchantments.UNBREAKING, 3,
                Enchantments.MENDING, 1, Enchantments.SWIFT_SNEAK, 3), 8);
        add(o, 40, gear(Items.DIAMOND_BOOTS, Enchantments.ALL_DAMAGE_PROTECTION, 4, Enchantments.UNBREAKING, 3,
                Enchantments.MENDING, 1, Enchantments.FALL_PROTECTION, 4, Enchantments.DEPTH_STRIDER, 3), 8);
        add(o, 48, gear(Items.DIAMOND_SWORD, Enchantments.SHARPNESS, 5, Enchantments.UNBREAKING, 3,
                Enchantments.MENDING, 1, Enchantments.FIRE_ASPECT, 2, Enchantments.MOB_LOOTING, 3,
                Enchantments.SWEEPING_EDGE, 3), 8);
        add(o, 40, gear(Items.DIAMOND_PICKAXE, Enchantments.BLOCK_EFFICIENCY, 5, Enchantments.UNBREAKING, 3,
                Enchantments.MENDING, 1, Enchantments.BLOCK_FORTUNE, 3), 8);
        add(o, 40, gear(Items.DIAMOND_AXE, Enchantments.BLOCK_EFFICIENCY, 5, Enchantments.SHARPNESS, 5,
                Enchantments.UNBREAKING, 3, Enchantments.MENDING, 1, Enchantments.BLOCK_FORTUNE, 3), 8);
        add(o, 24, gear(Items.DIAMOND_SHOVEL, Enchantments.BLOCK_EFFICIENCY, 5, Enchantments.UNBREAKING, 3,
                Enchantments.MENDING, 1, Enchantments.BLOCK_FORTUNE, 3), 8);
        add(o, 24, gear(Items.DIAMOND_HOE, Enchantments.BLOCK_EFFICIENCY, 5, Enchantments.UNBREAKING, 3,
                Enchantments.MENDING, 1, Enchantments.BLOCK_FORTUNE, 3), 8);
        add(o, 40, gear(Items.BOW, Enchantments.POWER_ARROWS, 5, Enchantments.PUNCH_ARROWS, 2,
                Enchantments.FLAMING_ARROWS, 1, Enchantments.INFINITY_ARROWS, 1, Enchantments.UNBREAKING, 3), 8);
        add(o, 40, gear(Items.CROSSBOW, Enchantments.QUICK_CHARGE, 3, Enchantments.PIERCING, 4,
                Enchantments.UNBREAKING, 3, Enchantments.MENDING, 1), 8);
        add(o, 40, gear(Items.TRIDENT, Enchantments.LOYALTY, 3, Enchantments.IMPALING, 5,
                Enchantments.UNBREAKING, 3, Enchantments.MENDING, 1), 8);
        add(o, 24, gear(Items.SHIELD, Enchantments.UNBREAKING, 3, Enchantments.MENDING, 1), 8);
        add(o, 24, gear(Items.FISHING_ROD, Enchantments.FISHING_LUCK, 3, Enchantments.FISHING_SPEED, 3,
                Enchantments.UNBREAKING, 3, Enchantments.MENDING, 1), 8);
        add(o, 48, gear(Items.ELYTRA, Enchantments.UNBREAKING, 3, Enchantments.MENDING, 1), 4);

        // O unico item de netherite a venda: o peitoral
        add(o, 64, gear(Items.NETHERITE_CHESTPLATE, Enchantments.ALL_DAMAGE_PROTECTION, 4, Enchantments.UNBREAKING, 3,
                Enchantments.MENDING, 1, Enchantments.THORNS, 3), 3);

        // Diversos
        add(o, 32, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), 8);
        add(o, 32, new ItemStack(Items.TOTEM_OF_UNDYING), 8);
        add(o, 6, new ItemStack(Items.GOLDEN_APPLE, 2), 16);
        add(o, 8, new ItemStack(Items.DIAMOND, 4), 32);
        add(o, 4, new ItemStack(Items.EXPERIENCE_BOTTLE, 8), 32);
        add(o, 5, new ItemStack(Items.ENDER_PEARL, 4), 32);
        add(o, 8, new ItemStack(Items.OBSIDIAN, 16), 16);
        add(o, 24, new ItemStack(Items.DIAMOND_HORSE_ARMOR), 4);
        add(o, 8, new ItemStack(Items.SADDLE), 4);
        add(o, 6, new ItemStack(Items.NAME_TAG), 8);
        add(o, 20, new ItemStack(Items.SHULKER_BOX), 4);
        add(o, 64, new ItemStack(Items.BEACON), 2);
    }

    /**
     * Melhora o mercador viajante: precos pela metade nas trocas normais, trocas boas extras
     * e o Mapa Umbral (64 esmeraldas) que leva a Vila Umbral mais proxima.
     */
    public static void improveWanderingTrader(WanderingTrader trader, ServerLevel level) {
        MerchantOffers offers = trader.getOffers();
        for (MerchantOffer offer : offers) {
            offer.addToSpecialPriceDiff(-(offer.getBaseCostA().getCount() / 2));
        }
        add(offers, 30, book(Enchantments.MENDING, 1), 4);
        add(offers, 20, book(Enchantments.ALL_DAMAGE_PROTECTION, 4), 4);
        add(offers, 20, book(Enchantments.SHARPNESS, 5), 4);
        add(offers, 20, book(Enchantments.BLOCK_EFFICIENCY, 5), 4);
        add(offers, 12, book(Enchantments.UNBREAKING, 3), 4);
        add(offers, 6, new ItemStack(Items.DIAMOND, 3), 8);
        add(offers, 40, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), 2);
        add(offers, 40, new ItemStack(Items.TOTEM_OF_UNDYING), 2);
        add(offers, 4, new ItemStack(Items.EXPERIENCE_BOTTLE, 8), 8);

        ItemStack map = createUmbralMap(level, trader.blockPosition());
        if (map != null) {
            offers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 64), map, 1, 30, 0.0F));
        }
    }

    /** Mapa que aponta para a estrutura Umbral mais proxima (ou null se nao achar). */
    public static ItemStack createUmbralMap(ServerLevel level, BlockPos from) {
        BlockPos target = level.findNearestMapStructure(UMBRAL_MAP_TARGET, from, 100, true);
        if (target == null) {
            return null;
        }
        ItemStack map = MapItem.create(level, target.getX(), target.getZ(), (byte) 2, true, true);
        MapItem.renderBiomePreviewMap(level, map);
        MapItemSavedData.addTargetDecoration(map, target, "+", MapDecoration.Type.TARGET_X);
        map.setHoverName(Component.translatable("item." + MODID + ".umbral_map"));
        return map;
    }
}
