package com.netheritesupremo.registry;

import com.netheritesupremo.NetheriteSupremo;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NetheriteSupremo.MODID);

    public static final RegistryObject<Item> NETHERITE_GOLEM_SPAWN_EGG = ITEMS.register(
            "netherite_golem_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.NETHERITE_GOLEM, 0x2B2326, 0xFF6A00, new Item.Properties()));

    public static final RegistryObject<Item> NETHERITE_KNIGHT_SPAWN_EGG = ITEMS.register(
            "netherite_knight_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.NETHERITE_KNIGHT, 0x1E1B1D, 0xB04A1A, new Item.Properties()));

    public static final RegistryObject<Item> NECROMANCER_SPAWN_EGG = ITEMS.register(
            "necromancer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.NECROMANCER, 0x14121C, 0x7B4FBF, new Item.Properties()));

    public static final RegistryObject<Item> NETHER_SPIDER_SPAWN_EGG = ITEMS.register(
            "nether_spider_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.NETHER_SPIDER, 0x3B0F0F, 0xFF7A1A, new Item.Properties()));

    public static final RegistryObject<Item> GOLDEN_ZOMBIE_SPAWN_EGG = ITEMS.register(
            "golden_zombie_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GOLDEN_ZOMBIE, 0xF2C230, 0x8A5A00, new Item.Properties()));

    public static final RegistryObject<Item> UMBRAL_MERCHANT_SPAWN_EGG = ITEMS.register(
            "umbral_merchant_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.UMBRAL_MERCHANT, 0x2A1B4D, 0xC9A227, new Item.Properties()));

    public static final RegistryObject<Item> BOMB_ROBOT_SPAWN_EGG = ITEMS.register(
            "netherite_bomb_robot_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.BOMB_ROBOT, 0x1A1A1A, 0xE01B1B, new Item.Properties()));

    public static final RegistryObject<Item> NETHERBOT_SPAWN_EGG = ITEMS.register(
            "netherbot_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.NETHERBOT, 0x2B2B2F, 0xFF8A1A, new Item.Properties()));

    public static final RegistryObject<Item> RAINBOW_BOSS_SPAWN_EGG = ITEMS.register(
            "netherite_robot_rainbow_boss_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.RAINBOW_BOSS, 0xFF3B3B, 0x3BD9FF, new Item.Properties()));

    public static final RegistryObject<Item> RAINBOW_GEM = ITEMS.register(
            "rainbow_gem",
            () -> new com.netheritesupremo.item.RainbowGemItem(new Item.Properties().stacksTo(1).fireResistant()
                    .rarity(net.minecraft.world.item.Rarity.EPIC)));

    public static final RegistryObject<Item> PRECURSOR_SPAWN_EGG = ITEMS.register(
            "precursor_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PRECURSOR, 0x2A1F5C, 0x35E0D0, new Item.Properties()));

    public static final RegistryObject<Item> PRECURSOR_CORE = ITEMS.register(
            "precursor_core",
            () -> new com.netheritesupremo.item.PrecursorCoreItem(new Item.Properties().stacksTo(1).fireResistant()
                    .rarity(net.minecraft.world.item.Rarity.EPIC)));

    public static final RegistryObject<Item> GIANT_GLOW_SQUID_SPAWN_EGG = ITEMS.register(
            "giant_glow_squid_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GIANT_GLOW_SQUID, 0x0B3B4A, 0x7CF5E0, new Item.Properties()));

    public static final RegistryObject<Item> GIANT_GLOW_SQUID_EGG = ITEMS.register(
            "giant_glow_squid_egg",
            () -> new net.minecraft.world.item.BlockItem(ModBlocks.GIANT_GLOW_SQUID_EGG.get(),
                    new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));

    public static final RegistryObject<Item> ROBOT_SPAWN_EGG = ITEMS.register(
            "robot_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.ROBOT, 0x1C2A33, 0x2BD9FF, new Item.Properties()));

    private ModItems() {
    }
}
