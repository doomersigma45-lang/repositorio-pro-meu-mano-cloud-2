/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Rarity
 *  net.minecraftforge.common.ForgeSpawnEggItem
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package com.claudecontent.registry;

import com.claudecontent.item.AshSigilItem;
import com.claudecontent.item.CinderArmorItem;
import com.claudecontent.registry.ModEntities;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"claude_content");
    public static final RegistryObject<Item> CINDER_SCALE = ITEMS.register("cinder_scale", () -> new Item(new Item.Properties().m_41486_()));
    public static final RegistryObject<Item> ROOTED_CORE = ITEMS.register("rooted_core", () -> new Item(new Item.Properties().m_41487_(16).m_41497_(Rarity.EPIC)));
    public static final RegistryObject<Item> ASH_SIGIL = ITEMS.register("ash_sigil", () -> new AshSigilItem(new Item.Properties().m_41487_(1).m_41497_(Rarity.EPIC).m_41486_()));
    public static final RegistryObject<Item> CINDER_HELMET = ITEMS.register("cinder_helmet", () -> new CinderArmorItem(ArmorItem.Type.HELMET, new Item.Properties().m_41497_(Rarity.RARE).m_41486_()));
    public static final RegistryObject<Item> CINDER_CHESTPLATE = ITEMS.register("cinder_chestplate", () -> new CinderArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties().m_41497_(Rarity.RARE).m_41486_()));
    public static final RegistryObject<Item> CINDER_LEGGINGS = ITEMS.register("cinder_leggings", () -> new CinderArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties().m_41497_(Rarity.RARE).m_41486_()));
    public static final RegistryObject<Item> CINDER_BOOTS = ITEMS.register("cinder_boots", () -> new CinderArmorItem(ArmorItem.Type.BOOTS, new Item.Properties().m_41497_(Rarity.RARE).m_41486_()));
    public static final RegistryObject<Item> ASH_REAPER_SPAWN_EGG = ITEMS.register("ash_reaper_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.ASH_REAPER, 0x2A2A2E, 16738816, new Item.Properties()));
    public static final RegistryObject<Item> MOSSLING_SPAWN_EGG = ITEMS.register("mossling_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.MOSSLING, 6196282, 15245504, new Item.Properties()));
    public static final RegistryObject<Item> CINDER_STALKER_SPAWN_EGG = ITEMS.register("cinder_stalker_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.CINDER_STALKER, 0x1E1B1B, 16751386, new Item.Properties()));
    public static final RegistryObject<Item> MOSS_COLOSSUS_SPAWN_EGG = ITEMS.register("moss_colossus_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.MOSS_COLOSSUS, 4215866, 10210148, new Item.Properties()));
    public static final RegistryObject<Item> LUMILEU_SPAWN_EGG = ITEMS.register("lumileu_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.LUMILEU, 5535306, 15128962, new Item.Properties()));

    private ModItems() {
    }
}

