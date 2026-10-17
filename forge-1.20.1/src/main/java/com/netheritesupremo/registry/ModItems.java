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

    public static final RegistryObject<Item> BOMB_ROBOT_SPAWN_EGG = ITEMS.register(
            "netherite_bomb_robot_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.BOMB_ROBOT, 0x1A1A1A, 0xE01B1B, new Item.Properties()));

    public static final RegistryObject<Item> NETHERBOT_SPAWN_EGG = ITEMS.register(
            "netherbot_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.NETHERBOT, 0x2B2B2F, 0xFF8A1A, new Item.Properties()));

    private ModItems() {
    }
}
