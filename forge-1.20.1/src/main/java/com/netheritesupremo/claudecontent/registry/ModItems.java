package com.netheritesupremo.claudecontent.registry;

import com.netheritesupremo.claudecontent.ClaudeContent;
import com.netheritesupremo.claudecontent.item.AshSigilItem;
import com.netheritesupremo.claudecontent.item.CinderArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ClaudeContent.MODID);

    // Materiais
    public static final RegistryObject<Item> CINDER_SCALE = ITEMS.register("cinder_scale",
            () -> new Item(new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> ROOTED_CORE = ITEMS.register("rooted_core",
            () -> new Item(new Item.Properties().stacksTo(16).rarity(Rarity.EPIC)));
    // EXTRA: item de ritual que invoca o boss
    public static final RegistryObject<Item> ASH_SIGIL = ITEMS.register("ash_sigil",
            () -> new AshSigilItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

    // ARMADURA: Armadura de Brasa
    public static final RegistryObject<Item> CINDER_HELMET = ITEMS.register("cinder_helmet",
            () -> new CinderArmorItem(ArmorItem.Type.HELMET, new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> CINDER_CHESTPLATE = ITEMS.register("cinder_chestplate",
            () -> new CinderArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> CINDER_LEGGINGS = ITEMS.register("cinder_leggings",
            () -> new CinderArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> CINDER_BOOTS = ITEMS.register("cinder_boots",
            () -> new CinderArmorItem(ArmorItem.Type.BOOTS, new Item.Properties().rarity(Rarity.RARE).fireResistant()));

    // Ovos de invocação
    public static final RegistryObject<Item> ASH_REAPER_SPAWN_EGG = ITEMS.register("ash_reaper_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.ASH_REAPER, 0x2A2A2E, 0xFF6A00, new Item.Properties()));
    public static final RegistryObject<Item> MOSSLING_SPAWN_EGG = ITEMS.register("mossling_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MOSSLING, 0x5E8C3A, 0xE8A0C0, new Item.Properties()));
    public static final RegistryObject<Item> CINDER_STALKER_SPAWN_EGG = ITEMS.register("cinder_stalker_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.CINDER_STALKER, 0x1E1B1B, 0xFF9B1A, new Item.Properties()));
    public static final RegistryObject<Item> MOSS_COLOSSUS_SPAWN_EGG = ITEMS.register("moss_colossus_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MOSS_COLOSSUS, 0x40543A, 0x9BCB64, new Item.Properties()));
    public static final RegistryObject<Item> LUMILEU_SPAWN_EGG = ITEMS.register("lumileu_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.LUMILEU, 0x54764A, 0xE6D982, new Item.Properties()));

    private ModItems() {}
}
